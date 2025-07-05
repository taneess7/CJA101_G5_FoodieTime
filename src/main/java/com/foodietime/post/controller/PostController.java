package com.foodietime.post.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodietime.favoritepost.model.FavoritePostService;
import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageService;
import com.foodietime.message.model.MessageVO;
import com.foodietime.post.model.PostRepository;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryService;
import com.foodietime.postcategory.model.PostCategoryVO;
import com.foodietime.reportpost.model.ReportPostService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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

	@Autowired
	MessageService messageService;

	@Autowired
	FavoritePostService favoritePostService;

	@Autowired
	ReportPostService reportPostService;

//	@Autowired
//	PostCategoryService postcategory;

	@GetMapping("/addPost")
	public String addPost(@RequestParam(value = "postId", required = false) Integer postId, ModelMap model,
			HttpSession session) {
		
		// 檢查登入狀態
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			// 未登入用戶，重導到登入頁面
			return "redirect:/front/member/login";
		}
		
		PostVO postVO;
		if (postId != null) {
			return "redirect:/post/update?postId=" + postId; // 如果有 postId，重定向到 update 頁面
		} else {
			postVO = new PostVO(); // 新增時給空物件
		}
		List<PostCategoryVO> category = postCategoryservice.getAll();
		model.addAttribute("category", category);
		model.addAttribute("loginMember", loginMember);
		model.addAttribute("postVO", postVO);
		return "front/post/addPost";
	}

	@PostMapping("/insert")
	public String insert(@Valid PostVO postVO, BindingResult result, ModelMap model, HttpSession session,
			@RequestParam("action") String action, @RequestParam(value = "additionalNotes", required = false) String additionalNotes,
			RedirectAttributes redirectAttributes) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		
		// 1. 先補上會員（登入或匿名）
		MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
		if (member == null) {
			// 未登入用戶，重導到登入頁面
			return "redirect:/front/member/login";
		}
		postVO.setMember(member);
		
		// 2. 處理額外備註 - 存儲到 session 中
		if (additionalNotes != null && !additionalNotes.trim().isEmpty()) {
			session.setAttribute("additionalNotes", additionalNotes.trim());
		} else {
			session.removeAttribute("additionalNotes");
		}

		// 2. 檢查會員是否因檢舉過多而被禁止發文
		if (reportPostService.isMemberBannedFromPosting(member.getMemId())) {
			model.addAttribute("errorMessage", "您的帳號因多次違規已被禁止發文，如有疑問請聯繫客服。");
			List<PostCategoryVO> category = postCategoryservice.getAll();
			model.addAttribute("category", category);
			model.addAttribute("postVO", postVO);
			model.addAttribute("loginMember", member);
			return "front/post/addPost";
		}

		// 3. 驗證失敗時，帶回表單與資料
		if (result.hasErrors()) {
			List<PostCategoryVO> category = postCategoryservice.getAll();
			model.addAttribute("category", category);
			model.addAttribute("postVO", postVO);
			// 重新加入 loginMember
			model.addAttribute("loginMember", member);
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
		if ("draft".equals(action)) {
			// 如果是儲存草稿，導向到討論區首頁
			redirectAttributes.addFlashAttribute("success", "草稿儲存成功！");
			return "redirect:/post/";
		} else {
			// 如果是發佈，導向到討論區首頁
			redirectAttributes.addFlashAttribute("success", "貼文發佈成功！");
			return "redirect:/post/";
		}
	}

	@GetMapping("/update")
	public String updatePost(@RequestParam("postId") Integer postId, ModelMap model, HttpSession session) {
		
		// 檢查登入狀態
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			// 未登入用戶，重導到登入頁面
			return "redirect:/front/member/login";
		}
		
		PostVO postVO = postservice.getOnePost(postId);
		if (postVO == null) {
			model.addAttribute("errorMessage", "找不到指定的貼文");
			return "redirect:/post/";
		}
		
		if (postVO.getMember() == null) {
			// 從 session 或其他方式補上
			postVO.setMember(loginMember);
		}
		List<PostCategoryVO> category = postCategoryservice.getAll();
		model.addAttribute("category", category);
		model.addAttribute("loginMember", loginMember);
		model.addAttribute("postVO", postVO);
		return "front/post/updatePost";
	}

	@PostMapping("/updatePost")
	public String update(@Valid PostVO postVO, BindingResult result, ModelMap model,
			@RequestParam("action") String action, @RequestParam(value = "additionalNotes", required = false) String additionalNotes,
			RedirectAttributes redirectAttributes, HttpSession session) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		
		// 處理額外備註 - 存儲到 session 中
		if (additionalNotes != null && !additionalNotes.trim().isEmpty()) {
			session.setAttribute("additionalNotes", additionalNotes.trim());
		} else {
			session.removeAttribute("additionalNotes");
		}
		
		// 檢查會員是否因檢舉過多而被禁止發文
		if (reportPostService.isMemberBannedFromPosting(postVO.getMember().getMemId())) {
			model.addAttribute("errorMessage", "您的帳號因多次違規已被禁止發文，如有疑問請聯繫客服。");
			List<PostCategoryVO> category = postCategoryservice.getAll();
			model.addAttribute("category", category);
			model.addAttribute("postVO", postVO);
			// 重新加入 loginMember
			MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
			model.addAttribute("loginMember", loginMember);
			return "front/post/updatePost";
		}

		if (result.hasErrors()) {
			// 驗證失敗時，要重新把分類列表加回 model
			List<PostCategoryVO> category = postCategoryservice.getAll();
			model.addAttribute("category", category);
			// 重新加入 loginMember
			MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
			model.addAttribute("loginMember", loginMember);
			return "front/post/updatePost";
		}
		/*************************** 2.開始新增資料 *****************************************/
		// 假設你從 DB 撈出原本的資料來比較
		PostVO originalPost = postservice.getOnePost(postVO.getPostId());

		// 根據使用者點擊的按鈕，設定貼文狀態
		if ("publish".equals(action)) {
			postVO.setPostStatus((byte) 0); // 已發佈
		} else if ("draft".equals(action)) {
			postVO.setPostStatus((byte) 1); // 未發佈(草稿)
		}
		// 如果標題、內容或發佈狀態有變更，就更新編輯時間
		boolean contentChanged = !originalPost.getPostTitle().equals(postVO.getPostTitle())
				|| !originalPost.getPostContent().equals(postVO.getPostContent());
		boolean statusChanged = originalPost.getPostStatus() != postVO.getPostStatus();

		if (contentChanged || statusChanged) {
			postVO.setEditDate(new Timestamp(System.currentTimeMillis()));
		} else {
			postVO.setEditDate(originalPost.getEditDate()); // 保持原編輯時間
		}

		postservice.updatePost(postVO.getPostId(), postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(),
				postVO.getPostDate(), postVO.getPostStatus(), postVO.getEditDate(), postVO.getPostTitle(),
				postVO.getPostContent(), postVO.getLikeCount(), postVO.getViews());

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		if ("draft".equals(action)) {
			redirectAttributes.addFlashAttribute("success", "草稿更新成功！");
			return "redirect:/post/";
		} else {
			redirectAttributes.addFlashAttribute("success", "貼文發佈成功！");
			return "redirect:/post/one?postId=" + postVO.getPostId();
		}
	}

	@PostMapping("/delete")
	public String delete(@RequestParam("postId") String postId, ModelMap model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		
		// 取得目前登入會員
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
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
		redirectAttributes.addFlashAttribute("success", "刪除成功");
		return "redirect:/post/";
	}

	// ================ GET ONE 查詢單一貼文 ================
	@PostMapping("/getOne_For_Display")
	public String getOne_For_Display(@RequestParam("postId") String postId, ModelMap model, HttpSession session) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		
		// 檢查登入狀態
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			// 未登入用戶，重導到登入頁面
			return "redirect:/front/member/login";
		}
		
		Integer id = null;
		try {
			id = Integer.valueOf(postId);
		} catch (NumberFormatException e) {
			model.addAttribute("errorMessage", "貼文編號格式不正確");
			return "redirect:/post/";
		}
		// 依標題查詢
//	    List<PostVO> posts = postservice.getByTitle(postTitle); // 你要實作這個方法，回傳 List
//	    if (posts == null || posts.isEmpty()) {
//	        model.addAttribute("errorMessage", "查無資料");
//	        return "front/post/listOnePost";
//	    }

		/*************************** 2.開始查詢資料 *****************************************/
		PostVO postVO = postservice.getOnePost(id);
		if (postVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "front/post/listOnePost";
		}
		model.addAttribute("loginMember", loginMember);

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("postVO", postVO);
		// 補這行
		model.addAttribute("messageVO", new MessageVO());

		// 你也要補留言列表
		List<MessageVO> messageList = messageService.getByPostId(id);
		model.addAttribute("messageList", messageList);

		return "front/post/listOnePost";
	}

	@GetMapping("/one")
	public String getOnePost(@RequestParam("postId") Integer postId, ModelMap model, HttpSession session) {
		
		// 檢查登入狀態
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			// 未登入用戶，重導到登入頁面
			return "redirect:/front/member/login";
		}
		
		PostVO postVO = postservice.getOnePost(postId);

		// 瀏覽數+1 並存回資料庫
		postVO.setViews(postVO.getViews() + 1);
		postservice.updatePost(postVO.getPostId(), postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(),
				postVO.getPostDate(), postVO.getPostStatus(), postVO.getEditDate(), postVO.getPostTitle(),
				postVO.getPostContent(), postVO.getLikeCount(), postVO.getViews());

		model.addAttribute("postVO", postVO);

		// 加這行
		model.addAttribute("loginMember", loginMember);

		List<MessageVO> messageList = messageService.getByPostId(postId);
		model.addAttribute("messageList", messageList);
		model.addAttribute("messageVO", new MessageVO());
		model.addAttribute("liked"); // boolean
		
		// 檢查收藏狀態，需要先判斷是否登入
		boolean bookmarked = false;
		if (loginMember != null) {
			bookmarked = favoritePostService.findByPK(postVO.getPostId(), loginMember.getMemId()) != null;
		}
		model.addAttribute("bookmarked", bookmarked);
		
		// 將session訊息傳遞給model，然後清除session中的訊息
		// 這樣可以避免刷新頁面時重複顯示訊息
		Object reportSuccess = session.getAttribute("reportSuccess");
		Object errorMessage = session.getAttribute("errorMessage");
		
		if (reportSuccess != null) {
			model.addAttribute("reportSuccess", reportSuccess);
			session.removeAttribute("reportSuccess");
		}
		
		if (errorMessage != null) {
			model.addAttribute("errorMessage", errorMessage);
			session.removeAttribute("errorMessage");
		}
		
		return "front/post/listOnePost";
	}

	// ================ GET ALL 查詢所有貼文 ================
	@GetMapping({ "", "/" })
	public String listAllPost(@RequestParam(value = "sort", required = false, defaultValue = "editDate") String sort,
			ModelMap model, HttpSession session) {

		/***************************** 1.接收請求參數 (無參數) *********************************/
		List<PostVO> list = postservice.getAll();

		// 取得所有留言
		List<MessageVO> allMessages = messageService.getAll();

		// 分組計算每篇貼文的留言數
		Map<Integer, Long> postIdToMsgCount = allMessages.stream()
				.collect(Collectors.groupingBy(m -> m.getPost().getPostId(), Collectors.counting()));
		// 排序
		switch (sort) {
		case "like_Count":
			list.sort((a, b) -> b.getLikeCount().compareTo(a.getLikeCount()));
			break;
		case "views":
			list.sort((a, b) -> b.getViews().compareTo(a.getViews()));
			break;
		case "messagecount":
			list.sort((a, b) -> {
				long aCount = postIdToMsgCount.getOrDefault(a.getPostId(), 0L);
				long bCount = postIdToMsgCount.getOrDefault(b.getPostId(), 0L);
				return Long.compare(bCount, aCount);
			});
			break;
		default:
			list.sort((a, b) -> b.getEditDate().compareTo(a.getEditDate())); // 最新貼文
		}

		/*************************** 2.開始查詢資料 *****************************************/

		List<PostCategoryVO> category = postCategoryservice.getAll();

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
//		model.addAttribute("postListData", list);
		model.addAttribute("threads", list);
		model.addAttribute("category", category);
		model.addAttribute("currentCategory", null); // 全部分類
		model.addAttribute("currentSort", sort);

		// 判斷是否登入
		boolean loggedIn = session.getAttribute("loggedInMember") != null;
		model.addAttribute("loggedIn", loggedIn);
		return "front/post/listallpost";
	}

//	@GetMapping
//	public String postHome(@RequestParam(value = "sort", required = false, defaultValue = "editDate") String sort,
//			ModelMap model, HttpSession session) {
//		return listAllPost(sort, model, session); // 重定向到 listAllPost 方法
//	}

	

	// ================ 新增：按讚數排序 ================
	@PostMapping("/like")
	public String likePost(@RequestParam("postId") Integer postId, HttpSession session,
			RedirectAttributes redirectAttributes) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			return "redirect:/front/member/login";
		}
		// 你可以限制只能對自己的貼文按讚，也可以允許所有人按讚
		PostVO post = postservice.getOnePost(postId);
		// 假設你允許所有人按讚
		post.setLikeCount(post.getLikeCount() + 1);
		postservice.updatePost(post.getPostId(), post.getMember().getMemId(), post.getPostCate().getPostCateId(),
				post.getPostDate(), post.getPostStatus(), post.getEditDate(), post.getPostTitle(),
				post.getPostContent(), post.getLikeCount(), post.getViews());
		redirectAttributes.addFlashAttribute("success", "按讚成功！");
		return "redirect:/post/one?postId=" + postId;
	}

	// ================ 新增：瀏覽數排序 ================

	// ================ 新增：關鍵字搜尋 ================
	@GetMapping("/search")
	public String searchPosts(@RequestParam("title") String title,
			@RequestParam(value = "sort", defaultValue = "date") String sort, ModelMap model, HttpSession session) {

		if (title == null || title.trim().isEmpty()) {
			return "redirect:/post/";
		}

		List<PostVO> list = postRepository.findByPostTitleContainingAndPostStatus(title.trim(), (byte) 0);
		// 你可以再依 sort 排序
		switch (sort) {
		case "like_Count":
			list.sort((a, b) -> b.getLikeCount().compareTo(a.getLikeCount()));
			break;
		case "views":
			list.sort((a, b) -> b.getViews().compareTo(a.getViews()));
			break;
		default:
			list.sort((a, b) -> b.getEditDate().compareTo(a.getEditDate()));
		}
		List<PostCategoryVO> category = postCategoryservice.getAll();

		model.addAttribute("threads", list);
		model.addAttribute("category", category);
		model.addAttribute("currentSort", sort);
		model.addAttribute("currentKeyword", title);
		model.addAttribute("searchResults", true);
		model.addAttribute("searchCount", list.size());

		boolean loggedIn = session.getAttribute("loggedInMember") != null;
		model.addAttribute("loggedIn", loggedIn);

		return "front/post/listallpost";
	}

	// ================ 分類查詢 ================
	@GetMapping("/category")
	public String getPostsByCategory(@RequestParam("categoryId") Integer categoryId,
			@RequestParam(value = "sort", defaultValue = "date") String sort, ModelMap model, HttpSession session) {

		List<PostVO> list = postRepository.findByCategoryAndSort(categoryId, sort);
		List<PostCategoryVO> category = postCategoryservice.getAll();

		// 找到當前分類名稱
		String currentCategoryName = category.stream().filter(cat -> cat.getPostCateId().equals(categoryId))
				.map(PostCategoryVO::getPostCate).findFirst().orElse("未知分類");

		model.addAttribute("threads", list);
		model.addAttribute("category", category);
		model.addAttribute("currentSort", sort);
		model.addAttribute("currentCategory", categoryId);
		model.addAttribute("currentCategoryName", currentCategoryName);

		boolean loggedIn = session.getAttribute("loggedInMember") != null;
		model.addAttribute("loggedIn", loggedIn);

		return "front/post/listallpost";
	}

	@GetMapping("/myPosts")
	public String myPosts(ModelMap model, HttpSession session) {
		
		// 1. 檢查登入狀態
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		if (loginMember == null) {
			// 未登入用戶，重導到登入頁面
			return "redirect:/front/member/login";
		}

		// 2. 根據會員ID查詢其所有貼文（包含草稿）
		// 這需要你在 PostRepository 中新增一個方法: findByMemberMemIdOrderByEditDateDesc
		List<PostVO> myPosts = postRepository.findByMemberMemIdOrderByEditDateDesc(loginMember.getMemId());
		model.addAttribute("threads", myPosts);

		// 3. 準備畫面需要的其他資料
		List<PostCategoryVO> category = postCategoryservice.getAll();
		model.addAttribute("category", category);

		// 傳遞頁面狀態，讓 Thymeleaf 可以高亮 "我的貼文" 按鈕
		model.addAttribute("isMyPostsPage", true);
		model.addAttribute("isFavoritePage", false);

		model.addAttribute("currentCategory", null);
		model.addAttribute("currentSort", "editDate"); // 預設排序

		return "front/post/listallpost";
	}

}
