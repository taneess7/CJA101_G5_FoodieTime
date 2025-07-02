package com.foodietime.controller;

import com.foodietime.cart.model.CartService;
import com.foodietime.member.model.MemberVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartService cartService; // 注入購物車 Service

    @Autowired
    private HttpSession session; // 注入 HttpSession 來獲取登入資訊

    /**
     * 將共通的 Model 屬性添加到所有請求的 Model 中。
     * 此方法會在任何 Controller 的請求處理方法執行前被呼叫。
     *
     * @param model Spring MVC 的 Model 物件，用於傳遞資料到視圖
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // ==================== 1. 取得當前登入會員資訊 ====================
        // 從 Session 中獲取登入時儲存的 MemberVO 物件
        MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");

        // 如果會員已登入，則進行後續處理
        if (loginMember != null) {
            // ==================== 2. 計算購物車商品總數 ====================
            // 呼叫 Service 層方法計算該會員的購物車商品總數
            Integer cartItemCount = cartService.getTotalItemCountByMemberId(loginMember.getMemId());

            // ==================== 3. 將資料加入全域 Model ====================
            // 將會員物件本身加入 Model，方便 Thymeleaf 直接取用，例如 ${loginMember.memName}
            model.addAttribute("loginMember", loginMember);

            // 將購物車數量加入 Model，即使為 null 或 0 也加入，讓前端能正確處理
            model.addAttribute("cartItemCount", cartItemCount);
        }
    }
}
