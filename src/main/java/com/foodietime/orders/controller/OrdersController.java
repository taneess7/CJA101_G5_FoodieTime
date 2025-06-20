package com.foodietime.orders.controller;

import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.orders.model.OrdersService;
import com.foodietime.orders.model.OrdersVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final CartService cartService;
    private final OrdersService ordersService;

    @Autowired
    public OrdersController(CartService cartService, OrdersService ordersService) {
        this.cartService = cartService;
        this.ordersService = ordersService;
    }

    /** ========================================================================================================================================= **/
    /** ========================================================================================================================================= **/
    /**
     * 處理前往結帳頁面的請求
     * @param model 用於將數據傳遞到視圖 (View)
     * @param redirectAttributes 用於在重定向時傳遞快閃屬性 (Flash Attribute)
     * @return 結帳頁面的視圖路徑，或重定向到購物車頁面
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        // ================== 步驟1：獲取當前會員ID ==================
        // 步驟1：從 session 中取出完整的 MemberVO 物件
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");

        // 步驟2：從 MemberVO 物件中取得會員 ID
        Integer currentMemberId = memberVO.getMemId(); // 假設 getter 方法是 getMemId()


        try {
            // ================== 步驟2：從購物車服務獲取數據 ==================
            List<CartVO> cartList = cartService.getByMemId(currentMemberId);
            model.addAttribute("cartListData", cartList);

            // ================== 步驟3：處理空購物車的情況 ==================
            if (cartList.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "您的購物車是空的，無法結帳。");
                return "redirect:/cart/cart";
            }

            // ================== 步驟4：計算訂單摘要所需金額 ==================
            Integer totalAmount = cartService.calculateTotalAmount(currentMemberId);
            model.addAttribute("totalAmount", totalAmount);
            Integer shippingFee = totalAmount >= 500 ? 0 : 60;
            model.addAttribute("shippingFee", shippingFee);
            Integer finalTotal = totalAmount + shippingFee;
            model.addAttribute("finalTotal", finalTotal);

            // ================== 【修改點】新增一個空的 OrdersVO 物件到 model ==================
            // 說明：此物件將作為表單的後端綁定物件 (form-backing bean)。
            // Thymeleaf 的 th:object 將會使用名為 "orderData" 的這個物件。
            model.addAttribute("orderData", new OrdersVO());

        } catch (Exception e) {
            // ================== 步驟5：處理潛在的異常 ==================
            model.addAttribute("error", "載入結帳頁面時發生錯誤：" + e.getMessage());
            model.addAttribute("cartListData", new ArrayList<>());
            // 即使出錯，也提供一個空的物件以避免 Thymeleaf 渲染錯誤
            model.addAttribute("orderData", new OrdersVO());
        }

        // ================== 步驟6：返回結帳頁面的視圖名稱 ==================
        return "/front/cart/checkout";
    }
    /** ========================================================================================================================================= **/
    /** ========================================================================================================================================= **/
    /**
     * 處理訂單確認頁面的請求。
     * 根據傳入的訂單ID顯示該訂單的所有詳細資訊。
     *
     * @param orderId 參數用途：要顯示的訂單的ID。由 placeOrder 方法透過 RedirectAttributes 傳遞。
     *                資料來源：URL 中的查詢參數或路徑變數。
     * @param model   參數用途：用於將訂單數據傳遞到視圖 (View)。
     * @return String 參數用途：訂單確認頁面的視圖路徑。
     */
    @GetMapping("/order-confirmation")
    public String showOrderConfirmation(@RequestParam(required = false) Integer orderId, Model model) {
        // ================== 步驟1：驗證 orderId 是否存在 ==================
        if (orderId == null) {
            // 如果沒有 orderId，可以選擇重定向到首頁、購物車頁面，或顯示錯誤訊息
            model.addAttribute("error", "找不到訂單資訊。");
            return "front/error"; // 或者 "redirect:/cart/cart"
        }

        // ================== 步驟2：透過 OrdersService 獲取訂單詳情 ==================
        Optional<OrdersVO> orderOptional = ordersService.getOrderById(orderId);

        if (orderOptional.isEmpty()) {
            model.addAttribute("error", "找不到訂單ID " + orderId + " 的詳細資訊。");
            return "/front/cart/cart";
        }

        OrdersVO order = orderOptional.get();
        model.addAttribute("order", order);

        // ================== 步驟3：計算運費 (因為 OrdersVO 中沒有單獨的運費欄位，需推導) ==================
        // 運費 = 實付金額 - 商品總金額 + 活動優惠金額 + 優惠券優惠金額
        Integer shippingFee = order.getActualPayment() - order.getOrdSum() + order.getPromoDiscount() + order.getCouponDiscount();
        model.addAttribute("shippingFee", shippingFee);

        // ================== 步驟4：返回訂單確認頁面的視圖名稱 ==================
        return "/front/cart/order-confirmation";

    }
    /** ========================================================================================================================================= **/
    /** ========================================================================================================================================= **/
        /**
         * 處理用戶提交的訂單
         * @param newOrderData 參數：使用 @ModelAttribute 綁定表單數據到 OrdersVO 物件。
         *                     Thymeleaf 表單中的 th:object 必須對應這裡的 model attribute 名稱 ("orderData")。
         * @param redirectAttributes 參數：用於重定向時傳遞成功或失敗的訊息
         * @param session 參數：用於獲取當前登入的會員資訊
         * @return 重定向到訂單確認頁或返回結帳頁
         */
    @PostMapping("/placeOrder")
    public String placeOrder(@ModelAttribute("orderData") OrdersVO newOrderData, // 【修改點】用 @ModelAttribute 取代多個 @RequestParam
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        // ================== 步驟1：獲取當前會員ID ==================
        // 步驟1：從 session 中取出完整的 MemberVO 物件
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");

        // 步驟2：從 MemberVO 物件中取得會員 ID
        Integer currentMemberId = memberVO.getMemId(); // 假設 getter 方法是 getMemId()

        // 【修改點】不再需要手動創建 OrdersVO 或設置從表單傳來的屬性，
        // 因為 Spring MVC 已經透過 @ModelAttribute 為我們完成了數據綁定。
        // OrdersVO newOrderData = new OrdersVO();
        // newOrderData.setOrdAddr(ordAddr); ... 等都不再需要

        // ================== 步驟2：調用Service層處理訂單創建的核心業務邏輯 ==================
        /*
         * 說明：
         * Service 層現在接收一個已經填充好表單數據的 OrdersVO 物件。
         * 這樣 Controller 的職責更清晰，只負責接收請求和數據轉發。
         */
        OrdersVO createdOrder = ordersService.createOrderFromCart(currentMemberId, newOrderData);

        // ================== 步驟3：成功後重定向到訂單確認頁面 ==================
        redirectAttributes.addFlashAttribute("successMessage", "訂單已成功建立！");
        redirectAttributes.addAttribute("orderId", createdOrder.getOrdId());
        return "redirect:/orders/order-confirmation";

        // 異常處理可以保持不變或根據業務需求調整
    }
}
