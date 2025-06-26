package com.foodietime.memfavlist.controller;

import com.foodietime.member.model.MemberVO;
import com.foodietime.memfavlist.model.FavoriteListService;
import com.foodietime.memfavlist.model.FavoriteListVO;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
	public String index() {
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
