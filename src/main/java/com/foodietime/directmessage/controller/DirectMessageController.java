package com.foodietime.directmessage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodietime.directmessage.model.DirectMessageService;
import com.foodietime.directmessage.model.DirectMessageVO;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/front/directmessage")
public class DirectMessageController {

    @Autowired
    private DirectMessageService messageService;

    @GetMapping("/send")
    public String showSendForm(HttpSession session,Model model) {
    	MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) return "redirect:/front/member/login";
    	
        model.addAttribute("message", new DirectMessageVO());
        return "front/directmessage/send"; // ⬅️ 對應 templates/directmessage/send.html
    }

    @PostMapping("/send")
    public String submitMessage(@ModelAttribute("message") DirectMessageVO message, HttpSession session,RedirectAttributes redirectAttributes) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) return "redirect:/front/member/login";

        message.setMember(member);
        message.setMessDirection(DirectMessageVO.MessageDirection.MEMBER_TO_ADMIN);
        messageService.addMessage(message);
        
        return "redirect:/front/directmessage/send_success";
    }
    
    @GetMapping("/send_success")
    public String showSuccessPage() {
        return "front/directmessage/send_success"; // 對應 Thymeleaf 頁面
    }

}
