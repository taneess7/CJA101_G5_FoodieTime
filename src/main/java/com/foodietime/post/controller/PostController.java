package com.foodietime.post.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostRepository;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryService;
import com.foodietime.postcategory.model.PostCategoryVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Controller
@RequestMapping("/post")
public class PostController {

	@Autowired
	PostService postservice;

	@Autowired
	PostRepository postRepository;

	@Autowired
	PostCategoryService postCategoryservice;

	@Autowired
	MemService memservice;

//	@Autowired
//	PostCategoryService postcategory;

	@GetMapping("/addPost")
	public String addPost(ModelMap model, HttpSession session) {
		PostVO postVO = new PostVO();
		List<PostCategoryVO> categories = postCategoryservice.getAll();
		model.addAttribute("categories", categories);
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		model.addAttribute("loginMember", loginMember);
		model.addAttribute("postVO", postVO);
		return "front/post/addPost"; // 表單頁面的 view 名稱
	}

	@PostMapping("/insert")
	public String insert(@Valid PostVO postVO, BindingResult result, ModelMap model, HttpSession session,
			@RequestParam("action") String action) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		// 1. 先補上會員（登入或匿名）
		MemberVO member = (MemberVO) session.getAttribute("loginMember");
		if (member == null) {
			member = memservice.getById(2); // 匿名會員
		}
		postVO.setMember(member);

		// 2. 驗證失敗時，帶回表單與資料
		if (result.hasErrors()) {
			List<PostCategoryVO> categories = postCategoryservice.getAll();
			model.addAttribute("categories", categories);
			model.addAttribute("postVO", postVO);
			return "front/post/addPost";
		}
		/*************************** 2.開始新增資料 *****************************************/
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		postVO.setPostDate(currentTime);

		if (action.equals("publish")) {
			postVO.setPostStatus((byte) 0); // 已發佈
			postVO.setEditDate(currentTime); // 發佈時設定編輯時間
		} else if (action.equals("draft")) {
			postVO.setPostStatus((byte) 1); // 未發佈
			postVO.setEditDate(currentTime); // 預設為POST_DATE
		}

		postVO.setLikeCount(0);
		postVO.setViews(0);

		postservice.addPost(postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(), postVO.getPostDate(),
				postVO.getPostStatus(), postVO.getEditDate(), postVO.getPostTitle(), postVO.getPostContent(),
				postVO.getLikeCount(), postVO.getViews());
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<PostVO> list = postservice.getAll();
		model.addAttribute("postListData", list);
		model.addAttribute("success", "新增成功");
		return "redirect:/post/";
	}

	@PostMapping("/updatePost")
	public String update(@Valid PostVO postVO, BindingResult result, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		if (result.hasErrors()) {
			return "front/post/addPost";
		}
		/*************************** 2.開始新增資料 *****************************************/
		// 假設你從 DB 撈出原本的資料來比較
		PostVO originalPost = postservice.getOnePost(postVO.getPostId());

		if (!originalPost.getPostTitle().equals(postVO.getPostTitle())
				|| !originalPost.getPostContent().equals(postVO.getPostContent())) {
			postVO.setEditDate(new Timestamp(System.currentTimeMillis()));
		} else {
			postVO.setEditDate(originalPost.getEditDate());
		}

		postservice.updatePost(postVO.getPostId(), postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(),
				postVO.getPostDate(), postVO.getPostStatus(), postVO.getEditDate(), postVO.getPostTitle(),
				postVO.getPostContent(), postVO.getLikeCount(), postVO.getViews());

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "修改成功");
		model.addAttribute("postVO", postVO);
		return "redirect:/post/";
	}

	@PostMapping("/delete")
	public String delete(@RequestParam("postId") String postId, ModelMap model, HttpSession session) {
		
		// ====== 測試用：手動指定登入會員 ======
	    // 你可以改這個 ID 來測試不同會員
	    MemberVO fakeMember = memservice.getById(2); // 2 改成你想測試的會員ID
	    session.setAttribute("loginMember", fakeMember);
	    // ====== 測試用結束 ======
		// 取得目前登入會員
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		if (loginMember == null) {
			model.addAttribute("errorMessage", "請先登入");
			return "redirect:/login";
		}

		// 查詢該貼文
		PostVO postVO = postservice.getOnePost(Integer.valueOf(postId));
		if (postVO == null) {
			model.addAttribute("errorMessage", "查無此貼文");
			return "redirect:/post/";
		}

		// 判斷是否為本人貼文
		if (!postVO.getMember().getMemId().equals(loginMember.getMemId())) {
			model.addAttribute("errorMessage", "只能刪除自己的貼文");
			return "redirect:/post/";
		}

		// 執行刪除
		postservice.deletePost(Integer.valueOf(postId));
		model.addAttribute("success", "刪除成功");
		return "redirect:/post/";
	}

	// ================ GET ONE 查詢單一貼文 ================
	@PostMapping("/getOne_For_Display")
	public String getOne_For_Display(@RequestParam("postId") String postId, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		Integer id = null;
		try {
			id = Integer.valueOf(postId);
		} catch (NumberFormatException e) {
			model.addAttribute("errorMessage", "貼文編號格式不正確");
			return "redirect:/post/";
		}

		/*************************** 2.開始查詢資料 *****************************************/
		PostVO postVO = postservice.getOnePost(id);
		if (postVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "front/post/listOnePost";
		}

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("postVO", postVO);
		return "front/post/listOnePost";
	}

	@GetMapping("/one")
	public String getOnePost(@RequestParam("postId") Integer postId, ModelMap model) {
		PostVO postVO = postservice.getOnePost(postId);
		model.addAttribute("postVO", postVO);
		return "front/post/listOnePost";
	}

	// ================ GET ALL 查詢所有貼文 ================
	@GetMapping("/")
	public String listAllPost(@RequestParam(value = "sort", required = false, defaultValue = "editDate") String sort,
			ModelMap model, HttpSession session) {

		/***************************** 1.接收請求參數 (無參數) *********************************/
		List<PostVO> list = postservice.getAll();
		// 排序
		switch (sort) {
		case "likes":
			list.sort((a, b) -> b.getLikeCount().compareTo(a.getLikeCount()));
			break;
		case "views":
			list.sort((a, b) -> b.getViews().compareTo(a.getViews()));
			break;
//		case "messagecount":
//			list.sort((a, b) -> {
//				int aCount = a.getMessageVOs() != null ? a.getMessageVOs().size() : 0;
//				int bCount = b.getMessageVOs() != null ? b.getMessageVOs().size() : 0;
//				return Integer.compare(bCount, aCount);
//			});
//			break;
		default:
			list.sort((a, b) -> b.getEditDate().compareTo(a.getEditDate()));
		}

		/*************************** 2.開始查詢資料 *****************************************/

		List<PostCategoryVO> categories = postCategoryservice.getAll();

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
//		model.addAttribute("postListData", list);
		model.addAttribute("threads", list);
		model.addAttribute("categories", categories);

		// 判斷是否登入
		boolean loggedIn = session.getAttribute("loginMember") != null;
		model.addAttribute("loggedIn", loggedIn);
		return "front/post/listallpost";
	}

	@GetMapping
	public String postHome(@RequestParam(value = "sort", required = false, defaultValue = "editDate") String sort,
			ModelMap model, HttpSession session) {
		return listAllPost(sort, model, session); // 重定向到 listAllPost 方法
	}

	// ================ 選擇頁面 ================
	@GetMapping("/select_page")
	public String select_page() {
		return "front/post/select_page";
	}

	// ================ 新增：按讚數排序 ================
//	@GetMapping("/byLikes")
//	public String getPostsByLikes(ModelMap model, HttpSession session) {
//
//		List<PostVO> list = postRepository.getPostsOrderByLikes();
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//
//		model.addAttribute("threads", list);
//		model.addAttribute("categories", categories);
//		model.addAttribute("currentSort", "likes");
//
//		boolean loggedIn = session.getAttribute("loginMember") != null;
//		model.addAttribute("loggedIn", loggedIn);
//
//		return "front/post/listallpost";
//	}
//
//	// ================ 新增：瀏覽數排序 ================
//	@GetMapping("/byViews")
//	public String getPostsByViews(ModelMap model, HttpSession session) {
//
//		List<PostVO> list = postRepository.getPostsOrderByViews();
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//
//		model.addAttribute("threads", list);
//		model.addAttribute("categories", categories);
//		model.addAttribute("currentSort", "views");
//
//		boolean loggedIn = session.getAttribute("loginMember") != null;
//		model.addAttribute("loggedIn", loggedIn);
//
//		return "front/post/listallpost";
//	}
//
//	// ================ 新增：關鍵字搜尋 ================
//	@GetMapping("/search")
//	public String searchPosts(@RequestParam("keyword") String keyword,
//			@RequestParam(value = "sort", defaultValue = "date") String sort, ModelMap model, HttpSession session) {
//
//		if (keyword == null || keyword.trim().isEmpty()) {
//			return "redirect:/post/";
//		}
//
//		List<PostVO> list = postRepository.searchPostsByKeyword(keyword.trim(), sort);
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//
//		model.addAttribute("threads", list);
//		model.addAttribute("categories", categories);
//		model.addAttribute("currentSort", sort);
//		model.addAttribute("currentKeyword", keyword);
//		model.addAttribute("searchResults", true);
//		model.addAttribute("searchCount", list.size());
//
//		boolean loggedIn = session.getAttribute("loginMember") != null;
//		model.addAttribute("loggedIn", loggedIn);
//
//		return "front/post/listallpost";
//	}
//
//	// ================ 新增：分類查詢 ================
//	@GetMapping("/category")
//	public String getPostsByCategory(@RequestParam("categoryId") Integer categoryId,
//			@RequestParam(value = "sort", defaultValue = "date") String sort, ModelMap model, HttpSession session) {
//
//		List<PostVO> list = postRepository.getPostsByCategory(categoryId, sort);
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//
//		// 找到當前分類名稱
//		String currentCategoryName = categories.stream().filter(cat -> cat.getPostCateId().equals(categoryId))
//				.map(PostCategoryVO::getPostCate).findFirst().orElse("未知分類");
//
//		model.addAttribute("threads", list);
//		model.addAttribute("categories", categories);
//		model.addAttribute("currentSort", sort);
//		model.addAttribute("currentCategory", categoryId);
//		model.addAttribute("currentCategoryName", currentCategoryName);
//
//		boolean loggedIn = session.getAttribute("loginMember") != null;
//		model.addAttribute("loggedIn", loggedIn);
//
//		return "front/post/listallpost";
//	}
}
