package com.foodietime.memfavlist.controller;

import com.foodietime.member.model.MemberVO;
import com.foodietime.memfavlist.model.FavoriteListService;
import com.foodietime.memfavlist.model.FavoriteListVO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreVO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/favorite")
public class FavoriteListController {

	private final FavoriteListService favoriteListService;

	@Autowired
	public FavoriteListController(FavoriteListService favoriteListService) {
		this.favoriteListService = favoriteListService;
	}
	
	@GetMapping("/member-favorites")
	public String showMemberFavorites(HttpSession session, HttpServletResponse response,Model model) {
		
		// 禁止快取
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
	    MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	    if (member == null) {
	        return "redirect:/category/login";
	    }
		model.addAttribute("memberVO", member);
	    List<FavoriteListVO> favoriteList = favoriteListService.getFavoritesByMemId(member.getMemId());
	    
	    // 將商品依店家分組
	    Map<StoreVO, List<ProductVO>> groupedFavorites = new LinkedHashMap<>();
	    for (FavoriteListVO fav : favoriteList) {
	        StoreVO store = fav.getProduct().getStore();
	        groupedFavorites.computeIfAbsent(store, k -> new ArrayList<>()).add(fav.getProduct());
	    }

	    // 圖片對應表
	    Map<Integer, String> productImageMap = new HashMap<>();
	    for (FavoriteListVO fav : favoriteList) {
	        byte[] photo = fav.getProduct().getProdPhoto();
	        if (photo != null && photo.length > 0) {
	            String base64 = Base64.getEncoder().encodeToString(photo);
	            productImageMap.put(fav.getProduct().getProdId(), "data:image/jpeg;base64," + base64);
	        } else {
	            productImageMap.put(fav.getProduct().getProdId(), "/images/restaurant/placeholder.svg");
	        }
	    }

	    model.addAttribute("groupedFavorites", groupedFavorites);
	    model.addAttribute("productImageMap", productImageMap);
	    return "/front/favoritelist/member-favorites";
	}
	
	// 收藏餐廳 API，從 JS 發送 prodId，session 取 memId
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<?> addFavorite(@RequestBody Map<String, Integer> payload, HttpSession session) {
	    MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	    if (member == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "請先登入會員"));
	    }

	    Integer memId = member.getMemId();
	    Integer prodId = payload.get("prodId");

	    try {
	        favoriteListService.addFavoriteList(memId, prodId);
	        return ResponseEntity.ok(Map.of("status", "success"));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "加入失敗"));
	    }
	}

	@PostMapping("/remove")
	@ResponseBody
	public ResponseEntity<?> removeFavorite(@RequestBody Map<String, Integer> payload, HttpSession session) {
	    MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	    if (member == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "請先登入會員"));
	    }

	    Integer memId = member.getMemId();
	    Integer prodId = payload.get("prodId");

	    try {
	        favoriteListService.deleteFavorite(memId, prodId);
	        return ResponseEntity.ok(Map.of("status", "removed"));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "移除失敗"));
	    }
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session, HttpServletResponse response) {
	    session.invalidate(); // 清除登入資訊

	    // 禁止快取
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	    response.setHeader("Pragma", "no-cache");
	    response.setDateHeader("Expires", 0);

	    return "redirect:/category"; // 登出後返回首頁或分類頁
	}
//	// 主頁
//    @GetMapping("/select_page")
//    public String index() {
//        return "favorite/select_page";
//    }
//
//    // 查詢單筆收藏（by memId & prodId）
//    @PostMapping("/searchByMemProd")
//    public String searchFavorite(@RequestParam Integer memId,
//                                 @RequestParam Integer prodId,
//                                 Model model) {
//    	FavoriteListVO vo = favoriteListService.getOneFavorite(memId, prodId);
//        model.addAttribute("favoriteVO", vo);
//        return "favorite/listOneFavorite";
//    }
//
//    //查詢單一會員所有收藏
//    @PostMapping("/searchByMem")
//    public String searchFavoritesByMem(@RequestParam Integer memId, Model model) {
//        List<FavoriteListVO> favorites = favoriteListService.getFavoritesByMemId(memId);
//        if (favorites.isEmpty()) {
//            model.addAttribute("errorMsgs", Arrays.asList("查無會員收藏資料"));
//            return "favorite/select_page";
//        }
//        model.addAttribute("favoriteList", favorites);
//        return "favorite/listAllFavorite";
//    }
//
//    // 新增收藏
//    @GetMapping("/add")
//    public String showAddForm(Model model) {
//        model.addAttribute("favoriteVO", new FavoriteListVO());
//        return "favorite/addFavorite";
//    }
//
//    
//
//    // 查詢所有收藏
//    @GetMapping("/listAll")
//    public String getAllFavorites(Model model) {
//        List<FavoriteListVO> list = favoriteListService.getAll();
//        model.addAttribute("favoriteList", list);
//        return "favorite/listAllFavorite";
//    }
//
//    // 刪除單筆收藏（by memId & prodId）
//    @PostMapping("/delete")
//    public String deleteFavorite(@RequestParam Integer memId,
//                                 @RequestParam Integer prodId,
//                                 Model model) {
//        favoriteListService.deleteFavorite(memId, prodId);
//        List<FavoriteListVO> list = favoriteListService.getAll(); // 更新列表
//        model.addAttribute("favoriteList", list);
//        return "favorite/listAllFavorite";
//    }
//
//    // 刪除某會員所有收藏
//    @PostMapping("/deleteAllByMem")
//    public String deleteAllByMem(@RequestParam Integer memId, Model model) {
//        favoriteListService.deleteAllFavoritesByMem(memId);
//        List<FavoriteListVO> list = favoriteListService.getAll(); // 更新列表
//        model.addAttribute("favoriteList", list);
//        return "favorite/listAllFavorite";
//    }
}
