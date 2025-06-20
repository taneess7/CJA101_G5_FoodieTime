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
import java.util.Set;
import java.util.stream.Collectors;

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
     * 【核心修改】處理從購物車提交的結帳請求 (POST)
     * 這個方法現在會接收被勾選的商品ID，並進行店家驗證。
     *
     * @param selectedItems      參數用途：從前端表單接收到的、被勾選的購物車項目ID列表 (name="selectedItems")。
     *                           資料來源：cart.html 中的複選框。
     * @param session            參數用途：用於獲取會員資訊及在不同請求間傳遞數據。
     * @param redirectAttributes 參數用途：用於在重定向時傳遞提示訊息給前端。
     * @return 重定向到結帳頁面或返回購物車（如果驗證失敗）。
     */
    @PostMapping("/checkout")
    public String processCheckout(@RequestParam(name = "selectedItems", required = false) List<Integer> selectedItems,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        // -------------------- 步驟1：安全性與有效性檢查 --------------------
        if (selectedItems == null || selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "請至少選擇一項商品進行結帳！");
            return "redirect:/cart/cart";
        }

        // -------------------- 步驟2：獲取勾選商品的詳情 --------------------
        List<CartVO> selectedCartItems = cartService.getCartItemsByIds(selectedItems);

        // -------------------- 步驟3：店家驗證邏輯 --------------------
        Set<Integer> uniqueStoreIds = selectedCartItems.stream()
                .map(cartVO -> cartVO.getProduct().getStore().getStorId())
                .collect(Collectors.toSet());

        if (uniqueStoreIds.size() > 1) {
            redirectAttributes.addFlashAttribute("errorMessage", "一次只能結帳一家店的商品喔！");
            return "redirect:/cart/cart";
        }

        // -------------------- 步驟4：將驗證通過的商品列表存入 Session --------------------
        session.setAttribute("checkoutItems", selectedCartItems);

        // 使用 Post-Redirect-Get 模式，重定向到顯示結帳頁面的 GET 請求
        return "redirect:/orders/checkout";
    }

    /**
     * 【核心修改】顯示結帳頁面 (GET)
     * 這個方法現在從 Session 中讀取經過驗證的商品數據，而不是直接查詢資料庫。
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // -------------------- 步驟1：從 Session 讀取已驗證的商品 --------------------
        List<CartVO> checkoutItems = (List<CartVO>) session.getAttribute("checkoutItems");

        if (checkoutItems == null || checkoutItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "無結帳商品，請從購物車重新選擇。");
            return "redirect:/cart/cart";
        }

        // -------------------- 步驟2：基於 Session 中的商品計算金額 --------------------
        int totalAmount = checkoutItems.stream()
                .mapToInt(cart -> cart.getProduct().getProdPrice() * cart.getProdN())
                .sum();
        int shippingFee = totalAmount >= 500 ? 0 : 60;
        int finalTotal = totalAmount + shippingFee;

        // -------------------- 步驟3：將數據傳遞給前端頁面 --------------------
        model.addAttribute("checkoutItems", checkoutItems); // 將過濾後的列表傳給前端
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("finalTotal", finalTotal);
        model.addAttribute("orderData", new OrdersVO()); // 為表單綁定提供一個空物件

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
        public String placeOrder(@ModelAttribute("orderData") OrdersVO newOrderData,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {

            // -------------------- 步驟1：獲取會員和已驗證的商品列表 --------------------
            MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
            if (memberVO == null) {
                return "redirect:/cart/login";
            }
            Integer currentMemberId = memberVO.getMemId();

            // 【核心修正】從 Session 中取出已驗證的商品列表
            List<CartVO> checkoutItems = (List<CartVO>) session.getAttribute("checkoutItems");

            if (checkoutItems == null || checkoutItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "結帳已逾時或無商品，請重新操作。");
                return "redirect:/cart/cart";
            }

            // -------------------- 步驟2：調用修改後的 Service 方法 --------------------
            try {
                // 將 checkoutItems 傳遞給 Service
                OrdersVO createdOrder = ordersService.createOrderFromCart(currentMemberId, newOrderData, checkoutItems);

                // -------------------- 步驟3：成功後清理 Session 並重定向 --------------------
                session.removeAttribute("checkoutItems"); // 清除暫存
                redirectAttributes.addFlashAttribute("successMessage", "訂單已成功建立！");
                redirectAttributes.addAttribute("orderId", createdOrder.getOrdId());
                return "redirect:/orders/order-confirmation";

            } catch (IllegalStateException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/orders/checkout"; // 返回結帳頁面顯示錯誤
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "建立訂單時發生未知錯誤。");
                return "redirect:/orders/checkout";
            }
        }
}
