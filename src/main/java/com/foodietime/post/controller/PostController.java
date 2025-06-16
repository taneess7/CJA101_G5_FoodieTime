//package com.foodietime.post.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.foodietime.post.model.PostService;
//import com.foodietime.post.model.PostVO;
//
//import jakarta.validation.constraints.NotEmpty;
//
//@Controller
//@RequestMapping("/post")
//public class PostController {
//
////	@Autowired
//	PostService postservice;
////	@Autowired
//	PostCategory postcategory;
//
//	@GetMapping("addPost")
//	public String addPost() {
//		PostVO postVO = new PostVO();
//		model.addAttribute("postVO", postVO);
//		return "";
//	}
//
//	@PostMapping("addPost")
//	public String insert(@Valic PostVO postVO, )
//	@RequestParam("") 
//	/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
//	@NotEmpty(message = "")
//	/*************************** 2.開始新增資料 *****************************************/
//	postservice.addPost(postVO);
//	/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
//    List<PostVO> list = postservice.getAll();
//    model.addAttribute("postListData", list);
//    model.addAttribute("success", "新增成功");
//    
//
//}
