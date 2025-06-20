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

        // ================== 步驟1：安全性與有效性檢查 ==================
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO == null) {
            return "redirect:/cart/login";
        }

        if (selectedItems == null || selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "請至少選擇一項商品進行結帳！");
            return "redirect:/cart/cart";
        }

        // ================== 步驟2：獲取被選中的購物車項目詳情 ==================
        // 我們需要一個新方法來只獲取被選中的項目，而不是整個購物車
        List<CartVO> selectedCartItems = cartService.getCartItemsByIds(selectedItems);

        // ================== 步驟3：【店家驗證邏輯】 ==================
        // 使用 Set 來收集所有不重複的店家 ID
        Set<Integer> uniqueStoreIds = selectedCartItems.stream()
                .map(cartVO -> cartVO.getProduct().getStore().getStorId())
                .collect(Collectors.toSet());

        // 如果 Set 的大小大於 1，代表商品來自多個店家
        if (uniqueStoreIds.size() > 1) {
            redirectAttributes.addFlashAttribute("errorMessage", "一次只能結帳一家店的商品喔！");
            return "redirect:/cart/cart";
        }

        // ================== 步驟4：【傳遞數據到結帳頁】 ==================
        // 將驗證通過的、被選中的商品列表存入 Session，以便結帳頁面 (GET) 可以獲取
        session.setAttribute("checkoutCartItems", selectedCartItems);

        // 使用 PRG (Post-Redirect-Get) 模式，重定向到顯示結帳頁面的 GET 請求
        return "redirect:/orders/checkout";
    }

    /**
     * 【核心修改】顯示結帳頁面 (GET)
     * 這個方法現在從 Session 中讀取經過驗證的商品數據，而不是直接查詢資料庫。
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // 從 Session 中獲取由 POST 方法存入的、已過濾的購物車項目
        List<CartVO> checkoutCartItems = (List<CartVO>) session.getAttribute("checkoutCartItems");
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");

        // 如果 Session 中沒有數據（例如用戶直接訪問URL），則重定向回購物車
        if (checkoutCartItems == null || checkoutCartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "無結帳商品，請從購物車重新選擇。");
            return "redirect:/cart/cart";
        }

        // ================== 計算訂單摘要 ==================
        // 直接基於傳過來的 checkoutCartItems 列表進行計算
        int totalAmount = checkoutCartItems.stream()
                .mapToInt(cart -> cart.getProduct().getProdPrice() * cart.getProdN())
                .sum();

        int shippingFee = totalAmount >= 500 ? 0 : 60;
        int finalTotal = totalAmount + shippingFee;

        model.addAttribute("cartListData", checkoutCartItems); // 將過濾後的列表傳給前端
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("finalTotal", finalTotal);
        model.addAttribute("orderData", new OrdersVO()); // 為表單綁定提供一個空物件

        return "/front/cart/checkout"; // 返回結帳頁面視圖
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
