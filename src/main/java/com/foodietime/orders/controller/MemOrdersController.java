package com.foodietime.orders.controller;

import com.foodietime.member.model.MemberVO;
import com.foodietime.orders.model.OrdersService;
import com.foodietime.orders.model.OrdersVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 會員個人訂單管理 Controller。
 * 負責處理會員查看自己訂單記錄的相關請求。
 */
@Controller
@RequestMapping("/member/orders")
public class MemOrdersController {

    private final OrdersService ordersService;

    @Autowired
    public MemOrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    /**
     * 顯示會員的個人訂單記錄頁面。
     * 此方法會獲取會員的所有訂單，並將它們分類為「進行中訂單」和「歷史訂單」。
     *
     * @param model   參數用途：Spring MVC 的 Model 物件，用於將後端資料傳遞給前端視圖。
     *                資料來源：由 Spring 框架自動注入。
     * @param session 參數用途：HTTP Session 物件，用於獲取當前登入的會員資訊，確保資料安全性。
     *                資料來源：從當前的 HTTP 請求中獲取。
     * @return 訂單記錄頁面的視圖路徑 (String)。如果會員未登入，則重導向至登入頁面。
     */
    @GetMapping
    public String showMemberOrders(Model model, HttpSession session) {
        // ==================== 1. 安全性檢查：從 Session 獲取登入會員 ====================
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            // 若 session 中找不到會員資訊，表示尚未登入，重導至登入頁面
            return "redirect:/login"; // 假設登入頁面路徑為 /login
        }

        // ==================== 2. 業務邏輯：呼叫 Service 獲取會員所有訂單 ====================
        // 透過 OrdersService 中的 getOrdersByMemberId 方法安全地查詢訂單
        List<OrdersVO> allOrders = ordersService.getOrdersByMemberId(member.getMemId());

        // ==================== 3. 資料處理：將訂單分類為「進行中」與「歷史訂單」====================
        // 參考商家訂單管理邏輯，定義進行中訂單的狀態碼 (0:待處理, 1:已接單, 2:準備中)
        List<Integer> currentStatusList = List.of(0, 1, 2);
        List<OrdersVO> currentOrders = allOrders.stream()
                .filter(order -> currentStatusList.contains(order.getOrderStatus()))
                .collect(Collectors.toList());

        // 歷史訂單的狀態碼 (3:已完成, 4:已取消)
        List<Integer> historicalStatusList = List.of(3, 4);
        List<OrdersVO> historicalOrders = allOrders.stream()
                .filter(order -> historicalStatusList.contains(order.getOrderStatus()))
                .collect(Collectors.toList());

        // ==================== 4. 資料渲染：將分類後的訂單列表加入 Model ====================
        // 將處理好的兩個列表放入 Model，供 Thymeleaf 模板使用
        // 屬性名稱 "currentOrders" 和 "historicalOrders" 必須與 HTML 中的 th:if 和 th:each 變數名對應
        model.addAttribute("currentOrders", currentOrders);
        model.addAttribute("historicalOrders", historicalOrders);

        // ==================== 5. 畫面導向：返回會員訂單頁面的視圖路徑 ====================
        return "front/memberorders/member-orders"; // 指定 Thymeleaf 模板的路徑
    }
}