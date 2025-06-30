package com.foodietime.orders.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.memcoupon.model.MemCouponService;
import com.foodietime.memcoupon.model.MemCouponVO;
import com.foodietime.orders.dto.LinePayWebhookRequestDTO;
import com.foodietime.orders.model.OrdersService;
import com.foodietime.orders.model.OrdersVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final CartService cartService;
    private final OrdersService ordersService;
    private final MemCouponService memCouponService;
    private final ObjectMapper objectMapper;

    // 【新增】從 application.properties 注入 LINE Pay Channel Secret
    @Value("${line.pay.channel.secret}")
    private String linePayChannelSecret;

    @Autowired
    public OrdersController(CartService cartService, OrdersService ordersService, MemCouponService memCouponService, ObjectMapper objectMapper) {
        this.cartService = cartService;
        this.ordersService = ordersService;
        this.memCouponService = memCouponService;
        this.objectMapper = objectMapper;
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
                                 @RequestParam(name = "selectedCouponId", required = false) Integer selectedCouponId,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {

            MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
            if (memberVO == null) {
                return "redirect:/cart/login";
            }
            List<CartVO> checkoutItems = (List<CartVO>) session.getAttribute("checkoutItems");
            if (checkoutItems == null || checkoutItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "結帳已逾時或無商品，請重新操作。");
                return "redirect:/cart/cart";
            }

            try {
                // ==================== 根據付款方式分流 ====================
                // PayMethod: 0:信用卡, 1:貨到付款, 2:LINE Pay
                if (newOrderData.getPayMethod() == 2) { // 假設 2 是 LINE Pay
                    // --- LINE Pay 流程 ---
                    // 1. 建立一筆「待付款」的訂單
                    OrdersVO pendingOrder = ordersService.createPendingOrder(memberVO.getMemId(), newOrderData, checkoutItems, selectedCouponId);

                    // 2. (模擬) 呼叫 LINE Pay API 取得支付連結
                    String linePayUrl = ordersService.initiateLinePayPayment(pendingOrder);

                    // 3. 將訂單ID和支付連結存起來，重導向到付款處理頁面
                    redirectAttributes.addAttribute("orderId", pendingOrder.getOrdId());
                    session.setAttribute("linePayUrl", linePayUrl); // 實際應用中，URL可能很長，放session較佳
                    return "redirect:/orders/payment";

                } else {
                    // --- 信用卡或貨到付款流程 (原有邏輯) ---
                    OrdersVO createdOrder = ordersService.createOrderFromCart(memberVO.getMemId(), newOrderData, checkoutItems, selectedCouponId);
                    session.removeAttribute("checkoutItems");
                    redirectAttributes.addFlashAttribute("successMessage", "訂單已成功建立！");
                    redirectAttributes.addAttribute("orderId", createdOrder.getOrdId());
                    return "redirect:/orders/order-confirmation";
                }

            } catch (IllegalStateException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/orders/checkout";
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "建立訂單時發生未知錯誤。");
                return "redirect:/orders/checkout";
            }
        }
    /**
     * 【新增】顯示 LINE Pay 付款處理中頁面 (GET)。
     * 此頁面用於顯示 QR Code 和付款倒數計時器。
     *
     * @param orderId 參數用途：從 placeOrder 方法重定向時傳入的訂單 ID。
     * @param model   參數用途：用於將訂單ID和支付URL傳遞到 `payment-processing.html` 模板。
     * @param session 參數用途：用於獲取儲存的 linePayUrl。
     * @return 返回付款處理頁面的視圖路徑。
     */
    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam Integer orderId, Model model, HttpSession session) {
        String linePayUrl = (String) session.getAttribute("linePayUrl");
        // 安全性檢查：如果 session 中沒有支付 URL (例如使用者直接訪問此頁面)，則重定向回購物車。
        if (linePayUrl == null) {
            return "redirect:/cart/cart";
        }

        model.addAttribute("orderId", orderId);
        model.addAttribute("linePayUrl", linePayUrl); // 這個 URL 將被前端 JS 用於生成 QR Code。

        // 從 Session 中移除，因為它是一次性的。
        session.removeAttribute("linePayUrl");

        return "front/cart/payment-processing";
    }

    /**
     * 【新增】供前端輪詢的 API，用來非同步檢查訂單付款狀態。
     *
     * @param orderId 參數用途：要檢查的訂單 ID，從 URL 路徑中獲取。
     * @return 返回一個 JSON 物件，包含付款狀態 (e.g., {"paymentStatus": 1})。
     */
    @GetMapping("/api/order/{orderId}/status")
    @ResponseBody // 標示此方法直接返回數據(JSON)，而不是視圖名稱。
    public Map<String, Object> getOrderStatus(@PathVariable Integer orderId) {
        Map<String, Object> response = new LinkedHashMap<>();
        Optional<OrdersVO> orderOpt = ordersService.getOrderById(orderId);

        if (orderOpt.isPresent()) {
            OrdersVO order = orderOpt.get();
            // paymentStatus: 0 (待付款), 1 (已付款)
            response.put("paymentStatus", order.getPaymentStatus());
        } else {
            response.put("paymentStatus", -1); // -1 代表訂單不存在，前端可據此處理錯誤。
        }
        return response;
    }

    /**
     * 【完整實作】接收 LINE Pay Webhook 通知的端點 (POST)。
     * 此方法包含完整的安全性驗證與業務邏輯處理。
     *
     * @param requestBody    參數用途: 接收來自 LINE Pay 伺服器發送的 JSON 請求體 (原始字串)。
     * @param signature      參數用途: 接收來自 LINE Pay 請求標頭(Header)中的 `X-LINE-Signature` 值，用於驗證請求的合法性。
     * @return 返回 HTTP 狀態碼，告知 LINE Pay 處理結果。
     */
    @PostMapping("/linepay/webhook")
    @ResponseBody
    public ResponseEntity<String> handleLinePayWebhook(@RequestBody String requestBody,
                                                       @RequestHeader("X-LINE-Signature") String signature) {

        // ==================== 步驟 1：安全性 - 驗證簽名 ====================
        // 這是最關鍵的一步，確保請求是來自 LINE Pay 而不是偽造的。
        if (!isValidSignature(requestBody, signature)) {
            // 如果簽名驗證失敗，立即回傳 401 Unauthorized，並記錄此非法請求。
            System.err.println("Webhook 簽名驗證失敗！");
            return new ResponseEntity<>("Invalid Signature", HttpStatus.UNAUTHORIZED);
        }

        try {
            // ==================== 步驟 2：解析請求主體 (JSON to DTO) ====================
            // 使用 ObjectMapper 將 JSON 字串轉換為我們定義的 DTO 物件。
            LinePayWebhookRequestDTO webhookData = objectMapper.readValue(requestBody, LinePayWebhookRequestDTO.class);

            // ==================== 步驟 3：執行業務邏輯 ====================
            // 檢查 returnCode 是否為 "0000"，這代表使用者已成功付款。
            if ("0000".equals(webhookData.getReturnCode())) {
                // 從 DTO 中提取我們的訂單編號 (LINE Pay稱之為 transactionId)。
                // 注意：這裡的 transactionId 必須是我們在發起支付請求時傳給 LINE Pay 的訂單ID。
                Integer orderId = Integer.parseInt(webhookData.getInfo().getTransactionId());

                // 呼叫 Service 層來完成訂單的後續處理。
                // 這個 Service 方法必須是冪等的（下面會說明）。
                ordersService.confirmPayment(orderId);
            } else {
                // 如果付款失敗，可以根據業務需求記錄日誌或更新訂單為失敗狀態。
                System.out.println("收到 LINE Pay 付款失敗通知: " + webhookData.getReturnMessage());
            }

        } catch (JsonProcessingException e) {
            // 如果JSON解析失敗，表示收到的資料格式有問題。
            System.err.println("Webhook JSON 解析失敗: " + e.getMessage());
            return new ResponseEntity<>("Bad Request: Invalid JSON format", HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException e) {
            // 如果從 transactionId 解析訂單ID失敗。
            System.err.println("Webhook transactionId 格式錯誤: " + e.getMessage());
            return new ResponseEntity<>("Bad Request: Invalid transactionId format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 捕獲 Service 層可能拋出的其他業務異常。
            System.err.println("處理 Webhook 時發生內部錯誤: " + e.getMessage());
            // 即使內部處理失敗，仍建議回傳 200 OK，否則 LINE Pay 會不斷重試發送通知。
            // 錯誤應由我們的監控系統捕捉並處理。
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // ==================== 步驟 4：回傳成功響應 ====================
        // 無論業務邏輯成功與否，只要成功接收並驗證了請求，就應回傳 200 OK。
        // 這是在告知 LINE Pay：「我收到你的通知了，請不用再發了。」
        return ResponseEntity.ok("OK");
    }

    /**
     * 驗證 LINE Pay Webhook 簽名的輔助方法。
     * @param requestBody 原始的請求主體字串。
     * @param signature   從 X-LINE-Signature 標頭收到的簽名。
     * @return 如果簽名有效，返回 true；否則返回 false。
     */
    private boolean isValidSignature(String requestBody, String signature) {
        try {
            // 使用 HMAC-SHA256 算法
            Mac mac = Mac.getInstance("HmacSHA256");
            // 使用我們的 Channel Secret 初始化 Mac
            SecretKeySpec secretKeySpec = new SecretKeySpec(linePayChannelSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            // 計算請求主體的 HMAC
            byte[] hmacBytes = mac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));

            // 將計算出的 HMAC 進行 Base64 編碼
            String calculatedSignature = Base64.getEncoder().encodeToString(hmacBytes);

            // 比對計算出的簽名與收到的簽名是否一致
            return signature.equals(calculatedSignature);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
