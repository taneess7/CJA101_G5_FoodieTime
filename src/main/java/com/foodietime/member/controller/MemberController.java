package com.foodietime.member.controller;

import java.io.IOException;
import jakarta.validation.Validator;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodietime.directmessage.model.DirectMessageDTO;
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
    private StoreService storeService;
    
    
    @Autowired
    private StoreCateService storeCateSvc;

    @Autowired
    private Validator validator;

    
    
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
        
        
        // ✅ 把會員 email 帶入 store
        StoreVO store = new StoreVO();
        store.setStorEmail(member.getMemEmail());  // ★★ 這一行是關鍵

        model.addAttribute("store", store);
        model.addAttribute("storeCateList", storeCateSvc.getAll	());
        System.out.println("準備為會員 ID " + member.getMemId() + " 建立店家資料");
        

        return "front/member/storeregister";
    }
    
    @PostMapping("/storeregister")
    public String processStoreForm(@Valid @ModelAttribute("store") StoreVO store,
                                   BindingResult result,
                                   @RequestParam("photoFile") MultipartFile photoFile,
                                   @RequestParam(value = "storOffDayList", required = false) List<String> storOffDayList,
                                   HttpSession session,
                                   Model model) throws IOException { 
    	// 設定預設值
    	if (store.getStorStatus() == null) store.setStorStatus((byte) 2); // 未上架
        if (store.getStorReportCount() == null) store.setStorReportCount((byte) 0);
        if (store.getStarNum() == null) store.setStarNum(0);
        if (store.getReviews() == null) store.setReviews(0);
        if (store.getStorOpen() == null) store.setStorOpen((byte) 1); // 預設營業
        
        if (result.hasErrors()) {
        	model.addAttribute("storeCateList", storeCateSvc.getAll());
            return "front/member/storeregister";
        }

        
        
        // ✅ 將 List<String> 公休日轉換為以逗號分隔的字串，存入 StoreVO
        if (storOffDayList != null && !storOffDayList.isEmpty()) {
            store.setStorOffDay(String.join(",", storOffDayList));
        } else {
            store.setStorOffDay("");
        }
        
       
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
        
        

        storeService.addStore(store); // ✅ 儲存 storeVO
        session.setAttribute("loggedInStore", store); // ✅ 新增這行：補上 store session
        session.removeAttribute("registeringStore"); // 記得清掉！
        return "redirect:/store/sc";
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
            model.addAttribute("member", member);
        }
        return "front/member/register";
    }


    @PostMapping("/register")
    public String register(	@ModelAttribute("member") MemberVO member,
    		 				BindingResult result,
    		 				 @RequestParam(value = "confirmPassword") String confirmPassword,
    						@RequestParam(value = "mem_avatar", required = false) MultipartFile memAvatarFile,
                           @RequestParam(value = "isStore", required = false) Boolean isStore,
                           HttpSession session,
                           Model model) throws IOException {
    	member.setMemStatus(MemberVO.MemberStatus.ACTIVE);
        member.setMemNoSpeak(MemberVO.NoSpeakStatus.INACTIVE);
        member.setMemNoPost(MemberVO.NoPostStatus.INACTIVE);
        member.setMemNoGroup(MemberVO.NoGroupStatus.INACTIVE);
        member.setMemNoJoingroup(MemberVO.NoJoingroupStatus.INACTIVE);
        member.setTotalStarNum(0);
        member.setTotalReviews(0);
        member.setMemTime(Timestamp.from(Instant.now()));

     // ✅ 自動驗證欄位
        Set<jakarta.validation.ConstraintViolation<MemberVO>> violations = validator.validate(member);
        for (var violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            result.rejectValue(field, "", message);
        }
    	 System.out.println("【DEBUG】是否有錯誤: " + result.hasErrors());  
    	 // ✅ 密碼與確認密碼不一致時手動加錯誤訊息
        if (member.getMemPassword() == null || !member.getMemPassword().equals(confirmPassword)) {
        	model.addAttribute("member", member);
            model.addAttribute("passwordError", "兩次輸入的密碼不一致");
            return "front/member/register";
        }
        if (result.hasErrors()) {
        	model.addAttribute("member", member);
        	result.getFieldErrors().forEach(err ->
            System.out.println("欄位錯誤: " + err.getField() + " -> " + err.getDefaultMessage()));
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

        return "redirect:/front/member/verify";
   
    }
    @GetMapping("/verify")
    public String showVerifyPage(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("pendingMember");
        if (member == null) {
            return "redirect:/front/member/register"; // 防止直接打網址
        }
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
        System.out.println("【DEBUG】activate - pendingIsStore = " + isStore);


        // fallback：若 session 遺失但該會員尚未建立店家 → 仍導去註冊
        if (Boolean.TRUE.equals(isStore) || storeService.findByStorEmail(member.getMemEmail()) == null) {
            session.setAttribute("registeringStore", member);
            session.removeAttribute("pendingIsStore");
            System.out.println("【DEBUG】導向店家註冊頁面");
            return "redirect:/front/member/storeregister";
        }
        return "front/member/activation_success";
    }


    @GetMapping("/login")
    public String showLoginForm(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
        // 如果已經登入，直接回會員中心
        if (session.getAttribute("loggedInMember") != null) {
            return "redirect:/index";
        }

        // ==================== ★★★【邏輯修改處】★★★ ====================
        // 1. 記錄登入前的來源頁面 (Referer)
        String referer = request.getHeader("Referer");

        if (session.getAttribute("redirectAfterLogin") == null && referer != null) {
            if (!referer.contains("/front/member/login")
                    && !referer.contains("/front/member/register")
                    && !referer.contains("/front/member/activate")
                    && !referer.contains("/front/member/verify")
            		&& !referer.contains("/favorite/")
            		&& !referer.contains("/gb/gbindex")) {
                session.setAttribute("redirectAfterLogin", referer);
            }
        }
        // ==============================================================

        // ✅ 告訴瀏覽器不要快取登入頁
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
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
        
        switch (member.getMemStatus()) {
        case INACTIVE -> {
            model.addAttribute("error", "帳號尚未啟用，請至信箱啟用！");
            return "front/member/login";
        }
        case DISABLED -> {
            model.addAttribute("error", "您的帳號已被停用，如有疑問請聯絡客服！");
            return "front/member/login";
        }
        case ACTIVE -> {
            // 通過檢查，繼續登入流程
        }
    }

        // 登入成功，儲存會員資訊
        session.setAttribute("loggedInMember", member);

        // ✅ 是否以店家身份登入
        boolean loginAsStore = "on".equals(isStore);
        if (loginAsStore) {
            
        	StoreVO store = storeService.findByStorEmail(member.getMemEmail()); // 假設你有這個方法
        	 // ✅ 檢查該會員是否有註冊店家，否則提示錯誤
            if (store == null) {
                model.addAttribute("error", "您尚未註冊店家，請先完成店家註冊流程");
                return "front/member/login";
            }
        	session.setAttribute("loginRole", "store");  // 設定身份為店家    
            session.setAttribute("loggedInStore", store); // ✅ 把 StoreVO 放入 Session
            
            return "redirect:/store/sc";         // 導向店家頁面
        } 
        
        // ✅ ✅ ✅ 檢查是否有記錄登入前的頁面
        String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
    	// ✅ 避免導向 reset-password 等一次性頁面
        if (redirectUrl != null && (
        		redirectUrl.contains("/reset-password") ||
        		redirectUrl.contains("/front/member/member_center") ||
        	    redirectUrl.contains("/front/member/edit_profile")
        	    )) {
            redirectUrl = null;
        }
        
        if (redirectUrl != null) {
            session.removeAttribute("redirectAfterLogin");
            return "redirect:" + redirectUrl;
        }

        return "redirect:/index"; // 預設頁面
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
        @Valid @ModelAttribute("member") MemberVO member,
        BindingResult result,
        @RequestParam("mem_avatar") MultipartFile avatarFile,
        RedirectAttributes redirectAttributes,
        HttpSession session,
        Model model
    ) {
        MemberVO sessionMember = (MemberVO) session.getAttribute("loggedInMember");
        if (sessionMember == null) {
            model.addAttribute("error", "請先登入");
            return "redirect:/front/member/login";
        }

       

        // 補齊必要欄位（避免前端修改關鍵欄位）
        member.setMemId(sessionMember.getMemId());
        member.setMemAccount(sessionMember.getMemAccount());
        member.setMemPassword(sessionMember.getMemPassword());
        member.setMemEmail(sessionMember.getMemEmail());
        member.setMemGender(sessionMember.getMemGender()); 
        member.setMemStatus(sessionMember.getMemStatus());
        member.setMemCode(sessionMember.getMemCode());
        member.setMemNoGroup(sessionMember.getMemNoGroup());
        member.setMemNoJoingroup(sessionMember.getMemNoJoingroup());
        member.setMemNoPost(sessionMember.getMemNoPost());
        member.setMemNoSpeak(sessionMember.getMemNoSpeak());
        member.setTotalReviews(sessionMember.getTotalReviews());
        member.setTotalStarNum(sessionMember.getTotalStarNum());
        member.setMemTime(sessionMember.getMemTime());
        
        // 如果有驗證錯誤
        if (result.hasErrors()) {
            // ✅ 補上原來的大頭照避免變成預設圖
            if ((member.getMemAvatar() == null || member.getMemAvatar().length == 0)
                    && sessionMember.getMemAvatar() != null) {
                member.setMemAvatar(sessionMember.getMemAvatar());
            }
            return "front/member/edit_profile";
        }


        // ✅ 上傳新圖片（有大小限制）
        if (avatarFile != null && !avatarFile.isEmpty()) {
            if (avatarFile.getSize() > 2 * 1024 * 1024) { // 2MB 上限
                model.addAttribute("error", "圖片大小不得超過 2MB");
                return "front/member/edit_profile";
            }
            try {
                member.setMemAvatar(avatarFile.getBytes());
            } catch (IOException e) {
                model.addAttribute("error", "圖片上傳失敗");
                return "front/member/edit_profile";
            }
        } else {
            // ✅ 沒有重新上傳圖片 → 保留舊圖
            member.setMemAvatar(sessionMember.getMemAvatar());
        }

        memService.save(member);
        session.setAttribute("loggedInMember", member);
        model.addAttribute("success", "資料已更新");
        System.out.println("更新成功！跳轉中");
        System.out.println("會員名稱: " + member.getMemName());
        System.out.println("Redirect 到 /front/member/member_center");
        return "redirect:/front/member/member_center";
        
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
        model.addAttribute("memberVO", member);
        model.addAttribute("currentPage", "member_center");
        return "front/member/member_center"; // 要有這個 HTML
    }
    
    @GetMapping("/messages")
    public String viewMessages(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front/member/login";
        }

        List<DirectMessageDTO> messages = dmService.getMessagesDtoByMemberId(member.getMemId());
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
    
    
    @GetMapping("/check-session")
    @ResponseBody
    public ResponseEntity<String> checkSession(HttpSession session) {
        if (session.getAttribute("loggedInMember") != null) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NOT_LOGGED_IN");
    }
    
    @PostMapping("/saveRedirectAfterLogin")
    @ResponseBody
    public void saveRedirectAfterLogin(@RequestParam("url") String url, HttpSession session) {
    	if (!url.matches(".*/(reset-password|verify|activate)(\\?.*)?")) {
    	    session.setAttribute("redirectAfterLogin", url);
    	}
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "front/member/forgot_password";
    }
    
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model, HttpSession session) {
        MemberVO member = memService.getByMemEmail(email);
        if (member == null) {
            model.addAttribute("error", "找不到此 Email 對應的帳號");
            return "front/member/forgot_password";
        }
        
        // 忘記密碼流程中加這段檢查
        if (member.getMemStatus() != MemberVO.MemberStatus.ACTIVE) {
            model.addAttribute("error", "您的帳號尚未啟用，請先至信箱啟用帳號");
            return "front/member/forgot_password";
        }


        // 產生 token 並保存
        String resetToken = UUID.randomUUID().toString();
        member.setMemCode(resetToken);
        memService.save(member);

        // 寄送 Email
        String resetLink = "http://localhost:8080/front/member/reset-password?token=" + resetToken;
        memService.sendResetPasswordEmail(email, resetLink);

        model.addAttribute("message", "重設密碼連結已寄出，請至信箱查看");
        return "front/member/forgot_password";
    }
    
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model,RedirectAttributes redirectAttributes) {
        MemberVO member = memService.getByMemCode(token);
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "重設連結已過期，請重新申請");
            return "redirect:/front/member/forgot-password";
        }
        model.addAttribute("token", token);
        return "front/member/reset_password";
    }
    
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       HttpSession session,
                                       Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "兩次密碼不一致");
            model.addAttribute("token", token);
            return "front/member/reset_password";
        }

        MemberVO member = memService.getByMemCode(token);
        if (member == null) {
            model.addAttribute("error", "無效或過期的重設連結");
            return "front/member/reset_password";
        }
        
        // ✅ 檢查是否與舊密碼相同
        if (member.getMemPassword() != null && member.getMemPassword().equals(password)) {
            model.addAttribute("error", "新密碼不能與舊密碼相同");
            model.addAttribute("token", token);
            return "front/member/reset_password";
        }

        member.setMemPassword(password); // 建議 hash 處理
        member.setMemCode(null); // 清除 token
        memService.save(member);
        
        session.invalidate();
        return "redirect:/front/member/login?resetSuccess=true";
    }





}
