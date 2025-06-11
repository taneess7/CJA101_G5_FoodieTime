package com.foodietime.member.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;
import com.foodietime.member.model.MemService;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemService memService;
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
    	model.addAttribute("member", new MemberVO()); // 表單綁定使用
        return "member/register"; // 對應 src/main/resources/templates/member/register.html
    }

    @PostMapping("/register")
    public String register(@ModelAttribute MemberVO member,
                           @RequestParam("mem_avatar") MultipartFile memAvatarFile,
                           Model model) throws IOException {
    	
    	// 帳號或 Email 重複檢查
        if (memService.isAccountExists(member.getMemAccount())) {
            model.addAttribute("member", member);
            model.addAttribute("error", "帳號已存在，請選擇其他帳號");
            return "member/register";
        }

        if (memService.isEmailExists(member.getMemEmail())) {
            model.addAttribute("member", member);
            model.addAttribute("error", "Email 已註冊過，請使用其他信箱");
            return "member/register";
        }
    	
    	
        if (!memAvatarFile.isEmpty()) {
            member.setMemAvatar(memAvatarFile.getBytes());
        }

        member.setMemTime(Timestamp.from(Instant.now())); // 設定當下註冊時間
        memService.save(member);

        model.addAttribute("successMessage", "註冊成功！");
        return "redirect:/member/success";
    }

    @GetMapping("/success")
    public String registerSuccess() {
        return "member/success"; // 自行建立 success 畫面
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String mem_account,
                        @RequestParam String mem_password,
                        Model model,
                        HttpSession session) {

        MemberVO member = memService.login(mem_account, mem_password);

        if (member != null) {
            session.setAttribute("loggedInMember", member);
            return "redirect:/"; // 成功導向首頁
        } else {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "member/login"; // 回登入頁
        }
        
    }
    

    @GetMapping("/edit_profile")
    public String showEditForm(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/member/login";
        }

        // 重新查詢最新資料（保險做法）
        MemberVO fullMember = memService.getById(member.getMemId());
        model.addAttribute("member", fullMember);
        return "member/edit_profile"; // 對應 templates/member/edit.html
    }

    // ✅ 接收表單送出更新
    @PostMapping("/update")
    public String updateMember(
            @RequestParam("mem_id") Integer memId,
            @RequestParam("mem_nickname") String nickname,
            @RequestParam("mem_name") String name,
            @RequestParam("mem_phone") String phone,
            @RequestParam("mem_city") String city,
            @RequestParam("mem_cityarea") String cityarea,
            @RequestParam("mem_address") String address,
            @RequestParam("mem_avatar") MultipartFile avatarFile,
            HttpSession session,
            Model model) {

        MemberVO member = memService.getById(memId);
        if (member == null) {
            model.addAttribute("error", "查無會員資料");
            return "member/edit_profile";
        }

        member.setMemNickname(nickname);
        member.setMemName(name);
        member.setMemPhone(phone);
        member.setMemCity(city);
        member.setMemCityarea(cityarea);
        member.setMemAddress(address);

        // 如果上傳新圖片
        if (!avatarFile.isEmpty()) {
            try {
                member.setMemAvatar(avatarFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "圖片上傳失敗");
                return "member/edit_profile";
            }
        }

        memService.save(member); // 儲存更新

        session.setAttribute("loggedInMember", member); // 更新 session 中資料
        model.addAttribute("member", member);
        model.addAttribute("success", "資料已更新");
        return "member/edit_profile";
    }

}
