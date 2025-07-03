package com.foodietime.message.controller;

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
import com.foodietime.message.model.MessageRepository;
import com.foodietime.message.model.MessageVO;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.message.model.MessageService;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/message")
public class MessageController {
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	MemService memservice;
	
	@Autowired
	PostService postservice;


	@PostMapping("/insert")
	public String insert(@Valid MessageVO messageVO, BindingResult result, ModelMap model, HttpSession session,
			@RequestParam("action") String action) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/
		
		// 取得貼文ID
		Integer postId = messageVO.getPost().getPostId();
		
		// 檢查貼文狀態，防止對已下架的貼文留言
		PostVO postVO = postservice.getOnePost(postId);
		if (postVO == null) {
			// 貼文不存在
			session.setAttribute("errorMessage", "貼文不存在！");
			return "redirect:/post/one?postId=" + postId;
		}
		
		if (postVO.getPostStatus() == 2) {
			// 貼文已下架，不允許留言
			session.setAttribute("errorMessage", "此貼文已下架，無法留言！");
			return "redirect:/post/one?postId=" + postId;
		}
		
		if (result.hasErrors()) {
	        // 重新查詢該貼文與留言
	        model.addAttribute("postVO", postVO);
	        List<MessageVO> messageList = messageService.getByPostId(postId);
	        model.addAttribute("messageList", messageList);
	        MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
	        model.addAttribute("loginMember", loginMember);
	        model.addAttribute("messageVO", messageVO);
	        // 回到單一貼文頁
	        return "front/post/listOnePost";
		}
		// 補上當下時間
		if (messageVO.getMesDate() == null) {
			messageVO.setMesDate(new java.sql.Timestamp(System.currentTimeMillis()));
		}
		/*************************** 2.開始新增資料 *****************************************/
		messageService.save(messageVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
		List<MessageVO> list = messageService.getAll();
		model.addAttribute("mesListData", list);
		
	    // 設定成功訊息到 session
	    session.setAttribute("reportSuccess", "留言成功，感謝您的協助！");

	    return "redirect:/post/one?postId=" + postId;
	}
	
	/**
	 * 清除session中的訊息
	 */
	@PostMapping("/clear-session-message")
	public String clearSessionMessage(HttpSession session, @RequestParam("type") String type, 
			@RequestParam(value = "postId", required = false) Integer postId) {
		if ("success".equals(type)) {
			session.removeAttribute("reportSuccess");
		} else if ("error".equals(type)) {
			session.removeAttribute("errorMessage");
		}
		
		// 如果有postId，重定向到該貼文頁面，否則重定向到貼文列表
		if (postId != null) {
			return "redirect:/post/one?postId=" + postId;
		} else {
			return "redirect:/post/";
		}
	}
}
