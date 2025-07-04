package com.foodietime.post.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.foodietime.directmessage.model.DirectMessageDTO;
import com.foodietime.directmessage.model.DirectMessageService;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/front/member")
public class PostMController {
	
	@Autowired
    private DirectMessageService dmService;
	
	@GetMapping("/post/notifications")
	public String showNotifications(HttpSession session, Model model,
	                               @RequestParam(defaultValue = "0") int page,
	                               @RequestParam(defaultValue = "10") int size) {
	    MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	    if (member == null) {
	        return "redirect:/front/member/login";
	    }
	    
	    
	    
	    // 取得所有系統通知（管理員發送的訊息）
	    List<DirectMessageDTO> allNotifications = dmService.getMessagesDtoByMemberId(member.getMemId())
	        .stream()
	        .filter(msg -> msg.getMessDirection() == 1) // 只取管理員發送的訊息
	        .filter(msg -> msg.getMessContent() != null && msg.getMessContent().contains("【系統通知】")) //排除客服回覆
	        .sorted((a, b) -> b.getMessTime().compareTo(a.getMessTime())) // 依時間降序排列
	        .collect(Collectors.toList());
	    
	    
	    
	    // 手動分頁
	    int totalNotifications = allNotifications.size();
	    int totalPages = (int) Math.ceil((double) totalNotifications / size);
	    int startIndex = page * size;
	    int endIndex = Math.min(startIndex + size, totalNotifications);
	    
	    List<DirectMessageDTO> pageNotifications = totalNotifications > 0 ? 
	        allNotifications.subList(startIndex, endIndex) : new ArrayList<>();
	    
	    
	    
	    model.addAttribute("member", member);
	    model.addAttribute("notifications", pageNotifications);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalNotifications", totalNotifications);
	    
	    return "front/member/notifications";
	}
}