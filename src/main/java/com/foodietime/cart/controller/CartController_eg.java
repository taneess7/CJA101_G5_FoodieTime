//package com.foodietime.cart.controller;
//
//import com.foodietime.cart.model.CartService;
//import com.foodietime.cart.model.CartVO;
//import com.foodietime.member.model.MemberVO;
//import com.foodietime.product.model.ProductVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Controller
//@RequestMapping("/cart")
//public class CartController_eg {
//
//    private final CartService cartService;
//
//    @Autowired
//    public CartController_eg(CartService cartService) {
//        this.cartService = cartService;
//    }
//
//    @GetMapping("/select_page")
//    public String index(Model model) {
//        return "cart/select_page";
//    }
//    // 新增：處理搜尋功能
//    @PostMapping("/search")
//    public String searchCart(@RequestParam String searchType,
//                             @RequestParam(required = false) Integer shopId,
//                             @RequestParam(required = false) Integer memId,
//                             @RequestParam(required = false) Integer prodId,
//                             Model model) {
//
//        try {
//            CartVO cartVO = null;
//
//            switch (searchType) {
//                case "byShopId":
//                    if (shopId != null) {
//                        cartVO = cartService.getOneCart(shopId);
//                    }
//                    break;
//
//                case "byMemId":
//                    if (memId != null) {
//                        List<CartVO> cartList = cartService.getByMemId(memId);
//                        if (!cartList.isEmpty()) {
//                            cartVO = cartList.get(0); // 取第一筆資料
//                            if (cartList.size() > 1) {
//                                model.addAttribute("message",
//                                        "找到 " + cartList.size() + " 筆資料，顯示第一筆");
//                            }
//                        }
//                    }
//                    break;
//
//                case "byMemIdAndProdId":
//                    if (memId != null && prodId != null) {
//                        cartVO = cartService.getByMemIdAndProdId(memId, prodId);
//                    }
//                    break;
//
//                default:
//                    model.addAttribute("errorMsgs", Arrays.asList("無效的搜尋類型"));
//                    return "cart/select_page";
//            }
//
//            if (cartVO != null) {
//                model.addAttribute("cartVO", cartVO);
//                model.addAttribute("searchType", searchType);
//                return "cart/listOneCart"; // 使用現有的 listOneCart.html
//            } else {
//                model.addAttribute("errorMsgs", Arrays.asList("查無相關資料"));
//                return "cart/select_page";
//            }
//
//        } catch (Exception e) {
//            model.addAttribute("errorMsgs", Arrays.asList("搜尋失敗：" + e.getMessage()));
//            return "cart/select_page";
//        }
//    }
//
//    // 新增購物車商品（對應insert）
//    @GetMapping("/add")
//    public String showAddForm(Model model) {
//        model.addAttribute("cartVO", new CartVO());
//        return "cart/addCart";
//    }
//    @PostMapping("/add")
//    public String addCart(@RequestParam MemberVO memId,
//                          @RequestParam ProductVO prodId,
//                          @RequestParam Integer prodN,
//                          Model model) {
//        CartVO cartVO = cartService.addCart(memId, prodId, prodN);
//        model.addAttribute("cartVO", cartVO);
//        return "cart/listOneCart"; // 或 return "cart/listAllCart";
//    }
//
//    // 查詢單一購物車商品
//    @GetMapping("/{shopId}")
//    public String getOneCart(@PathVariable Integer shopId, Model model) {
//        CartVO cartVO = cartService.getOneCart(shopId);
//        model.addAttribute("cartVO", cartVO);
//        return "cart/listOneCart";
//    }
//
//    // 查詢所有
//    @GetMapping("/listAll")
//    public String getAll(Model model) {
//        List<CartVO> cartList = cartService.getAll();
//        model.addAttribute("cartList", cartList);
//        return "cart/listAllCart";
//    }
//
//    // 修改
//    @GetMapping("/update/{shopId}")  // 處理 GET 請求
//    public String showUpdateForm(@PathVariable Integer shopId, Model model) {
//        // 跳轉到修改頁面
//        CartVO cartVO = cartService.getOneCart(shopId);
//        model.addAttribute("cartVO", cartVO);
//        return "cart/update_cart_input";
//    }
//    @PostMapping("/update")
//    public String updateCart(@RequestParam Integer shopId,
//                             @RequestParam MemberVO memId,
//                             @RequestParam ProductVO prodId,
//                             @RequestParam Integer prodN,
//                             Model model) {
//        CartVO cartVO = cartService.updateCart(shopId, memId, prodId, prodN);
//        model.addAttribute("cartVO", cartVO);
//        return "cart/listOneCart";
//    }
//
//    // 刪除
//    @PostMapping("/delete")
//    public String deleteCart(@RequestParam Integer shopId) {
//        cartService.deleteCart(shopId);
//        return "cart/listAllCart";
//    }
//}
