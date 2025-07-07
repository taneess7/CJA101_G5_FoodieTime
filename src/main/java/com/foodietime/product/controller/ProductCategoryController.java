package com.foodietime.product.controller;

import com.foodietime.coupon.model.CouponService;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.memcoupon.model.MemCouponService;
import com.foodietime.memfavlist.model.FavoriteListService;
import com.foodietime.memfavlist.model.FavoriteListVO;
import com.foodietime.product.dto.ProductCardDTO;
import com.foodietime.product.model.ProductCategoryService;
import com.foodietime.product.model.ProductCategoryVO;
import com.foodietime.product.model.ProductService;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/category")
public class ProductCategoryController {

	@Autowired
    private ProductCategoryService categoryService;
	@Autowired
	private ProductService productService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private FavoriteListService favoriteListService;
	@Autowired
	private StoreService storeService;
    @Autowired
    private MemCouponService memCouponService;

    // 查全部
    @GetMapping("/food-categories")
    public String listAll(HttpServletResponse response) {   
    	
    	//禁止快取
    	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        return "/front/restaurant/food-categories";
    }
    
    //跳回首頁
    @GetMapping("/index")
    public String listindex(HttpServletResponse response) {
    	
    	//禁止快取
    	response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        
    	return "/index";
    }
    
    //跳到會員登入頁面
    @GetMapping("/login")
    public String listlogin() {
    	return "/front/member/login";
    }

    // 收藏商品 ID 清單（要用於判斷哪些商品愛心亮起）
    private void setFavoriteProducts(MemberVO memberVO, Model model) {
        if (memberVO != null) {
            List<FavoriteListVO> favorites = favoriteListService.getFavoritesByMemId(memberVO.getMemId());
            Set<Integer> favoriteProdIds = favorites.stream()
                    .map(FavoriteListVO::getProdId)
                    .collect(Collectors.toSet());
            model.addAttribute("favoriteProdIds", favoriteProdIds);
        }
    }
    
    @GetMapping("/{cateId}")
    public String showCategoryPage(@PathVariable Integer cateId,
                                   HttpSession session,
                                   HttpServletResponse response,
                                   Model model) {

        // ==================== 1. 前置作業 (維持不變) ====================
        // 禁止快取
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 處理會員登入狀態、收藏、已領取優惠券等資訊
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO != null) {
            model.addAttribute("member", memberVO);
            setFavoriteProducts(memberVO, model);
            
            Set<Integer> claimedCouponIds = memCouponService.getClaimedCouponIdsByMemberId(memberVO.getMemId());
            model.addAttribute("claimedCouponIds", claimedCouponIds);
        } else {
            model.addAttribute("claimedCouponIds", Collections.emptySet());
        }

        // ==================== 2. 獲取分類與店家基礎資料 (維持不變) ====================
        ProductCategoryVO categoryVO = categoryService.findById(cateId);
        model.addAttribute("categoryName", categoryVO.getProdCate());
        model.addAttribute("isSearchPage", false); // 明確標記為非搜尋頁
        
        List<StoreVO> storeList = categoryService.getStoresByCategoryId(cateId);
        model.addAttribute("storeList", storeList);
        model.addAttribute("categoryId", cateId);


        // ==================== 3. ★★★【核心重構】高效能資料整合 ★★★ ====================
        // 建立三個 Map，用於存放按店家ID分類的 DTO、圖片和優惠券
        Map<Integer, List<ProductCardDTO>> storeProductCardMap = new HashMap<>();
        Map<Integer, String> storeImageMap = new HashMap<>();
        Map<Integer, List<CouponVO>> storeCouponMap = new HashMap<>();

        // 【優化】使用單一迴圈處理所有與店家相關的資料查詢
        for (StoreVO store : storeList) {
            Integer storeId = store.getStorId();

            // a. 【高效能】呼叫新的 DTO 查詢方法，獲取輕量級商品資料
            List<ProductCardDTO> productCards = productService.getProductCardsByStoreId(storeId);
            storeProductCardMap.put(storeId, productCards);

            // b. 處理店家圖片
            byte[] imageBytes = store.getStorPhoto();
            if (imageBytes != null && imageBytes.length > 0) {
                storeImageMap.put(storeId, Base64.getEncoder().encodeToString(imageBytes));
            }

            // c. 處理店家優惠券
            List<CouponVO> coupons = couponService.getCouponsByStorId(storeId);
            storeCouponMap.put(storeId, coupons);
        }

        // 將整合好的 Map 傳遞給前端
        model.addAttribute("storeProductCardMap", storeProductCardMap);
        model.addAttribute("storeImageMap", storeImageMap);
        model.addAttribute("storeCouponMap", storeCouponMap);

        // 【移除】不再需要傳遞沉重的、未經組織的 ProductVO 列表
        // model.addAttribute("productList", allProducts);


        // ==================== 4. 其他輔助資料 (維持不變) ====================
        Map<String, String> weekMap = Map.of(
                "0", "週日", "1", "週一", "2", "週二", "3", "週三",
                "4", "週四", "5", "週五", "6", "週六"
        );
        model.addAttribute("weekMap", weekMap);
        model.addAttribute("serverTime", new java.sql.Timestamp(System.currentTimeMillis()));

        return "front/restaurant/category";
    }
    
    // 餐廳首頁的模糊搜尋
    @GetMapping("/search")
    public String searchByKeyword(@RequestParam String keyword,
                                  Model model,
                                  HttpServletResponse response,
                                  HttpSession session) {

    	// 🔒 若關鍵字為空，直接導回分類總覽頁
    	if (keyword == null || keyword.isBlank()) {
    	    return "redirect:/category/food-categories";
    	}
    	
    	// 禁止快取
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // ===== claimedCouponIds 是會員已經領取過的優惠券 ID 清單。============================================= //
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO != null) {
            model.addAttribute("member", memberVO);
            setFavoriteProducts(memberVO, model);

            Set<Integer> claimedCouponIds = memCouponService.getClaimedCouponIdsByMemberId(memberVO.getMemId());
            model.addAttribute("claimedCouponIds", claimedCouponIds);
        } else {
            model.addAttribute("claimedCouponIds", Collections.emptySet());
        }
        // ======================================================================================================== //
        model.addAttribute("keyword", keyword);
        model.addAttribute("isSearchPage", true);
        model.addAttribute("categoryName", null);
        
        // 1. 搜尋商品與店家
        List<StoreVO> storeList = storeService.searchStores(keyword);
        List<ProductVO> productList = productService.searchProductsByKeyword(keyword);

        Set<StoreVO> productStoreSet = productList.stream()
                .map(ProductVO::getStore)
                .collect(Collectors.toSet());

        Set<StoreVO> combinedStores = new HashSet<>(storeList);
        combinedStores.addAll(productStoreSet);
        List<StoreVO> finalStoreList = new ArrayList<>(combinedStores);
        model.addAttribute("storeList", finalStoreList);
        model.addAttribute("productList", productList != null ? productList : new ArrayList<>());

        // ✅ 判斷是否為空結果（前端會用來顯示查無結果訊息）
        boolean isEmptyResult = finalStoreList.isEmpty();
        model.addAttribute("isEmptyResult", isEmptyResult);
        
        // 2. 店家圖片轉 base64
        Map<Integer, String> storeImageMap = new HashMap<>();
        for (StoreVO store : finalStoreList) {
            byte[] imageBytes = store.getStorPhoto();
            if (imageBytes != null && imageBytes.length > 0) {
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                storeImageMap.put(store.getStorId(), base64);
            }
        }
        model.addAttribute("storeImageMap", storeImageMap);

        // 3. 店家商品 Map (DTO)
        Map<Integer, List<ProductCardDTO>> storeProductCardMap = new HashMap<>();
        for (StoreVO store : finalStoreList) {
            List<ProductCardDTO> cards = productService.getProductCardsByStoreId(store.getStorId());
            storeProductCardMap.put(store.getStorId(), cards);
        }
        model.addAttribute("storeProductCardMap", storeProductCardMap);

        // 4. 店家優惠券 Map
        Map<Integer, List<CouponVO>> storeCouponMap = new HashMap<>();
        for (StoreVO store : finalStoreList) {
            List<CouponVO> coupons = couponService.getCouponsByStorId(store.getStorId());
            storeCouponMap.put(store.getStorId(), coupons);
        }
        model.addAttribute("storeCouponMap", storeCouponMap);

        // 5. 星期對照表（必要才加）
        Map<String, String> weekMap = Map.of(
                "0", "週日",
                "1", "週一",
                "2", "週二",
                "3", "週三",
                "4", "週四",
                "5", "週五",
                "6", "週六"
        );
        model.addAttribute("weekMap", weekMap);

        model.addAttribute("serverTime", new java.sql.Timestamp(System.currentTimeMillis()));
        
        return "front/restaurant/category";
    }
    
    
    //會員收藏清單(查看餐廳)
    @GetMapping("/store/{storeId}")
    public String viewStoreDetail(@PathVariable Integer storeId, Model model, HttpServletResponse response,HttpSession session) {
    	
    	// 禁止快取
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // ✅ 加這一行，讓前端正確判斷是否顯示黃色 header 區塊
        model.addAttribute("isSearchPage", false);
        
        StoreVO store = storeService.getOneStore(storeId);
        List<CouponVO> couponList = storeService.getCouponsByStore(storeId);

        // ✅ 補上店名當作 categoryName（讓 category.html 標題有內容）
        model.addAttribute("categoryName", store.getStorName());
        
        // ✅ 包裝 storeList
        List<StoreVO> storeList = List.of(store);
        model.addAttribute("storeList", storeList);

        // ✅ 商品 Map(DTO)
        Map<Integer, List<ProductCardDTO>> storeProductCardMap = new HashMap<>();
        storeProductCardMap.put(storeId, productService.getProductCardsByStoreId(storeId));
        model.addAttribute("storeProductCardMap", storeProductCardMap);

        // ✅ 優惠券 Map
        Map<Integer, List<CouponVO>> storeCouponMap = new HashMap<>();
        storeCouponMap.put(storeId, couponList);
        model.addAttribute("storeCouponMap", storeCouponMap);

        // ✅ 圖片 Map
        Map<Integer, String> storeImageMap = new HashMap<>();
        byte[] imageBytes = store.getStorPhoto();
        if (imageBytes != null && imageBytes.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            storeImageMap.put(storeId, base64);
        }
        model.addAttribute("storeImageMap", storeImageMap);

        // ✅ weekMap
        Map<String, String> weekMap = Map.of(
                "0", "週日", "1", "週一", "2", "週二", "3", "週三",
                "4", "週四", "5", "週五", "6", "週六"
        );
        model.addAttribute("weekMap", weekMap);

     // ✅ 收藏商品 ID 清單（要用於判斷哪些商品愛心亮起）
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO != null) {
        	setFavoriteProducts(memberVO, model);    
        }
        
        model.addAttribute("serverTime", new java.sql.Timestamp(System.currentTimeMillis()));
        return "front/restaurant/category";
    }
    
    //模糊搜尋	
//  @GetMapping("")
//  public String searchCategory(@RequestParam(required = false) String keyword,
//                               Model model, HttpServletResponse response,HttpSession session) {
//
//  	// 禁止快取
//      response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//      response.setHeader("Pragma", "no-cache");
//      response.setDateHeader("Expires", 0);
//      
//      MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
//      if (memberVO != null) {
//          List<FavoriteListVO> favorites = favoriteListService.getFavoritesByMemId(memberVO.getMemId());
//          Set<Integer> favoriteProdIds = favorites.stream()
//              .map(FavoriteListVO::getProdId)
//              .collect(Collectors.toSet());
//          model.addAttribute("favoriteProdIds", favoriteProdIds);
//      }
//
//      if (keyword != null && !keyword.isBlank()) {
//          model.addAttribute("categoryName", "搜尋結果");
//
//          List<ProductVO> matchedProducts = productService.searchProductsByKeyword(keyword);
//          List<StoreVO> matchedStores = categoryService.searchStoresByKeyword(keyword);
//
//          Set<StoreVO> allStores = new HashSet<>(matchedStores);
//          for (ProductVO p : matchedProducts) {
//              allStores.add(p.getStore());
//          }
//
//          Map<String, String> weekMap = Map.of(
//                  "0", "週日",
//                  "1", "週一",
//                  "2", "週二",
//                  "3", "週三",
//                  "4", "週四",
//                  "5", "週五",
//                  "6", "週六"
//              );
//              model.addAttribute("weekMap", weekMap);
//              
//          // 這裡才可以處理圖片
//          Map<Integer, String> storeImageMap = new HashMap<>();
//          for (StoreVO store : allStores) {
//              byte[] imageBytes = store.getStorPhoto();
//              if (imageBytes != null && imageBytes.length > 0) {
//                  String base64 = Base64.getEncoder().encodeToString(imageBytes);
//                  storeImageMap.put(store.getStorId(), base64);
//              }
//          }
//          model.addAttribute("storeImageMap", storeImageMap);
//
//          // 商品 map
//          Map<Integer, List<ProductVO>> storeProductMap = new HashMap<>();
//          for (StoreVO store : allStores) {
//              List<ProductVO> allProds = productService.findByStoreId(store.getStorId());
//              storeProductMap.put(store.getStorId(), allProds);
//          }
//
//          // 優惠券 map
//          Map<Integer, List<CouponVO>> storeCouponMap = new HashMap<>();
//          for (StoreVO store : allStores) {
//              List<CouponVO> coupons = couponService.getCouponsByStorId(store.getStorId());
//              storeCouponMap.put(store.getStorId(), coupons);
//          }
//
//          model.addAttribute("storeList", new ArrayList<>(allStores));
//          model.addAttribute("storeProductMap", storeProductMap);
//          model.addAttribute("productList", matchedProducts);
//          model.addAttribute("storeCouponMap", storeCouponMap);
//
//      } else {
//          return "redirect:/category/food-categories";
//      }
//
//      return "/front/restaurant/category";
//  }
//    // 顯示新增頁面
//    @GetMapping("/add")
//    public String showAddForm(Model model) {
//        model.addAttribute("categoryVO", new ProductCategoryVO());
//        return "category/addCategory";
//    }

//    // 新增分類
//    @PostMapping("/add")
//    public String add(@RequestParam String prodCate, Model model) {
//        try {
//            ProductCategoryVO vo = categoryService.addCategory(prodCate);
//            model.addAttribute("categoryVO", vo);
//            return "category/listOneCategory";
//        } catch (RuntimeException e) {
//            model.addAttribute("errorMsg", e.getMessage());
//            return "category/addCategory";
//        }
//    }
//
//    // 顯示修改表單
//    @GetMapping("/edit")
//    public String showEditForm(@RequestParam Integer prodCateId, Model model) {
//        ProductCategoryVO vo = categoryService.getCategoryById(prodCateId);
//        model.addAttribute("categoryVO", vo);
//        return "category/editCategory";
//    }

    // 修改分類
//    @PostMapping("/edit")
//    public String update(@RequestParam Integer prodCateId,
//                         @RequestParam String prodCate,
//                         Model model) {
//        try {
//            ProductCategoryVO vo = categoryService.updateCategory(prodCateId, prodCate);
//            model.addAttribute("categoryVO", vo);
//            return "category/listOneCategory";
//        } catch (RuntimeException e) {
//            model.addAttribute("errorMsg", e.getMessage());
//            return "category/editCategory";
//        }
//    }
//
//    // 刪除分類
//    @PostMapping("/delete")
//    public String delete(@RequestParam Integer prodCateId, Model model) {
//        categoryService.deleteCategory(prodCateId);
//        return "redirect:/category/list";
//    }

}
    
