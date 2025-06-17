package com.foodietime.member.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.member.model.MemberVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.foodietime.member.model.MemService;

@Controller
@RequestMapping("/front/member")
public class MemberController {

    @Autowired
    private MemService memService;
    
    @Autowired
    private StoreService storeService;

    
    // ✅ 初始化預設值（讓驗證不會失敗）
    @ModelAttribute("member")
    public MemberVO prepareMember() {
        MemberVO member = new MemberVO();
        member.setMemStatus(MemberVO.MemberStatus.ACTIVE);
        member.setMemNoSpeak(MemberVO.NoSpeakStatus.INACTIVE);
        member.setMemNoPost(MemberVO.NoPostStatus.INACTIVE);
        member.setMemNoGroup(MemberVO.NoGroupStatus.INACTIVE);
        member.setMemNoJoingroup(MemberVO.NoJoingroupStatus.INACTIVE);
        member.setTotalStarNum(0);
        member.setTotalReviews(0);
        member.setMemTime(Timestamp.from(Instant.now()));
        return member;
    }
    
    @GetMapping("/storeregister")
    public String showStoreForm(HttpSession session, Model model) {
        model.addAttribute("store", new StoreVO());
        
        MemberVO member = (MemberVO) session.getAttribute("registeringStore");
        if (member != null) {
            System.out.println("準備為會員 ID " + member.getMemId() + " 建立店家資料");
            // 你也可以在 StoreVO 中預設 storEmail = member.getMemEmail() 這類邏輯
        }

        return "front/member/storeregister";
    }
    
    @PostMapping("/storeregister")
    public String processStoreForm(@ModelAttribute("store") StoreVO store,
                                   BindingResult result,
                                   Model model) {

        if (result.hasErrors()) {
            return "front/member/storeregister";
        }

        storeService.addStore(store); // ✅ 儲存 storeVO
        return "redirect:/front/member/success";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
    	 if (!model.containsAttribute("member")) {
    	        MemberVO member = new MemberVO();
    	        member.setMemStatus(MemberVO.MemberStatus.ACTIVE);
    	        member.setMemNoSpeak(MemberVO.NoSpeakStatus.INACTIVE);
    	        member.setMemNoPost(MemberVO.NoPostStatus.INACTIVE);
    	        member.setMemNoGroup(MemberVO.NoGroupStatus.INACTIVE);
    	        member.setMemNoJoingroup(MemberVO.NoJoingroupStatus.INACTIVE);
    	        member.setTotalStarNum(0);
    	        member.setTotalReviews(0);
    	        member.setMemTime(Timestamp.from(Instant.now()));
    	        model.addAttribute("member", member); // 這行不能少！
    	    }
        return "front/member/register"; // 對應 src/main/resources/templates/member/register.html
    }

    @PostMapping("/register")
    public String register( @Valid @ModelAttribute("member") MemberVO member,
    		 				BindingResult result,
    		 				 @RequestParam(value = "confirmPassword") String confirmPassword,
    						@RequestParam(value = "mem_avatar", required = false) MultipartFile memAvatarFile,
                           @RequestParam(value = "isStore", required = false) Boolean isStore,
                           HttpSession session,
                           Model model) throws IOException {
    	
    	 System.out.println("【DEBUG】是否有錯誤: " + result.hasErrors());
        if (result.hasErrors()) {
            return "front/member/register";
        }
        
        // ✅ 密碼與確認密碼不一致時手動加錯誤訊息
        if (member.getMemPassword() == null || !member.getMemPassword().equals(confirmPassword)) {
            model.addAttribute("passwordError", "兩次輸入的密碼不一致");
            return "front/member/register";
        }
    	
    	// 帳號或 Email 重複檢查
        if (memService.isAccountExists(member.getMemAccount())) {
            model.addAttribute("member", member);
            model.addAttribute("error", "帳號已存在，請選擇其他帳號");
            return "front/member/register";
        }
        if (memAvatarFile != null && !memAvatarFile.isEmpty()) {
    	    member.setMemAvatar(memAvatarFile.getBytes());
    	}
        
        
        if (memService.isEmailExists(member.getMemEmail())) {
            model.addAttribute("member", member);
            model.addAttribute("error", "Email 已註冊過，請使用其他信箱");
            return "front/member/register";
        }

        
        memService.save(member);

        session.setAttribute("loggedInMember", member);
        model.addAttribute("nickname", member.getMemNickname());
  
        
        if (Boolean.TRUE.equals(isStore)) {
        	session.setAttribute("registeringStore", member); // 傳遞到 store 註冊
            return "redirect:/front/member/storeregister"; // ⬅️ 第二階段：前往填寫店家資料
        }

        return "front/member/success"; // ⬅️ 原有流程：一般會員註冊成功
    }

    @GetMapping("/success")
    public String registerSuccess(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member != null) {
            model.addAttribute("nickname", member.getMemNickname());
        }
        return "front/member/success";
    }
    
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("member", new MemberVO()); // 可加可不加
        return "front/member/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("mem_account") String account,
            @RequestParam("mem_password") String password,
            @RequestParam(value = "isStore", required = false) String isStore,
            HttpSession session,
            Model model) {

        MemberVO member = memService.login(account, password); // 會員驗證

        if (member == null) {
            model.addAttribute("error", "帳號或密碼錯誤！");
            return "front/member/login";
        }

        // 登入成功，儲存會員資訊
        session.setAttribute("loggedInMember", member);

        // ✅ 是否以店家身份登入
        boolean loginAsStore = isStore != null;
        if (loginAsStore) {
            session.setAttribute("loginRole", "store");  // 設定身份為店家
            return "redirect:/store/";         // 導向店家頁面
        } else {
            session.setAttribute("loginRole", "member"); // 設定身份為一般會員
            return "redirect:/front/member/member_center";              // 導向會員頁面
        }
    }




    @GetMapping("/edit_profile")
    public String showEditForm(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }

        // 重新查詢最新資料（保險做法）
        MemberVO fullMember = memService.getById(member.getMemId());
        model.addAttribute("member", fullMember);
        return "front/member/edit_profile"; 
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
            return "front/member/edit_profile";
        }

        member.setMemNickname(nickname);
        member.setMemName(name);
        member.setMemPhone(phone);
        member.setMemCity(city);
        member.setMemCityarea(cityarea);
        member.setMemAddress(address);
        
        // ✅ 處理大頭照（如果有上傳
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                member.setMemAvatar(avatarFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "圖片上傳失敗");
                return "front/member/edit_profile";
            }
        }

        memService.save(member); // 儲存更新

        session.setAttribute("loggedInMember", member); // 更新 session 中資料
        model.addAttribute("member", member);
        model.addAttribute("success", "資料已更新");
        return "front/member/member_center";
    }
    
    @GetMapping("/avatar/{memId}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Integer memId) {
        MemberVO member = memService.getById(memId);
        
        if (member == null || member.getMemAvatar() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 根據實際圖片格式調整
        return new ResponseEntity<>(member.getMemAvatar(), headers, HttpStatus.OK);
    }
    
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    @GetMapping("/member_center")
    public String showMemberCenter(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }
        model.addAttribute("member", member);
        return "front/member/member_center"; // 要有這個 HTML
    }

}
