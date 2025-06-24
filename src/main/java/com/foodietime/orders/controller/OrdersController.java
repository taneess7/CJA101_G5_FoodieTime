package com.foodietime.orders.controller;

import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.memcoupon.model.MemCouponService;
import com.foodietime.memcoupon.model.MemCouponVO;
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
    private final MemCouponService memCouponService;

    @Autowired
    public OrdersController(CartService cartService, OrdersService ordersService, MemCouponService memCouponService) {
        this.cartService = cartService;
        this.ordersService = ordersService;
        this.memCouponService = memCouponService;
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
    public String showCheckoutPage(Model model,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes,
                                   // 【新增】接收使用者選擇的優惠券 ID (用於更新預覽)
                                   @RequestParam(name = "selectedCouponId", required = false, defaultValue = "0") Integer selectedCouponId) {

        List<CartVO> checkoutItems = (List<CartVO>) session.getAttribute("checkoutItems");
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "無結帳商品，請從購物車重新選擇。");
            return "redirect:/cart/cart";
        }
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO == null) {
            return "redirect:/cart/login";
        }

        // 1. 計算商品總金額 (小計)
        Integer subtotalAmount = checkoutItems.stream().mapToInt(cart -> cart.getProduct().getProdPrice() * cart.getProdN()).sum();
        model.addAttribute("totalAmount", subtotalAmount); // 將小計金額傳給前端

        // 2. 計算運費
        Integer shippingFee = subtotalAmount >= 500 ? 0 : 60;
        model.addAttribute("shippingFee", shippingFee);

        // 3. 查詢可用優惠券
        Integer storeId = checkoutItems.get(0).getProduct().getStore().getStorId();
        List<MemCouponVO> availableCoupons = memCouponService.findAvailableCouponsByMemberAndStore(memberVO.getMemId(), storeId);
        model.addAttribute("availableCoupons", availableCoupons);

        // 4. 【核心】根據 selectedCouponId 預計算優惠券折扣和最終總計
        Integer couponDiscount = 0; // 初始化為 0
        Integer finalTotal = subtotalAmount + shippingFee; // 初始化為未折扣的總計
        Integer validatedCouponId = selectedCouponId;

        if (selectedCouponId != null && selectedCouponId > 0) {
            Optional<MemCouponVO> selectedCouponOpt = availableCoupons.stream()
                    .filter(mc -> mc.getMemCouId().equals(selectedCouponId))
                    .findFirst(); // 從已查詢出的可用優惠券中找

            if (selectedCouponOpt.isPresent()) {
                MemCouponVO selectedMemCoupon = selectedCouponOpt.get();
                // 再次驗證最低消費門檻 (防止直接從 URL 提交不符合門檻的優惠券 ID)
                if (subtotalAmount >= selectedMemCoupon.getCoupon().getCouMinOrd()) {
                    couponDiscount = selectedMemCoupon.getCoupon().getCouDiscount().intValue(); // 獲取折扣金額
                    finalTotal = finalTotal - couponDiscount;
                } else {
                    // 如果不滿足低消，將選中的優惠券 ID 設為 0，防止前端顯示錯誤折扣
                    validatedCouponId = 0;
                    model.addAttribute("errorMessage", "所選優惠券未達最低消費門檻，請重新選擇。");
                }
            }
        }

        model.addAttribute("couponDiscount", couponDiscount); // 將預期的優惠券折扣傳給前端
        model.addAttribute("finalTotal", Math.max(0, finalTotal)); // 確保總計不為負數

        model.addAttribute("checkoutItems", checkoutItems); // 傳遞購物車商品列表
        model.addAttribute("orderData", new OrdersVO());    // 傳遞一個空的 OrdersVO 物件用於表單綁定
        model.addAttribute("selectedCouponId", selectedCouponId); // 傳遞當前選中的優惠券ID，用於前端 select 預選

        model.addAttribute("now", new java.util.Date()); // 如果 HTML 中日期判斷仍使用，需要保留

        return "/front/cart/checkout"; // 返回結帳頁面
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
                                 // 【新增】接收從表單提交的優惠券ID
                                 @RequestParam(name = "selectedCouponId", required = false) Integer selectedCouponId,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {

            // ... (您原有的獲取會員和商品列表的邏輯不變)
            MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
            if (memberVO == null) return "redirect:/cart/login";
            List<CartVO> checkoutItems = (List<CartVO>) session.getAttribute("checkoutItems");
            if (checkoutItems == null || checkoutItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "結帳已逾時或無商品，請重新操作。");
                return "redirect:/cart/cart";
            }

            try {
                // ★★★ 將 selectedCouponId 傳遞給 Service ★★★
                OrdersVO createdOrder = ordersService.createOrderFromCart(memberVO.getMemId(), newOrderData, checkoutItems, selectedCouponId);

                // ... (您原有的成功後清理 Session 並重定向的邏輯不變)
                session.removeAttribute("checkoutItems");
                redirectAttributes.addFlashAttribute("successMessage", "訂單已成功建立！");
                redirectAttributes.addAttribute("orderId", createdOrder.getOrdId());
                return "redirect:/orders/order-confirmation";

            } catch (IllegalStateException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/orders/checkout";
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "建立訂單時發生未知錯誤。");
                return "redirect:/orders/checkout";
            }
        }
}
