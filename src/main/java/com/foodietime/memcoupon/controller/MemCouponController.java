package com.foodietime.memcoupon.controller;

import com.foodietime.member.model.MemberVO;
import com.foodietime.memcoupon.dto.CouponClaimRequest;
import com.foodietime.memcoupon.model.MemCouponService;
import com.foodietime.memcoupon.model.MemCouponVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemCouponController {


    private final MemCouponService memCouponService;

    @Autowired
    public MemCouponController(MemCouponService memCouponService) {
        this.memCouponService = memCouponService;
    }

    /**
     * @param model  Spring MVC 的 Model 物件，用於將資料傳遞到視圖
     * @param status 篩選狀態，從 URL 查詢參數 (?status=...) 獲取
     * @return 視圖的名稱 (Thymeleaf 模板檔案)
     */
    @GetMapping("/coupons")
    public String showCouponsPage(Model model,
                                  HttpSession session,
                                  @RequestParam(name = "status", defaultValue = "all") String status) {

        // ============================ 步驟 1: 獲取當前登入會員ID ============================

        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO == null) {
            return "redirect:/login";
        }
        Integer currentMemberId = memberVO.getMemId();
        model.addAttribute("memberVO", memberVO);
        // ============================ 步驟 2: 調用 Service 獲取優惠券資料 ============================
        List<MemCouponVO> memberCoupons = memCouponService.findCouponsByMemberAndStatus(currentMemberId, status);

        // ============================ 步驟 3: 將資料存入 Model 中，以便 Thymeleaf 使用 ============================
        // "memberCoupons" 是在 Thymeleaf 中使用的變數名稱
        model.addAttribute("memberCoupons", memberCoupons);
        // 將當前的篩選狀態也傳給前端，用於設定下拉選單的預設選中項
        model.addAttribute("currentFilter", status);

        model.addAttribute("now", new java.util.Date());

        // ============================ 步驟 4: 回傳視圖名稱 ============================
        // Spring Boot 會尋找位於 `src/main/resources/templates/front-end/member/member-coupons.html` 的模板
        return "front/memcoupon/member-coupons";
    }

    /**
     * 處理會員領取優惠券的 POST 請求。
     * @param request 包含要領取的優惠券 ID 的 DTO
     * @param session 從 HttpSession 中獲取當前登入的會員資訊
     * @return 返回 JSON 格式的成功/失敗訊息
     */
    @PostMapping("/coupons/claim")
    @ResponseBody // 表示此方法直接返回響應體，而不是視圖名稱
    public ResponseEntity<Map<String, Object>> claimCoupon(@RequestBody CouponClaimRequest request, HttpSession session) {
        // ==================== 1. 獲取當前登入會員 ====================
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO == null) {
            // 返回未授權錯誤碼
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "請先登入。"));
        }

        // ==================== 2. 調用 Service 處理領取邏輯 ====================
        String result = memCouponService.claimCoupon(memberVO, request.getCouponId());

        // ==================== 3. 根據 Service 返回的結果構建響應 ====================
        if ("SUCCESS".equals(result)) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.ok(Collections.singletonMap("message", result)); // 將 Service 返回的錯誤訊息傳遞給前端
        }
    }
}

