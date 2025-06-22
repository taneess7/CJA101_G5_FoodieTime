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
		// ====== 測試用：手動指定登入會員 ======
		// 你可以改這個 ID 來測試不同會員
		MemberVO fakeMember = memservice.getById(1); // 2 改成你想測試的會員ID
		session.setAttribute("loginMember", fakeMember);
		// ====== 測試用結束 ======
		// 取得貼文ID
		Integer postId = messageVO.getPost().getPostId();
		if (result.hasErrors()) {
	        // 重新查詢該貼文與留言
	        PostVO postVO = postservice.getOnePost(postId);
	        model.addAttribute("postVO", postVO);
	        List<MessageVO> messageList = messageService.getByPostId(postId);
	        model.addAttribute("messageList", messageList);
	        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
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
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		List<MessageVO> list = messageService.getAll();
		model.addAttribute("mesListData", list);
		
	    // 設定成功訊息到 session
	    session.setAttribute("reportSuccess", "留言成功，感謝您的協助！");

	    return "redirect:/post/one?postId=" + postId;
	}
}
