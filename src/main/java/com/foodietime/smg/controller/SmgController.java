package com.foodietime.smg.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/smg")
public class SmgController {
	@Autowired
	SmgService smgSvc;
//	@PostMapping("/login")
//    public String login(@RequestParam String account,
//                        @RequestParam String password,
//                        HttpSession session,
//                        Model model) {
//
//        SmgVO smg = smgSvc.login(account, password);
//
//        if (smg != null) {
//            session.setAttribute("loggedInSmg", smg); // 儲存商家資訊於 session
//            return "redirect:/smg/dashboard";
//        } else {
//            model.addAttribute("error", "帳號或密碼錯誤");
//            return "smgLogin"; // 返回登入頁面
//        }
//    }
//
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/smg/login";
//    }
	
	
}
