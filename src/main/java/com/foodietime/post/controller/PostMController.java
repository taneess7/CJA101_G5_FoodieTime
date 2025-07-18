package com.foodietime.post.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.foodietime.reportpost.dto.ForumReportDTO;
import com.foodietime.reportpost.model.ReportPostService;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/front/member")
public class PostMController {
	
	@Autowired
    private ReportPostService reportPostService;
	
	
	
	/**
	 * 收件夾頁面 - 顯示檢舉相關的通知
	 */
	@GetMapping("/post/notifications")
	public String showNotifications(HttpSession session, Model model,
	                               @RequestParam(defaultValue = "0") int page,
	                               @RequestParam(defaultValue = "10") int size) {
	    MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	    if (member == null) {
	        return "redirect:/front/member/login";
	    }
	    

	    model.addAttribute("memberVO", member);

	    // 取得該會員的所有檢舉通知（使用 ForumReportDTO）
	    List<ForumReportDTO> allNotifications = reportPostService.getNotificationsByMemberId(member.getMemId());

	    
	    // 手動分頁
	    int totalNotifications = allNotifications.size();
	    int totalPages = (int) Math.ceil((double) totalNotifications / size);
	    int startIndex = page * size;
	    int endIndex = Math.min(startIndex + size, totalNotifications);
	    
	    List<ForumReportDTO> pageNotifications = totalNotifications > 0 ? 
	        allNotifications.subList(startIndex, endIndex) : new ArrayList<>();
	    
	    model.addAttribute("member", member);
	    model.addAttribute("notifications", pageNotifications);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("totalNotifications", totalNotifications);
	    
	    return "front/member/notifications";
	}
}