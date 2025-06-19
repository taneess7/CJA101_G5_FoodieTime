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

import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryService;
import com.foodietime.postcategory.model.PostCategoryVO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;


@Controller
@RequestMapping("/post")
public class PostController {

	@Autowired
	PostService postservice;
	
	@Autowired
	PostCategoryService postCategoryservice;
	
	
//	@Autowired
//	PostCategoryService postcategory;

	@GetMapping("/addPost")
	public String addPost(ModelMap model) {
		PostVO postVO = new PostVO();
		model.addAttribute("postVO", postVO);
		return "back-end/post/addPost"; // 表單頁面的 view 名稱
	}

	@PostMapping("/insert")
	public String insert(@Valid PostVO postVO, BindingResult result, ModelMap model,
			@RequestParam("action") String action) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		if (result.hasErrors()) {
			return "back-end/post/addPost";
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
		return "redirect:/post/listAllPost";
	}

	@PostMapping("/updatePost")
	public String update(@Valid PostVO postVO, BindingResult result, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		if (result.hasErrors()) {
			return "back-end/post/addPost";
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
		return "redirect:/post/listAllPost";
	}

	@PostMapping("/delete")
	public String delete(@RequestParam("postId") String postId, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
//		Integer id = null;
//		try {
//			id = Integer.valueOf(postId);
//		} catch (NumberFormatException e) {
//			List<PostVO> list = postservice.getAll();
//			model.addAttribute("postListData", list);
//			model.addAttribute("errorMessage", "貼文編號格式不正確");
//			return "back-end/post/listAllPost";
//		}

		/*************************** 2.開始刪除資料 *****************************************/
//		postservice.deletePost(id);
		postservice.deletePost(Integer.valueOf(postId));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<PostVO> list = postservice.getAll();
		model.addAttribute("postListData", list);
		model.addAttribute("success", "刪除成功");
		return "redirect:/post/listAllPost";
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
			return "front/post/listOnePost";
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
	public String listAllPost(ModelMap model) {

		/**************************** 1.接收請求參數 (無參數)************************************/

		/*************************** 2.開始查詢資料 *****************************************/
		List<PostVO> list = postservice.getAll();
		List<PostCategoryVO> categories = postCategoryservice.getAll();

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
//		model.addAttribute("postListData", list);
		model.addAttribute("threads", list);
		model.addAttribute("categories", categories);
		return "front/post/listallpost";
	}
	@GetMapping
	public String postHome(ModelMap model) {
	    return listAllPost(model); // 重定向到 listAllPost 方法
	}

	 //================ 選擇頁面 ================
	 @GetMapping("/select_page")
	 public String select_page() {
	 	return "front/post/select_page";
	 }
}
