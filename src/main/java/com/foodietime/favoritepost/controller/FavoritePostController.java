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
	public String myFavoritePosts(@RequestParam(value = "sort", required = false, defaultValue = "editDate") String sort,
								  ModelMap model, HttpSession session, HttpServletRequest request) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			return "redirect:/front/member/login";
		}
		// 用 Criteria 查詢
		List<PostVO> allFavoritePosts = favoritePostService.findFavoritePostsByMemberIdCriteria(loginMember.getMemId());
		
		// 過濾掉已下架的貼文 (POST_STATUS = 2)
		List<PostVO> favoritePosts = allFavoritePosts.stream()
			.filter(post -> post.getPostStatus() != 2)
			.toList();
		
		// 排序處理
		List<PostVO> sortedPosts = sortPosts(new java.util.ArrayList<>(favoritePosts), sort);
		
		model.addAttribute("threads", sortedPosts);

		// 補這一行：查詢所有分類
	    List<PostCategoryVO> category = postCategoryService.getAll();
	    model.addAttribute("category", category);
	    model.addAttribute("currentCategory", null); // 收藏時不屬於任何分類
	    model.addAttribute("currentSort", sort);
	    model.addAttribute("isFavoritePage", true);
	    model.addAttribute("isMyPostsPage", false);
	    model.addAttribute("currentPath", request.getRequestURI()); // 傳遞當前路徑
		return "front/post/listallpost";
	}
	
	// 新增：按分類查詢收藏貼文
	@RequestMapping("/my/category")
	public String myFavoritePostsByCategory(@RequestParam("categoryId") Integer categoryId,
										   @RequestParam(value = "sort", required = false, defaultValue = "editDate") String sort,
										   ModelMap model, HttpSession session, HttpServletRequest request) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			return "redirect:/front/member/login";
		}
		
		// 先取得所有收藏貼文，然後在Controller層篩選分類
		List<PostVO> allFavoritePosts = favoritePostService.findFavoritePostsByMemberIdCriteria(loginMember.getMemId());
		
		// 篩選指定分類的貼文，並過濾掉已下架的貼文 (POST_STATUS = 2)
		List<PostVO> favoritePosts = allFavoritePosts.stream()
			.filter(post -> post.getPostCate() != null && 
						   post.getPostCate().getPostCateId().equals(categoryId) &&
						   post.getPostStatus() != 2)
			.toList();
		
		// 排序處理
		List<PostVO> sortedPosts = sortPosts(new java.util.ArrayList<>(favoritePosts), sort);
		
		model.addAttribute("threads", sortedPosts);

		// 查詢所有分類
	    List<PostCategoryVO> category = postCategoryService.getAll();
	    model.addAttribute("category", category);
	    model.addAttribute("currentCategory", categoryId); // 設定當前分類
	    model.addAttribute("currentSort", sort);
	    model.addAttribute("isFavoritePage", true);
	    model.addAttribute("isMyPostsPage", false);
	    model.addAttribute("currentPath", request.getRequestURI()); // 傳遞當前路徑
		return "front/post/listallpost";
	}
	
	// 私有方法：排序貼文
	private List<PostVO> sortPosts(List<PostVO> posts, String sort) {
		switch (sort) {
		case "like_Count":
			posts.sort((a, b) -> b.getLikeCount().compareTo(a.getLikeCount()));
			break;
		case "views":
			posts.sort((a, b) -> b.getViews().compareTo(a.getViews()));
			break;
		case "messagecount":
			// 這裡需要計算留言數，暫時按編輯時間排序
			posts.sort((a, b) -> b.getEditDate().compareTo(a.getEditDate()));
			break;
		default:
			posts.sort((a, b) -> b.getEditDate().compareTo(a.getEditDate())); // 最新貼文
		}
		return posts;
	}
	
	// 新增：搜尋收藏貼文
	@RequestMapping("/search")
	public String searchFavoritePosts(@RequestParam("title") String title,
									 @RequestParam(value = "sort", defaultValue = "editDate") String sort,
									 ModelMap model, HttpSession session, HttpServletRequest request) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			return "redirect:/front/member/login";
		}
		
		if (title == null || title.trim().isEmpty()) {
			return "redirect:/FavoritePost/my";
		}
		
		// 先取得所有收藏貼文
		List<PostVO> allFavoritePosts = favoritePostService.findFavoritePostsByMemberIdCriteria(loginMember.getMemId());
		
		// 篩選包含關鍵字的貼文，並過濾掉已下架的貼文 (POST_STATUS = 2)
		List<PostVO> filteredPosts = allFavoritePosts.stream()
			.filter(post -> post.getPostTitle().toLowerCase().contains(title.toLowerCase().trim()) &&
						   post.getPostStatus() != 2)
			.toList();
		
		// 排序處理
		List<PostVO> sortedPosts = sortPosts(new java.util.ArrayList<>(filteredPosts), sort);
		
		model.addAttribute("threads", sortedPosts);
		
		// 查詢所有分類
		List<PostCategoryVO> category = postCategoryService.getAll();
		model.addAttribute("category", category);
		model.addAttribute("currentSort", sort);
		model.addAttribute("currentKeyword", title);
		model.addAttribute("searchResults", true);
		model.addAttribute("searchCount", sortedPosts.size());
		model.addAttribute("isFavoritePage", true);
		model.addAttribute("isMyPostsPage", false);
		model.addAttribute("currentPath", request.getRequestURI());
		
		return "front/post/listallpost";
	}
}
