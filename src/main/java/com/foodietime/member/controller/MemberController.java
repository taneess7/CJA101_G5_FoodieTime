package com.foodietime.member.controller;

import java.io.IOException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;
@Controller
@RequestMapping("/member")
public class MemberController {
	 @Autowired
	 private MemService memService;
	
	@PostMapping("/register")
	public String registerMember(
	        @RequestParam("mem_email") String memEmail,
	        @RequestParam("mem_account") String memAccount,
	        @RequestParam("mem_password") String memPassword,
	        @RequestParam("mem_nickname") String memNickname,
	        @RequestParam("mem_name") String memName,
	        @RequestParam("mem_phone") String memPhone,
	        @RequestParam("mem_gender") Integer genderOrdinal,
	        @RequestParam("mem_city") String memCity,
	        @RequestParam("mem_cityarea") String memCityarea,
	        @RequestParam("mem_address") String memAddress,
	        @RequestParam("mem_avatar") MultipartFile memAvatar,
	        Model model
	) {
	    MemberVO member = new MemberVO();

	    // 設定欄位
	    member.setMemEmail(memEmail);
	    member.setMemAccount(memAccount);
	    member.setMemPassword(memPassword);
	    member.setMemNickname(memNickname);
	    member.setMemName(memName);
	    member.setMemPhone(memPhone);
	    member.setMemGender(MemberVO.Gender.values()[genderOrdinal]);
	    member.setMemCity(memCity);
	    member.setMemCityarea(memCityarea);
	    member.setMemAddress(memAddress);
	    member.setMemTime(new Timestamp(System.currentTimeMillis()));

	    try {
	        if (!memAvatar.isEmpty()) {
	            member.setMemAvatar(memAvatar.getBytes());
	        }
	    } catch (IOException e) {
	        e.printStackTrace(); // 可改為 log
	    }
	    

	    memService.addMem(
	            member.getMemEmail(),
	            member.getMemAccount(),
	            member.getMemPassword(),
	            member.getMemNickname(),
	            member.getMemName(),
	            member.getMemPhone(),
	            member.getMemGender(),
	            member.getMemCity(),
	            member.getMemCityarea(),
	            member.getMemAddress(),
	            member.getMemAvatar(),
	            member.getMemTime()
	    );

	    model.addAttribute("success", "註冊成功！");
	    return "redirect:/member/list"; // 註冊成功後導向會員列表
	}
	
	 @PostMapping("/login")
	    public String login(
	            @RequestParam String memAccount,
	            @RequestParam String memPassword,
	            Model model,
	            HttpSession session) {

	        MemberVO member = memService.findByAccount(memAccount);

	        if (member != null && member.getMemPassword().equals(memPassword)) {
	            session.setAttribute("loggedInMember", member);
	            return "redirect:/"; // 登入成功後導回首頁或會員中心
	        } else {
	            model.addAttribute("error", "帳號或密碼錯誤！");
	            return "member/login";
	        }
	    }


}
