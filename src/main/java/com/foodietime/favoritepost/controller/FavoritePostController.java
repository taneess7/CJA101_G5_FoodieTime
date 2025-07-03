package com.foodietime.favoritepost.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.favoritepost.model.FavoritePostRepository;
import com.foodietime.favoritepost.model.FavoritePostService;
import com.foodietime.favoritepost.model.FavoritePostVO;
import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryService;
import com.foodietime.postcategory.model.PostCategoryVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/FavoritePost")
public class FavoritePostController {

	@Autowired
	FavoritePostService favoritePostService;

	@Autowired
	FavoritePostRepository favoritePostRepository;

	@Autowired
	MemService memservice;

	@Autowired
	PostService postservice;
	
	@Autowired
	PostCategoryService postCategoryService;

	@PostMapping("/insert")
	public String insert(@RequestParam("postId") Integer postId, HttpSession session) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		
		// 取得貼文ID
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
	    if (loginMember == null) {
	        return "redirect:/front/member/login";
	    }
	    PostVO post = postservice.getOnePost(postId);
	    favoritePostService.addFavoritePost(post, loginMember);// 這裡組合複合主鍵
		/*************************** 2.開始新增資料 *****************************************/
//		favoritePostService.addFavoritePost(favoritePostVO.getPost(), favoritePostVO.getMember());
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<FavoritePostVO> list = favoritePostService.getAll();
		
		// 設定成功訊息到 session
		session.setAttribute("reportSuccess", "收藏成功！");

		return "redirect:/post/one?postId=" + postId;
	}
	
	@PostMapping("/delete")
	public String delete(@RequestParam("postId") Integer postId, HttpSession session) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		
		// 取得貼文ID
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
	    if (loginMember == null) {
	        return "redirect:/front/member/login";
	    }
	    favoritePostService.deleteFavoritePost(postId, loginMember.getMemId());
	    session.setAttribute("reportSuccess", "已取消收藏！");
	    return "redirect:/post/one?postId=" + postId;
	}

	@RequestMapping("/my")
	public String myFavoritePosts(ModelMap model, HttpSession session,HttpServletRequest request) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			return "redirect:/front/member/login";
		}
		// 用 Criteria 查詢
		List<PostVO> favoritePosts = favoritePostService.findFavoritePostsByMemberIdCriteria(loginMember.getMemId());
		model.addAttribute("threads", favoritePosts);

		// 補這一行：查詢所有分類
	    List<PostCategoryVO> category = postCategoryService.getAll();
	    model.addAttribute("category", category);
	    model.addAttribute("currentCategory", null); // 收藏時不屬於任何分類
	    model.addAttribute("isFavoritePage", true);
	    model.addAttribute("isMyPostsPage", false);
	    model.addAttribute("currentPath", request.getRequestURI()); // 傳遞當前路徑
		return "front/post/listallpost";
	}
}
