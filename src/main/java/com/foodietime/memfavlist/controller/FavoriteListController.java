package com.foodietime.memfavlist.controller;

import com.foodietime.memfavlist.model.FavoriteListService;
import com.foodietime.memfavlist.model.FavoriteListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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
	
	
	
	
//
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
//    @PostMapping("/add")
//    public String addFavorite(@RequestParam Integer memId,
//                              @RequestParam Integer prodId,
//                              Model model) {
//        FavoriteListVO favorite = favoriteListService.addFavoriteList(memId, prodId);
//        model.addAttribute("favoriteVO", favorite);
//        return "favorite/listOneFavorite";
//    }
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
