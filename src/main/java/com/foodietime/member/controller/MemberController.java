package com.foodietime.member.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.directmessage.model.DirectMessageService;
import com.foodietime.directmessage.model.DirectMessageVO;
import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/front/member")
public class MemberController {

    @Autowired
    private MemService memService;
    
    @Autowired
    private DirectMessageService dmService;
    
    
    @Autowired
    private StoreCateService storeCateSvc;

    
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
    	 // 先取得會員
        MemberVO member = (MemberVO) session.getAttribute("registeringStore");
        if (member == null) {
            // 沒登入，或流程錯誤，回會員登入
            return "redirect:/front/member/login";
        }
        if (member.getMemStatus() == MemberVO.MemberStatus.INACTIVE) {
            model.addAttribute("errorMessage", "請先完成帳號啟用，才能註冊店家！");
            return "front/member/login";
        }
        model.addAttribute("store", new StoreVO());
        model.addAttribute("storeCateList", storeCateSvc.getAll	());
        System.out.println("準備為會員 ID " + member.getMemId() + " 建立店家資料");
        

        return "front/member/storeregister";
    }
    
    @PostMapping("/storeregister")
    public String processStoreForm(@Valid @ModelAttribute("store") StoreVO store,
                                   BindingResult result,
                                   @RequestParam("photoFile") MultipartFile photoFile,
                                   HttpSession session,
                                   Model model) throws IOException {

        if (result.hasErrors()) {
        	model.addAttribute("storeCateList", storeCateSvc.getAll());
            return "front/member/storeregister";
        }
        
        // 設定預設值
        store.setStorStatus((byte) 2); // 未上架
        store.setStorReportCount((byte) 0);
        store.setStarNum(0);
        store.setReviews(0);
        store.setStorOpen((byte) 1); // 預設營業
        
        // 處理圖片
        if (photoFile != null && !photoFile.isEmpty()) {
            store.setStorPhoto(photoFile.getBytes());
        }
        
        // 取出會員
        MemberVO member = (MemberVO) session.getAttribute("registeringStore");
        if (member != null) {
            store.setStorEmail(member.getMemEmail());
            // 如果你之後有 FK，可以寫 store.setMember(member);
        }

//        storeService.addStore(store); // ✅ 儲存 storeVO
        
        session.removeAttribute("registeringStore"); // 記得清掉！
        return "redirect:/front/member/member_center";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
    	if (!model.containsAttribute("member")) {
    	    model.addAttribute("member", prepareMember());
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
        
        // 產生驗證碼
        String verificationCode = String.valueOf((int)((Math.random() * 900000) + 100000)); // 6 位數
        member.setMemCode(verificationCode); // 存進 VO

        // 暫存
        session.setAttribute("pendingMember", member);
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("isStore", isStore);

        // 寄信
        memService.sendVerificationEmail(member.getMemEmail(), verificationCode);

        return "front/member/verify";
   
    }
    
    @PostMapping("/verify")
    public String verifyEmail(@RequestParam("code") String codeInput, HttpSession session, Model model) {
    	String codeSent = (String) session.getAttribute("verificationCode");
        MemberVO pendingMember = (MemberVO) session.getAttribute("pendingMember");
        
        
        // 🌟 存 pendingIsStore，給 activate 判斷
        Boolean isStore = (Boolean) session.getAttribute("isStore");
        session.setAttribute("pendingIsStore", isStore);
        session.removeAttribute("isStore"); // 清掉原本的
        
        
        if (codeSent == null || pendingMember == null) {
            model.addAttribute("error", "驗證流程已過期，請重新註冊");
            return "redirect:/front/member/register";
        }

        if (!codeSent.equals(codeInput)) {
            model.addAttribute("error", "驗證碼錯誤，請重新輸入");
            return "front/member/verify";
        }

        // 通過驗證碼 → 產生啟用碼
        String activationCode = UUID.randomUUID().toString();
        pendingMember.setMemCode(activationCode);
        pendingMember.setMemStatus(MemberVO.MemberStatus.INACTIVE);

        memService.save(pendingMember);

        // 寄 activation link
        String activationLink = "http://localhost:8080/front/member/activate?code=" + activationCode;
        memService.sendActivationEmail(pendingMember.getMemEmail(), activationLink);

        // 清除 session
        session.removeAttribute("pendingMember");
        session.removeAttribute("verificationCode");
        
        

        model.addAttribute("email", pendingMember.getMemEmail());
        return "front/member/activation_notice";  // 提示會員去點啟用信
    }
    
    @GetMapping("/activate")
    public String activateAccount(@RequestParam("code") String code, Model model, HttpSession session) {
        MemberVO member = memService.getByMemCode(code);

        // 🌟 Case 1：該 code 已被清除，但帳號已啟用 → 直接顯示成功頁
        if (member == null) {	
        	// 嘗試 fallback
            String fallbackEmail = (String) session.getAttribute("lastActivatedEmail");
            if (fallbackEmail != null) {
                MemberVO fallbackMember = memService.getByMemEmail(fallbackEmail);
                if (fallbackMember != null && fallbackMember.getMemStatus() == MemberVO.MemberStatus.ACTIVE) {
                    session.setAttribute("loggedInMember", fallbackMember);
                    model.addAttribute("nickname", fallbackMember.getMemNickname());
                    return "front/member/activation_success";
                }
            }
            model.addAttribute("error", "啟用失敗，啟用碼無效！");
            session.removeAttribute("pendingIsStore"); // 確保清掉
            session.removeAttribute("lastActivatedEmail");
            return "front/member/activation_failed";
        }
        
        // 🌟 Case 2：該帳號已經啟用過 → 再點一次，不要失敗，直接當作成功
        if (member.getMemStatus() == MemberVO.MemberStatus.ACTIVE) {
            session.setAttribute("loggedInMember", member);
            model.addAttribute("nickname", member.getMemNickname());
            return "front/member/activation_success";
        }

        // Case 3：正常啟用流程
        member.setMemStatus(MemberVO.MemberStatus.ACTIVE);
        member.setMemCode(null);  // 清掉啟用碼
        memService.save(member);

        session.setAttribute("loggedInMember", member);
        model.addAttribute("nickname", member.getMemNickname());
        
        // 記住 fallback email
        session.setAttribute("lastActivatedEmail", member.getMemEmail());

        

        // 🌟 用 pendingIsStore
        Boolean isStore = (Boolean) session.getAttribute("pendingIsStore");
        if (Boolean.TRUE.equals(isStore)) {
            session.setAttribute("registeringStore", member);
            session.removeAttribute("pendingIsStore");  
            return "redirect:/front/member/storeregister";
        }
        return "front/member/activation_success";
    }

    
    @GetMapping("/login")
    public String showLoginForm(HttpSession session,Model model) {
    	   // 如果已經登入，直接回會員中心
        if (session.getAttribute("loggedInMember") != null) {
            return "redirect:/front/member/member_center";
        }
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
        
        if (member.getMemStatus() != MemberVO.MemberStatus.ACTIVE) {
            model.addAttribute("error", "帳號尚未啟用，請至信箱啟用！");
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
    
    @GetMapping("/messages")
    public String viewMessages(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }

        List<DirectMessageVO> messages = dmService.getMessagesByMemberId(member.getMemId());
        model.addAttribute("messages", messages);

        return "front/member/messages"; // 這頁就是 messages.html
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session,HttpServletResponse response) {
        session.invalidate();
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return "redirect:/index";
    }
    
    @GetMapping("/memcoupon/member-coupons")
    public String showMemberCoupons(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }

        return "front/memcoupon/member-coupons"; // 對應 templates/front/memcoupon/member-coupons.html
    }


}
