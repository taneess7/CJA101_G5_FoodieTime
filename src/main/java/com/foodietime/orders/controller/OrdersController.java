package com.foodietime.orders.controller;

import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.orders.model.OrdersService;
import com.foodietime.orders.model.OrdersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 處理前往結帳頁面的請求
     * @param model 用於將數據傳遞到視圖 (View)
     * @param redirectAttributes 用於在重定向時傳遞快閃屬性 (Flash Attribute)
     * @return 結帳頁面的視圖路徑，或重定向到購物車頁面
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, RedirectAttributes redirectAttributes) {
        // ================== 步驟1：獲取當前會員ID ==================
        // 說明：在實際應用中，會員ID應從用戶Session或Spring Security上下文中獲取
        Integer currentMemberId = 1; // 根據需求，此處使用測試會員ID 1

        try {
            // ================== 步驟2：從購物車服務獲取數據 ==================
            List<CartVO> cartList = cartService.getByMemId(currentMemberId);
            model.addAttribute("cartListData", cartList);

            // ================== 步驟3：處理空購物車的情況 ==================
            if (cartList.isEmpty()) {
                // 如果購物車是空的，不應進入結帳頁面。重定向回購物車並顯示提示。
                redirectAttributes.addFlashAttribute("error", "您的購物車是空的，無法結帳。");
                return "redirect:/cart/cart";
            }

            // ================== 步驟4：計算訂單摘要所需金額 ==================
            // 商品小計
            Integer totalAmount = cartService.calculateTotalAmount(currentMemberId);
            model.addAttribute("totalAmount", totalAmount);

            // 運費（業務邏輯：滿500免運，未滿則運費60）
            Integer shippingFee = totalAmount >= 500 ? 0 : 60;
            model.addAttribute("shippingFee", shippingFee);

            // 訂單總計
            Integer finalTotal = totalAmount + shippingFee;
            model.addAttribute("finalTotal", finalTotal);

        } catch (Exception e) {
            // ================== 步驟5：處理潛在的異常 ==================
            model.addAttribute("error", "載入結帳頁面時發生錯誤：" + e.getMessage());
            // 即使出錯，也提供一個空列表，以防Thymeleaf渲染時出錯
            model.addAttribute("cartListData", new ArrayList<>());
        }

        // ================== 步驟6：返回結帳頁面的視圖名稱 ==================
        return "/front/cart/checkout";
    }
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
    /**
     * 處理用戶提交的訂單
     * @param ordAddr 參數：外送地址，來自表單 name="ordAddr"
     * @param payMethod 參數：付款方式，來自表單 name="payMethod"
     * @param deliver 參數：取餐方式，來自表單 name="deliver"
     * @param comment 參數：訂單備註，來自表單 name="comment"
     * @param redirectAttributes 參數：用於重定向時傳遞成功或失敗的訊息
     * @return 重定向到訂單確認頁或返回結帳頁
     */
    @PostMapping("/placeOrder")
    public String placeOrder(@RequestParam String ordAddr,
                             @RequestParam Integer payMethod,
                             @RequestParam Integer deliver,
                             @RequestParam(required = false) String comment,
                             RedirectAttributes redirectAttributes) {

        // ================== 步驟1：獲取當前會員ID ==================
        Integer currentMemberId = 1; // 同上，應從Session獲取

//        try {
            // ================== 步驟2：創建一個OrdersVO實例以收集表單數據 ==================
            OrdersVO newOrderData = new OrdersVO();
            newOrderData.setOrdAddr(ordAddr);
            newOrderData.setPayMethod(payMethod);
            newOrderData.setDeliver(deliver);
            newOrderData.setComment(comment);

            // ================== 步驟3：調用Service層處理訂單創建的核心業務邏輯 ==================
            /*
             * 說明：
             * 一個健壯的系統會將複雜的業務邏輯封裝在Service層。
             * OrderService應負責：
             * 1. 事務管理(Transaction)：確保訂單(Orders)和訂單明細(Order_Details)要麼全部成功，要麼全部失敗。
             * 2. 數據驗證：再次確認購物車數據的有效性。
             * 3. 數據整合：從購物車數據轉換為OrdersVO和OrdDetVO。
             * 4. 數據庫操作：保存訂單和訂單明細。
             * 5. 後續處理：清空購物車。
             */
            OrdersVO createdOrder = ordersService.createOrderFromCart(currentMemberId, newOrderData);

            // ================== 步驟4：成功後重定向到訂單確認頁面 ==================
            redirectAttributes.addFlashAttribute("successMessage", "訂單已成功建立！");
            // 將新生成的訂單ID傳遞給確認頁面
            redirectAttributes.addAttribute("orderId", createdOrder.getOrdId());
            return "redirect:/cart/order-confirmation";

//        }
//        catch (IllegalStateException e) {
//            // ================== 步驟5：處理可預期的業務異常 (如購物車為空) ==================
//            redirectAttributes.addFlashAttribute("error", "訂單建立失敗：" + e.getMessage());
//            return "redirect:/cart/checkout";
//        }
//         catch (Exception e) {
//            // ================== 步驟6：處理未預期的系統異常 ==================
//            e.printStackTrace(); // 在後台記錄詳細錯誤，方便開發者調試
//            redirectAttributes.addFlashAttribute("error", "系統發生未知錯誤，請稍後再試。");
//            return "redirect:/cart/checkout";
//        }
    }
}
