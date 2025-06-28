//package com.foodietime.post.controller;
//
//import java.sql.Timestamp;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 引入 RedirectAttributes 以便在重定向時傳遞訊息
//
//import com.foodietime.favoritepost.model.FavoritePostService;
//import com.foodietime.member.model.MemService;
//import com.foodietime.member.model.MemberVO;
//import com.foodietime.message.model.MessageService;
//import com.foodietime.message.model.MessageVO;
//import com.foodietime.post.model.PostRepository;
//import com.foodietime.post.model.PostService;
//import com.foodietime.post.model.PostVO;
//import com.foodietime.postcategory.model.PostCategoryService;
//import com.foodietime.postcategory.model.PostCategoryVO;
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//
///**
// * PostController 負責處理所有與貼文相關的 HTTP 請求。 它作為前端請求與後台服務層之間的橋樑，協調業務邏輯並準備視圖數據。
// */
//@Controller // 標註這是一個 Spring MVC 的控制器
//@RequestMapping("/post") // 定義這個控制器處理所有以 "/post" 開頭的請求路徑
//public class PostMController {
//
//	// 透過 @Autowired 自動注入所需的 Service 和 Repository
//	// 這是 Spring 依賴注入的機制，讓您無需手動實例化這些服務。
//	@Autowired
//	PostService postservice;
//
//	@Autowired
//	PostRepository postRepository; // 直接注入 Repository 通常用於自定義查詢，否則應優先透過 Service 呼叫
//
//	@Autowired
//	PostCategoryService postCategoryservice;
//
//	@Autowired
//	MemService memservice;
//
//	@Autowired
//	MessageService messageService;
//
//	@Autowired
//	FavoritePostService favoritePostService;
//
//	/**
//	 * 顯示新增貼文或重定向到修改貼文的表單頁面。 如果 URL 參數中包含 postId，則表示是修改現有貼文，會重定向到 /post/update 路徑。
//	 * 否則，提供一個空的 PostVO 物件用於新增。
//	 *
//	 * @param postId  貼文 ID，可選參數。如果存在，表示要修改貼文。
//	 * @param model   Spring MVC 模型，用於將數據 (如分類列表、會員資訊、PostVO) 傳遞給前端視圖。
//	 * @param session HTTP 會話，用於從會話中獲取當前登入會員的資訊。
//	 * @return 視圖名稱 (Thymeleaf 模板路徑，例如 "front/post/addPost")。
//	 */
//	@GetMapping("/addPost")
//	public String addPost(@RequestParam(value = "postId", required = false) Integer postId, ModelMap model,
//			HttpSession session) {
//		PostVO postVO;
//		if (postId != null) {
//			// 如果傳入 postId，表示要修改貼文，則重定向到修改貼文頁面，並帶上 postId。
//			return "redirect:/post/update?postId=" + postId;
//		} else {
//			// 如果沒有 postId，表示是新增貼文，則建立一個空的 PostVO 物件。
//			postVO = new PostVO();
//		}
//		// 取得所有貼文分類，用於前端表單的下拉選單。
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//		model.addAttribute("categories", categories);
//
//		// 取得當前登入會員資訊，並放入模型中，以便前端顯示或用於後續操作。
//		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
//		model.addAttribute("loginMember", loginMember);
//
//		// 將 PostVO 物件放入模型，供前端表單進行數據綁定 (Thymeleaf 的 th:object)。
//		model.addAttribute("postVO", postVO);
//
//		// 返回新增貼文的頁面。
//		return "front/post/addPost";
//	}
//
//	/**
//	 * 處理新增貼文的 POST 請求。 根據表單的 "action" 參數 (publish 或 draft) 設定貼文狀態。
//	 *
//	 * @param postVO             透過表單自動綁定並經過 @Valid 驗證的 PostVO 物件。
//	 * @param result             BindingResult 包含了 @Valid 驗證的結果，用於檢查是否有錯誤。
//	 * @param model              Spring MVC 模型。
//	 * @param session            HTTP 會話，用於獲取登入會員資訊。
//	 * @param action             表單提交動作，"publish" 表示發佈，"draft" 表示存為草稿。
//	 * @param redirectAttributes 用於在重定向後向目標頁面傳遞一次性訊息 (Flash Attributes)。
//	 * @return 重定向到貼文列表頁面 (成功) 或返回新增貼文表單頁面 (驗證失敗)。
//	 */
//	@PostMapping("/insert")
//	public String insert(@Valid PostVO postVO, BindingResult result, ModelMap model, HttpSession session,
//			@RequestParam("action") String action, RedirectAttributes redirectAttributes) {
//
//		// 1. 處理會員資訊：如果未登入，則設定為匿名會員 (memId = 1，可根據實際業務邏輯調整)。
//		MemberVO member = (MemberVO) session.getAttribute("loginMember");
//		if (member == null) {
//			// 實際應用中，如果發佈貼文必須登入，這裡應該直接重定向到登入頁面。
//			// 這裡假設可以匿名發文，或是一種後備方案。
//			member = memservice.getById(1); // 假設 memId=1 是匿名會員或預設會員
//		}
//		postVO.setMember(member);
//
//		// 2. 數據驗證：檢查是否有來自 @Valid 註解的驗證錯誤。
//		if (result.hasErrors()) {
//			// 如果有錯誤，需要重新載入前端表單所需的所有數據 (例如分類列表)。
//			List<PostCategoryVO> categories = postCategoryservice.getAll();
//			model.addAttribute("categories", categories);
//			// 將帶有錯誤訊息的 postVO 返回，以便前端可以顯示具體的錯誤。
//			model.addAttribute("postVO", postVO);
//			return "front/post/addPost"; // 返回新增貼文的表單頁面
//		}
//
//		// 3. 設定貼文時間和狀態。
//		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
//		postVO.setPostDate(currentTime); // 設定貼文建立時間
//
//		if ("publish".equals(action)) {
//			postVO.setPostStatus((byte) 0); // 0: 已發佈狀態
//			postVO.setEditDate(currentTime); // 發佈時更新編輯時間
//		} else if ("draft".equals(action)) {
//			postVO.setPostStatus((byte) 1); // 1: 草稿狀態 (未發佈)
//			postVO.setEditDate(currentTime); // 草稿也設定編輯時間，方便追蹤
//		}
//
//		// 初始化讚數和瀏覽數，新貼文通常從 0 開始。
//		postVO.setLikeCount(0);
//		postVO.setViews(0);
//
//		// 4. 呼叫 Service 層方法執行新增操作。
//		// Service 層負責處理具體的業務邏輯和數據庫持久化。
//		postservice.addPost(postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(), postVO.getPostDate(),
//				postVO.getPostStatus(), postVO.getEditDate(), postVO.getPostTitle(), postVO.getPostContent(),
//				postVO.getLikeCount(), postVO.getViews());
//
//		// 5. 新增成功後重定向到所有貼文列表頁面。
//		// 使用 RedirectAttributes 來在重定向後傳遞一次性訊息，訊息只會在目標頁面顯示一次。
//		redirectAttributes.addFlashAttribute("successMessage", "貼文發佈成功！");
//		return "redirect:/post/"; // 重定向到 /post 請求，顯示所有貼文列表
//	}
//
//	/**
//	 * 顯示修改貼文的表單頁面。
//	 *
//	 * @param postId             貼文 ID，必須參數。用於查詢要修改的貼文數據。
//	 * @param model              Spring MVC 模型。
//	 * @param session            HTTP 會話。
//	 * @param redirectAttributes 用於在找不到貼文時重定向並傳遞錯誤訊息。
//	 * @return 視圖名稱或重定向。
//	 */
//	@GetMapping("/update")
//	public String updatePost(@RequestParam("postId") Integer postId, ModelMap model, HttpSession session,
//			RedirectAttributes redirectAttributes) {
//		// 1. 根據 postId 取得要修改的貼文數據。
//		PostVO postVO = postservice.getOnePost(postId);
//		if (postVO == null) {
//			// 如果找不到指定的貼文，傳遞錯誤訊息並重定向回列表頁。
//			redirectAttributes.addFlashAttribute("errorMessage", "找不到指定的貼文！");
//			return "redirect:/post/";
//		}
//
//		// 2. 取得所有貼文分類和登入會員資訊，用於前端表單的顯示。
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//		model.addAttribute("categories", categories);
//		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
//		model.addAttribute("loginMember", loginMember);
//
//		// 3. 將查詢到的 PostVO 物件放入模型，供前端表單進行數據綁定。
//		model.addAttribute("postVO", postVO);
//
//		// 返回修改貼文的頁面。
//		return "front/post/updatePost";
//	}
//
//	/**
//	 * 處理修改貼文的 POST 請求。
//	 *
//	 * @param postVO             透過表單自動綁定並經過 @Valid 驗證的 PostVO 物件，包含修改後的數據。
//	 * @param result             BindingResult 包含了驗證結果。
//	 * @param model              Spring MVC 模型。
//	 * @param redirectAttributes 用於在重定向後向目標頁面傳遞一次性訊息。
//	 * @return 重定向到單一貼文頁面 (成功) 或返回修改貼文表單頁面 (驗證失敗)。
//	 */
//	@PostMapping("/updatePost")
//	public String update(@Valid PostVO postVO, BindingResult result, ModelMap model,
//			RedirectAttributes redirectAttributes) {
//
//		// 1. 數據驗證：檢查是否有驗證錯誤。
//		if (result.hasErrors()) {
//			// 如果有錯誤，需要重新載入前端表單所需的所有數據 (例如分類列表)。
//			List<PostCategoryVO> categories = postCategoryservice.getAll();
//			model.addAttribute("categories", categories);
//			// 返回修改貼文的表單頁面，以便前端顯示錯誤訊息。
//			return "front/post/updatePost";
//		}
//
//		// 2. 檢查貼文內容是否有變動，若有變動則更新編輯時間。
//		// 先從數據庫取得原始貼文，以便比較和保留未變動的數據 (如建立時間、會員ID)。
//		PostVO originalPost = postservice.getOnePost(postVO.getPostId());
//		if (originalPost == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "更新失敗：找不到原始貼文！");
//			return "redirect:/post/";
//		}
//
//		// 判斷標題或內容是否被修改，如果修改則更新 editDate。
//		if (!originalPost.getPostTitle().equals(postVO.getPostTitle())
//				|| !originalPost.getPostContent().equals(postVO.getPostContent())) {
//			postVO.setEditDate(new Timestamp(System.currentTimeMillis())); // 內容有變動，更新編輯時間
//		} else {
//			postVO.setEditDate(originalPost.getEditDate()); // 內容沒變動，保持原始編輯時間
//		}
//
//		// 為了確保數據的完整性，從原始貼文複製一些不應被前端直接修改的數據，例如會員ID和貼文建立時間。
//		postVO.setMember(originalPost.getMember());
//		postVO.setPostDate(originalPost.getPostDate());
//		// 確保 likes 和 views 在更新時也保持最新或原始值 (如果前端沒有提供)。
//		postVO.setLikeCount(originalPost.getLikeCount());
//		postVO.setViews(originalPost.getViews());
//
//		// 3. 呼叫 Service 層方法執行更新操作。
//		postservice.updatePost(postVO.getPostId(), postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(),
//				postVO.getPostDate(), postVO.getPostStatus(), postVO.getEditDate(), postVO.getPostTitle(),
//				postVO.getPostContent(), postVO.getLikeCount(), postVO.getViews());
//
//		// 4. 更新成功後重定向到單一貼文頁面，並傳遞成功訊息。
//		redirectAttributes.addFlashAttribute("successMessage", "貼文修改成功！");
//		return "redirect:/post/one?postId=" + postVO.getPostId();
//	}
//
//	/**
//	 * 處理刪除貼文的 POST 請求。 包含登入驗證和權限檢查，確保只有貼文發布者才能刪除自己的貼文。
//	 *
//	 * @param postId             貼文 ID。
//	 * @param model              Spring MVC 模型。
//	 * @param session            HTTP 會話，用於驗證登入會員資訊。
//	 * @param redirectAttributes 用於在重定向時傳遞訊息。
//	 * @return 重定向到貼文列表頁面。
//	 */
//	@PostMapping("/delete")
//	public String delete(@RequestParam("postId") String postId, ModelMap model, HttpSession session,
//			RedirectAttributes redirectAttributes) {
//
//		// 1.1 驗證登入狀態：如果未登入，則重定向到登入頁面並提示。
//		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
//		if (loginMember == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "請先登入才能執行此操作！");
//			return "redirect:/login";
//		}
//
//		// 1.2 驗證 postId 格式：確保 postId 是有效的數字。
//		Integer id = null;
//		try {
//			id = Integer.valueOf(postId);
//		} catch (NumberFormatException e) {
//			redirectAttributes.addFlashAttribute("errorMessage", "貼文編號格式不正確！");
//			return "redirect:/post/";
//		}
//
//		// 1.3 查詢貼文是否存在：從數據庫查詢要刪除的貼文。
//		PostVO postVO = postservice.getOnePost(id);
//		if (postVO == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "查無此貼文！");
//			return "redirect:/post/";
//		}
//
//		// 1.4 權限檢查：判斷當前登入會員是否為貼文的發布者。
//		if (!postVO.getMember().getMemId().equals(loginMember.getMemId())) {
//			redirectAttributes.addFlashAttribute("errorMessage", "您只能刪除自己的貼文！");
//			return "redirect:/post/";
//		}
//
//		// 2. 執行刪除操作：呼叫 Service 層的刪除方法。
//		postservice.deletePost(id);
//		redirectAttributes.addFlashAttribute("successMessage", "貼文刪除成功！");
//
//		// 3. 刪除成功後重定向到所有貼文列表頁面。
//		return "redirect:/post/";
//	}
//
//	/**
//	 * 透過 GET 請求顯示單一貼文，並增加該貼文的瀏覽數。 這是顯示單一貼文的主要方法，用於用戶點擊貼文標題進入詳情頁面。
//	 *
//	 * @param postId             貼文 ID。
//	 * @param model              Spring MVC 模型。
//	 * @param session            HTTP 會話。
//	 * @param redirectAttributes 用於處理找不到貼文時的重定向。
//	 * @return 視圖名稱 (例如 "front/post/listOnePost")。
//	 */
//	@GetMapping("/one")
//	public String getOnePost(@RequestParam("postId") Integer postId, ModelMap model, HttpSession session,
//			RedirectAttributes redirectAttributes) {
//
//		// 1. 根據 postId 查詢貼文。
//		PostVO postVO = postservice.getOnePost(postId);
//		if (postVO == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "查無此貼文！");
//			return "redirect:/post/";
//		}
//
//		// 2. 增加貼文的瀏覽數並更新到數據庫。
//		postVO.setViews(postVO.getViews() + 1);
//		// 在更新瀏覽數時，為了避免覆蓋其他字段，應該呼叫一個僅更新瀏覽數的方法
//		// 或者確保 updatePost 方法能正確處理部分更新，或傳入原始 postVO 的完整數據。
//		// 這裡假設 updatePost 能夠處理，但實際開發中建議更精細的更新方法。
//		postservice.updatePost(postVO.getPostId(), postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(),
//				postVO.getPostDate(), postVO.getPostStatus(), postVO.getEditDate(), postVO.getPostTitle(),
//				postVO.getPostContent(), postVO.getLikeCount(), postVO.getViews());
//
//		// 3. 將貼文數據放入模型中，供前端顯示。
//		model.addAttribute("postVO", postVO);
//
//		// 4. 取得登入會員資訊，用於判斷是否顯示特定功能 (如留言、收藏)。
//		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
//		model.addAttribute("loginMember", loginMember);
//
//		// 5. 取得該貼文的所有留言列表，並放入模型中。
//		List<MessageVO> messageList = messageService.getByPostId(postId);
//		model.addAttribute("messageList", messageList);
//		model.addAttribute("messageVO", new MessageVO()); // 提供一個空的 MessageVO 用於留言表單
//
//		// 6. 檢查當前貼文是否被登入會員收藏，用於前端顯示收藏狀態。
//		boolean bookmarked = false;
//		if (loginMember != null) {
//			bookmarked = favoritePostService.findByPK(postVO.getPostId(), loginMember.getMemId()) != null;
//		}
//		model.addAttribute("bookmarked", bookmarked);
//
//		// 返回單一貼文的詳情頁面。
//		return "front/post/listOnePost";
//	}
//
//	/**
//	 * 顯示所有貼文列表，並支援多種排序方式。 這是應用程式的首頁或貼文列表頁面。
//	 *
//	 * @param sort    排序方式 (editDate: 最新編輯, like_Count: 最多讚, messagecount: 最多留言,
//	 *                views: 最多瀏覽)。
//	 * @param model   Spring MVC 模型。
//	 * @param session HTTP 會話，用於判斷使用者登入狀態。
//	 * @return 視圖名稱 (例如 "front/post/listallpost")。
//	 */
//	@GetMapping({ "", "/" }) // 同時處理 /post 和 /post/ 路徑
//	public String listAllPost(@RequestParam(value = "sort", required = false, defaultValue = "editDate") String sort,
//			ModelMap model, HttpSession session) {
//
//		// 1. 取得所有貼文 (通常會透過 Service 層從數據庫獲取)。
//		List<PostVO> list = postservice.getAll();
//
//		// 2. 取得所有留言，並計算每篇貼文的留言數量，用於「留言數」排序。
//		List<MessageVO> allMessages = messageService.getAll();
//		// 使用 Java Stream API 將留言按 postId 分組並計數。
//		Map<Integer, Long> postIdToMsgCount = allMessages.stream()
//				.collect(Collectors.groupingBy(m -> m.getPost().getPostId(), Collectors.counting()));
//
//		// 3. 根據 `sort` 參數對貼文列表進行排序。
//		switch (sort) {
//		case "like_Count":
//			// 按讚數降序排序。
//			list.sort((a, b) -> b.getLikeCount().compareTo(a.getLikeCount()));
//			break;
//		case "views":
//			// 按瀏覽數降序排序。
//			list.sort((a, b) -> b.getViews().compareTo(a.getViews()));
//			break;
//		case "messagecount":
//			// 按留言數降序排序。
//			list.sort((a, b) -> {
//				long aCount = postIdToMsgCount.getOrDefault(a.getPostId(), 0L);
//				long bCount = postIdToMsgCount.getOrDefault(b.getPostId(), 0L);
//				return Long.compare(bCount, aCount); // 返回負數表示 a 在 b 之前 (降序)
//			});
//			break;
//		default: // 預設或無效的 sort 參數，按最新編輯日期降序排序。
//			list.sort((a, b) -> b.getEditDate().compareTo(a.getEditDate()));
//		}
//
//		// 4. 取得所有貼文分類，用於前端篩選器或導航。
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//
//		// 5. 將所有數據放入模型中，供前端視圖渲染。
//		model.addAttribute("threads", list); // 將排序後的貼文列表傳遞給前端 (命名為 "threads" 匹配您的 HTML)
//		model.addAttribute("categories", categories); // 所有分類
//		model.addAttribute("currentCategory", null); // 標示當前未選中特定分類
//		model.addAttribute("currentSort", sort); // 傳遞當前排序方式，用於前端選單的高亮顯示
//
//		// 判斷使用者是否登入，以便前端根據登入狀態顯示不同內容。
//		boolean loggedIn = session.getAttribute("loginMember") != null;
//		model.addAttribute("loggedIn", loggedIn);
//
//		// 返回貼文列表的頁面。
//		return "front/post/listallpost";
//	}
//
//	/**
//	 * 處理貼文按讚的 POST 請求。
//	 *
//	 * @param postId             貼文 ID。
//	 * @param session            HTTP 會話，用於驗證登入會員。
//	 * @param redirectAttributes 用於在重定向時傳遞訊息。
//	 * @return 重定向到單一貼文頁面。
//	 */
//	@PostMapping("/like")
//	public String likePost(@RequestParam("postId") Integer postId, HttpSession session,
//			RedirectAttributes redirectAttributes) {
//		// 1. 驗證登入狀態：確保只有登入用戶才能按讚。
//		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
//		if (loginMember == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "請先登入才能按讚！");
//			return "redirect:/login"; // 未登入則重定向到登入頁
//		}
//
//		// 2. 取得貼文：查詢要按讚的貼文。
//		PostVO post = postservice.getOnePost(postId);
//		if (post == null) {
//			redirectAttributes.addFlashAttribute("errorMessage", "找不到要按讚的貼文！");
//			return "redirect:/post/"; // 處理找不到貼文的情況
//		}
//
//		// 3. 增加讚數：將貼文的讚數加一。
//		post.setLikeCount(post.getLikeCount() + 1);
//
//		// 4. 更新貼文：將更新後的讚數持久化到數據庫。
//		// 同樣，這裡呼叫 updatePost 時需要確保所有必要字段都正確傳遞或從原始數據中保留。
//		postservice.updatePost(post.getPostId(), post.getMember().getMemId(), post.getPostCate().getPostCateId(),
//				post.getPostDate(), post.getPostStatus(), post.getEditDate(), post.getPostTitle(),
//				post.getPostContent(), post.getLikeCount(), post.getViews());
//
//		redirectAttributes.addFlashAttribute("successMessage", "按讚成功！");
//		// 5. 重定向回單一貼文頁面，讓用戶看到更新後的讚數。
//		return "redirect:/post/one?postId=" + postId;
//	}
//
//	/**
//	 * 根據關鍵字搜尋貼文。
//	 *
//	 * @param title              搜尋關鍵字。
//	 * @param sort               排序方式。
//	 * @param model              Spring MVC 模型。
//	 * @param session            HTTP 會話。
//	 * @param redirectAttributes 用於處理空關鍵字時的重定向。
//	 * @return 視圖名稱。
//	 */
//	@GetMapping("/search")
//	public String searchPosts(@RequestParam("title") String title,
//			@RequestParam(value = "sort", defaultValue = "editDate") String sort, ModelMap model, HttpSession session,
//			RedirectAttributes redirectAttributes) {
//
//		// 1. 檢查關鍵字是否為空或只包含空格。
//		if (title == null || title.trim().isEmpty()) {
//			redirectAttributes.addFlashAttribute("errorMessage", "請輸入搜尋關鍵字！");
//			return "redirect:/post/"; // 如果為空，重定向回所有貼文列表
//		}
//
//		// 2. 呼叫 Repository 進行關鍵字搜尋，只搜尋已發佈 (狀態為 0) 的貼文。
//		List<PostVO> list = postRepository.findByPostTitleContainingAndPostStatus(title.trim(), 0);
//
//		// 3. 根據排序參數對搜尋結果進行排序。
//		// 注意：留言數排序需要從 messageService 獲取相關留言數據並計算，這裡為簡化暫不實現。
//		switch (sort) {
//		case "like_Count":
//			list.sort((a, b) -> b.getLikeCount().compareTo(a.getLikeCount()));
//			break;
//		case "views":
//			list.sort((a, b) -> b.getViews().compareTo(a.getViews()));
//			break;
//		case "messagecount":
//			// 如果需要留言數排序，這裡可以參考 listAllPost 中的邏輯，獲取所有相關留言並計算。
//			List<MessageVO> allMessages = messageService.getAll();
//			Map<Integer, Long> postIdToMsgCount = allMessages.stream()
//					.collect(Collectors.groupingBy(m -> m.getPost().getPostId(), Collectors.counting()));
//			list.sort((a, b) -> {
//				long aCount = postIdToMsgCount.getOrDefault(a.getPostId(), 0L);
//				long bCount = postIdToMsgCount.getOrDefault(b.getPostId(), 0L);
//				return Long.compare(bCount, aCount);
//			});
//			break;
//		default: // 預設按編輯日期排序。
//			list.sort((a, b) -> b.getEditDate().compareTo(a.getEditDate()));
//		}
//
//		// 4. 取得所有分類，傳遞給前端，保持頁面導航完整。
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//
//		// 5. 將數據放入模型中。
//		model.addAttribute("threads", list); // 搜尋結果列表
//		model.addAttribute("categories", categories);
//		model.addAttribute("currentSort", sort); // 當前排序方式
//		model.addAttribute("currentKeyword", title); // 傳遞當前搜尋關鍵字，以便前端輸入框保留
//		model.addAttribute("searchResults", true); // 標示為搜尋結果頁面
//		model.addAttribute("searchCount", list.size()); // 搜尋結果數量
//
//		// 判斷登入狀態。
//		boolean loggedIn = session.getAttribute("loginMember") != null;
//		model.addAttribute("loggedIn", loggedIn);
//
//		// 返回貼文列表頁面，顯示搜尋結果。
//		return "front/post/listallpost";
//	}
//
//	/**
//	 * 根據貼文分類查詢貼文。
//	 *
//	 * @param categoryId 分類 ID。
//	 * @param sort       排序方式。
//	 * @param model      Spring MVC 模型。
//	 * @param session    HTTP 會話。
//	 * @return 視圖名稱。
//	 */
//	@GetMapping("/category")
//	public String getPostsByCategory(@RequestParam("categoryId") Integer categoryId,
//			@RequestParam(value = "sort", defaultValue = "editDate") String sort, ModelMap model, HttpSession session) {
//
//		// 1. 呼叫 Repository 根據分類 ID 查詢貼文。
//		// Repository 中的 findByCategoryAndSort 方法負責處理按分類和排序邏輯。
//		List<PostVO> list = postRepository.findByCategoryAndSort(categoryId, sort);
//
//		// 2. 取得所有分類，傳遞給前端。
//		List<PostCategoryVO> categories = postCategoryservice.getAll();
//
//		// 3. 找到當前分類的名稱，用於前端顯示。
//		String currentCategoryName = categories.stream().filter(cat -> cat.getPostCateId().equals(categoryId))
//				.map(PostCategoryVO::getPostCate).findFirst().orElse("未知分類");
//
//		// 4. 將數據放入模型中。
//		model.addAttribute("threads", list); // 分類篩選後的貼文列表
//		model.addAttribute("categories", categories);
//		model.addAttribute("currentSort", sort); // 當前排序方式
//		model.addAttribute("currentCategory", categoryId); // 當前分類 ID
//		model.addAttribute("currentCategoryName", currentCategoryName); // 當前分類名稱
//
//		// 判斷登入狀態。
//		boolean loggedIn = session.getAttribute("loginMember") != null;
//		model.addAttribute("loggedIn", loggedIn);
//
//		// 返回貼文列表頁面，顯示分類篩選結果。
//		return "front/post/listallpost";
//	}
//}