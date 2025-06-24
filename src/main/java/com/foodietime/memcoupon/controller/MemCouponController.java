package com.foodietime.memcoupon.controller;

import com.foodietime.member.model.MemberVO;
import com.foodietime.memcoupon.model.MemCouponService;
import com.foodietime.memcoupon.model.MemCouponVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
}

