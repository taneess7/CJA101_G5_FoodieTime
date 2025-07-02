package com.foodietime.orders.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberRepository;
import com.foodietime.member.model.MemberVO;
import com.foodietime.memcoupon.model.MemCouponRepository;
import com.foodietime.memcoupon.model.MemCouponVO;
import com.foodietime.orddet.dto.OrderDetailDTO;
import com.foodietime.orddet.model.OrdDetRepository;
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.orders.dto.LinePayRequestDTO;
import com.foodietime.orders.dto.LinePayResponseDTO;
import com.foodietime.orders.dto.NewOrderNotificationDTO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdersService {
    // =========================== (Web socket) 新增 Logger 和 ObjectMapper ================================================
    private static final Logger log = LoggerFactory.getLogger(OrdersService.class);

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot 會自動配置此 Bean，用於序列化

    //============================= line pay 設定 =============================================================
    @Value("${line.pay.channel.id}")
    private String linePayChannelId;

    @Value("${line.pay.channel.secret}")
    private String linePayChannelSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String linePayApiUri = "/v3/payments/request"; // ★ API 路徑
    private final String linePayApiUrl = "https://sandbox-api-pay.line.me" + linePayApiUri; // ★ API 完整網址
    //=================================================================================================

    // 依據使用者要求，修改參數名稱
    private final OrdersRepository ordersRepo;
    private final CartService cartService;
    private final MemberRepository memberRepo;
    private final MemCouponRepository memCouponRepo;
    private final StoreRepository storeRepo;
    private final NotificationService notificationService;
    private final MemService memService;
    /**
     * 建構子注入依賴
     * @param ordersRepo   訂單資料庫操作介面
     * @param cartService  購物車服務
     * @param memberRepo   會員資料庫操作介面
     * @param memCouponRepo 會員優惠卷資料操作介面
     * @param storeRepo    店家資料庫操作介面
     * @param notificationService 推播服務
     * @param memService   會員相關業務服務
     */
    @Autowired
    public OrdersService(OrdersRepository ordersRepo,
                         CartService cartService,
                         MemberRepository memberRepo, MemCouponRepository memCouponRepo,
                         StoreRepository storeRepo, NotificationService notificationService, MemService memService) {
        this.ordersRepo = ordersRepo;
        this.cartService = cartService;
        this.memberRepo = memberRepo;
        this.memCouponRepo = memCouponRepo;
        this.storeRepo = storeRepo;
        this.notificationService = notificationService;
        this.memService = memService;
    }
//=================================================================================================
    /**
     * 根據會員【選定】的購物車內容建立新的訂單。
     * 此方法不再查詢完整購物車，而是直接使用傳入的、已經過驗證的商品列表。
     *
     * @param memberId      參數用途：當前登入會員的唯一識別ID。
     *                      資料來源：由控制器從 Session 或安全上下文獲取。
     * @param orderFormData 參數用途：包含從結帳表單收集的訂單基本資訊。
     *                      資料來源：由前端表單提交。
     * @param checkoutItems 參數用途：【最重要】使用者在購物車勾選並通過驗證的商品列表。
     *                      資料來源：由控制器從 Session 中取出後傳入。
     * @return OrdersVO     成功建立的訂單物件。
     * @throws IllegalStateException 當購物車為空、找不到會員或店家時拋出。
     */
    @Transactional
    public OrdersVO createOrderFromCart(Integer memberId, OrdersVO orderFormData, List<CartVO> checkoutItems, Integer selectedMemCouId) {

        // ======================================== 步驟1：獲取基礎資料 (與您原版相似) ========================================
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("訂單建立失敗：找不到會員ID " + memberId));

        if (checkoutItems == null || checkoutItems.isEmpty()) {
            throw new IllegalStateException("訂單建立失敗：沒有需要結帳的商品。");
        }

        StoreVO orderStore = checkoutItems.get(0).getProduct().getStore();

        // ======================================== 步驟2：建立 OrdersVO 主物件並設定屬性 ========================================
        OrdersVO order = new OrdersVO();
        order.setMember(member);
        order.setStore(orderStore);
        // ... (設定 ordAddr, payMethod 等，與您原版相同)
        order.setOrdAddr(orderFormData.getOrdAddr());
        order.setPayMethod(orderFormData.getPayMethod());
        order.setDeliver(orderFormData.getDeliver());
        order.setComment(orderFormData.getComment());
        order.setOrdDate(new Timestamp(System.currentTimeMillis()));
        order.setPaymentStatus(0); // 待付款
        order.setOrderStatus(0);   // 處理中

        // ======================================== 步驟3：處理訂單明細並計算原始總金額 ========================================
        Integer ordSum = 0;
        Set<OrdDetVO> orderDetails = new HashSet<>();
        for (CartVO cartItem : checkoutItems) {
            OrdDetVO ordDet = new OrdDetVO();
            ordDet.setOrders(order);
            ordDet.setProduct(cartItem.getProduct());
            ordDet.setProdQty(cartItem.getProdN());
            ordDet.setProdPrice(cartItem.getProduct().getProdPrice());
            orderDetails.add(ordDet);
            ordSum += cartItem.getProdN() * cartItem.getProduct().getProdPrice();
        }
        order.setOrdSum(ordSum); // 設定訂單原始總額
        order.setOrdDet(orderDetails);

        // ======================================== 步驟4：【核心】處理優惠券折扣 ========================================
        Integer couponDiscount = 0;
        if (selectedMemCouId != null && selectedMemCouId > 0) {
            // 4.1 查找並驗證優惠券
            MemCouponVO memCoupon = memCouponRepo.findById(selectedMemCouId)
                    .orElseThrow(() -> new IllegalStateException("優惠券無效或已不存在。"));

            // ★★★ 安全性與業務邏輯驗證 ★★★
            if (!memCoupon.getMember().getMemId().equals(memberId)) throw new IllegalStateException("權限錯誤：不能使用不屬於您的優惠券。");
            if (memCoupon.getUseStatus() != 0) throw new IllegalStateException("該優惠券已被使用或失效。");
            if (new Timestamp(System.currentTimeMillis()).after(memCoupon.getCoupon().getCouEndDate())) throw new IllegalStateException("該優惠券已過期。");
            if (ordSum < memCoupon.getCoupon().getCouMinOrd()) throw new IllegalStateException("訂單金額未達到優惠券最低消費門檻。");

            // 4.2 驗證通過，應用折扣
            couponDiscount = memCoupon.getCoupon().getCouDiscount().intValue(); // 假設折扣是整數
            order.setCoupon(memCoupon.getCoupon()); // 在 OrdersVO 中關聯 CouponVO

            // ★★★ 關鍵：更新會員優惠券狀態 ★★★
            memCoupon.setUseStatus(1); // 1 代表已使用
            memCouponRepo.save(memCoupon);
        }
        order.setCouponDiscount(couponDiscount);
        order.setPromoDiscount(0); // 活動優惠暫設為0

        // ======================================== 步驟5：計算最終金額 ========================================
        Integer actualPayment = ordSum - couponDiscount - order.getPromoDiscount();
        order.setActualPayment(Math.max(0, actualPayment)); // 確保實付金額不為負

        // ======================================== 步驟6：保存訂單並清理購物車 ========================================
        OrdersVO savedOrder = ordersRepo.save(order);

        List<Integer> checkedOutShopIds = checkoutItems.stream().map(CartVO::getShopId).collect(Collectors.toList());
        cartService.deleteCartItems(checkedOutShopIds);

        // ★★★★★★★★★★★★★★★★★★★★★★ 新增區塊開始 ★★★★★★★★★★★★★★★★★★★★★★
        // ============================== 步驟7：觸發 WebSocket 即時推播通知 ==============================
        triggerNotification(savedOrder);
        // ★★★★★★★★★★★★★★★★★★★★★ 新增區塊結束 ★★★★★★★★★★★★★★★★★★★★★★

        return savedOrder;
    }

    // =======================================================================================================
    // 新增的訂單相關業務方法
    // =======================================================================================================

    /**
     * 查詢指定訂單ID的訂單詳情。
     * @param ordId 參數用途：要查詢的訂單的唯一識別ID。
     *              資料來源：由控制器從請求參數獲取。
     * @return Optional<OrdersVO> 參數用途：如果找到訂單則返回 OrdersVO 物件，否則返回空的 Optional。
     */
    public Optional<OrdersVO> getOrderById(Integer ordId) {
        // ================== 步驟1：透過 Repository 查詢訂單 ==================
        return ordersRepo.findById(ordId); // 使用 ordersRepo
    }

    /**
     * 查詢某會員的所有歷史訂單。
     * @param memId 參數用途：要查詢訂單歷史的會員ID。
     *              資料來源：由控制器從 Session 或安全上下文獲取。
     * @return List<OrdersVO> 參數用途：該會員的所有訂單列表，如果沒有則返回空列表。
     * @throws IllegalStateException 如果找不到指定會員。
     */
    public List<OrdersVO> getOrdersByMemberId(Integer memId) {
        // ================== 步驟1：獲取會員資訊 ==================
        MemberVO member = memberRepo.findById(memId) // 使用 memberRepo
                .orElseThrow(() -> new IllegalStateException("查詢訂單失敗：找不到會員ID " + memId));

        // ================== 步驟2：透過 Repository 查詢會員的所有訂單 ==================
        return ordersRepo.findByMember(member); // 使用 ordersRepo
    }

    /**
     * 查詢某店家的所有訂單。
     * @param storId 參數用途：要查詢訂單的店家ID。
     *               資料來源：由控制器從請求參數獲取。
     * @return List<OrdersVO> 參數用途：該店家的所有訂單列表，如果沒有則返回空列表。
     * @throws IllegalStateException 如果找不到指定店家。
     */
    public List<OrdersVO> getOrdersByStoreId(Integer storId) {
        // ================== 步驟1：獲取店家資訊 ==================
        StoreVO store = storeRepo.findById(storId) // 使用 storeRepo
                .orElseThrow(() -> new IllegalStateException("查詢訂單失敗：找不到店家ID " + storId));

        // ================== 步驟2：透過 Repository 查詢店家的所有訂單 ==================
        return ordersRepo.findByStore(store); // 使用 ordersRepo
    }

    /**
     * 更新指定訂單的狀態。
     * @param ordId     參數用途：要更新狀態的訂單ID。
     *                  資料來源：由控制器從請求參數獲取。
     * @param newStatus 參數用途：新的訂單狀態碼。
     *                  資料來源：由控制器從請求參數獲取。
     * @return OrdersVO 參數用途：更新後的訂單物件。
     * @throws IllegalStateException 如果找不到訂單，或新狀態無效。
     */
    @Transactional
    public OrdersVO updateOrderStatus(Integer ordId, Integer newStatus) {
        // ================== 步驟1：查詢訂單 ==================
        OrdersVO order = ordersRepo.findById(ordId) // 使用 ordersRepo
                .orElseThrow(() -> new IllegalStateException("更新訂單狀態失敗：找不到訂單ID " + ordId));

        // ================== 步驟2：驗證新狀態 (可選，根據業務需求添加更複雜的狀態機驗證) ==================
        if (newStatus < 0 || newStatus > 4) { // 假設狀態碼範圍為 0-99
            throw new IllegalStateException("無效的訂單狀態碼：" + newStatus);
        }
        // TODO: 如果有複雜的狀態轉換規則，例如：未付款不能直接跳到已完成，則在此處添加邏輯。

        // ================== 步驟3：更新訂單狀態並保存 ==================
        order.setOrderStatus(newStatus);
        return ordersRepo.save(order); // 使用 ordersRepo
    }

    /**
     * 取消指定訂單。
     * 此方法包含交易管理，可能涉及退款和庫存回滾的模擬邏輯。
     * @param ordId        參數用途：要取消的訂單ID。
     *                     資料來源：由控制器從請求參數獲取。
     * @param cancelReason 參數用途：取消訂單的原因。
     *                     資料來源：由控制器從請求參數獲取。
     * @return OrdersVO 參數用途：取消後的訂單物件。
     * @throws IllegalStateException 如果找不到訂單，或訂單狀態不允許取消。
     */
    @Transactional
    public OrdersVO cancelOrder(Integer ordId, String cancelReason) {
        // ================== 步驟1：查詢訂單 ==================
        OrdersVO order = ordersRepo.findById(ordId) // 使用 ordersRepo
                .orElseThrow(() -> new IllegalStateException("取消訂單失敗：找不到訂單ID " + ordId));

        // ================== 步驟2：驗證訂單狀態是否允許取消 ==================
        // 假設只有狀態為 0 (待處理) 或 1 (已確認) 的訂單可以取消
        if (order.getOrderStatus() != 0 && order.getOrderStatus() != 1) {
            throw new IllegalStateException("訂單狀態不允許取消，目前狀態為：" + order.getOrderStatus());
        }

        // ================== 步驟3：設定取消原因和更新訂單狀態 ==================
        order.setCancelReason(cancelReason);
        order.setOrderStatus(4); // 4 代表已取消狀態

        // ================== 步驟4：模擬退款和庫存回滾 (根據實際業務邏輯實現) ==================
        // TODO: 實際的退款邏輯可能需要與支付閘道整合。
        // TODO: 實際的庫存回滾邏輯需要根據訂單明細更新商品庫存。
        System.out.println("DEBUG: 訂單 " + ordId + " 已取消。執行模擬退款和庫存回滾。");

        // ================== 步驟5：保存更新後的訂單 ==================
        return ordersRepo.save(order); // 使用 ordersRepo
    }

    /**
     * 驗證訂單是否屬於指定商家。
     *
     * @param orderId 參數用途：要驗證的訂單ID。
     *                資料來源：控制器傳入的訂單ID。
     * @param storeId 參數用途：要驗證的商家ID。
     *                資料來源：當前登入商家的ID。
     * @return boolean 參數用途：如果訂單屬於該商家返回true，否則返回false。
     */
    public boolean validateOrderBelongsToStore(Integer orderId, Integer storeId) {
        // ================== 步驟1：查詢訂單 ==================
        Optional<OrdersVO> orderOpt = ordersRepo.findById(orderId);

        if (orderOpt.isEmpty()) {
            return false;
        }

        // ================== 步驟2：比對商家ID ==================
        OrdersVO order = orderOpt.get();
        return order.getStore().getStorId().equals(storeId);
    }

    @Transactional
    public OrdersVO createNewOrder(OrdersVO newOrderVO) {
        // ==================== 1. 儲存訂單到資料庫 ====================
        OrdersVO savedOrder = ordersRepo.save(newOrderVO);

        // ==================== 2. 觸發 WebSocket 推播通知 ====================
        triggerNotification(savedOrder);

        // ==================== 3. 返回儲存後的訂單 ====================
        return savedOrder;
    }

    /**
     * 輔助方法：觸發新訂單的 WebSocket 推播通知。
     * 此版本使用團隊已有的 getByMemEmail 方法，並執行 null 檢查。
     * @param savedOrder 剛剛儲存到資料庫的訂單實體
     */
    private void triggerNotification(OrdersVO savedOrder) {
        try {
            // ================ 步驟 1: 從訂單中獲取 StoreVO，再從 StoreVO 中獲取店家的 email。========================
            String storeEmail = savedOrder.getStore().getStorEmail();
            if (storeEmail == null || storeEmail.isBlank()) {
                log.warn("無法為訂單 #{} 觸發推播，因為店家的 Email 為空。", savedOrder.getOrdId());
                return;
            }

            // ================ 步驟 2: 使用團隊已有的 memberService.getByMemEmail 方法，透過 email 查找對應的 MemberVO。
            MemberVO storeMember = memService.getByMemEmail(storeEmail);

            // ================ 步驟 3: 檢查回傳的 MemberVO 是否為 null。如果不為 null，才進行推播。
            if (storeMember != null) {
                Integer storeMemId = storeMember.getMemId(); // ★ 成功獲取到 WebSocket 需要的 memId！

                // ================== 步驟 3.1: 將訂單明細 (Set<OrdDetVO>) 轉換為 List<OrderDetailDTO> ==============
                List<OrderDetailDTO> itemDTOs = savedOrder.getOrdDet().stream()
                        .map(detail -> new OrderDetailDTO(
                                detail.getProduct().getProdName(),
                                detail.getProdQty(),
                                detail.getProdPrice(),
                                detail.getOrdDesc() // 商品備註
                        ))
                        .collect(Collectors.toList());
                // ================== 步驟 3.2: 組裝完整的 NewOrderNotificationDTO ==================================
                NewOrderNotificationDTO notificationDTO = new NewOrderNotificationDTO(
                        savedOrder.getOrdId(),
                        savedOrder.getOrdDate(),
                        savedOrder.getActualPayment(),
                        savedOrder.getOrderStatus(), // 傳遞數字 0
                        "待處理", // 傳遞文字
                        savedOrder.getComment(), // 訂單備註
                        savedOrder.getMember().getMemName(),
                        savedOrder.getMember().getMemPhone(),
                        savedOrder.getOrdAddr(),
                        itemDTOs // 傳入組裝好的商品列表
                );
                String notificationJson = objectMapper.writeValueAsString(notificationDTO);
                notificationService.sendOrderNotificationAsync(storeMemId, notificationJson);
                log.info("新訂單 #{} 已成功觸發 WebSocket 推播至商家 (會員ID: {})", savedOrder.getOrdId(), storeMemId);

            } else {
                // 如果找不到對應的店家會員帳號，記錄一個警告。
                log.warn("無法為訂單 #{} 觸發推播，因為找不到 email 為 {} 的店家會員帳號。", savedOrder.getOrdId(), storeEmail);
            }
            // ==========================================================

        } catch (Exception e) {
            log.error("為訂單 #{} 觸發 WebSocket 推播時發生未知錯誤", savedOrder.getOrdId(), e);
        }
    }
    //================================================================================================================================
    //                      line pay 相關的方法
    //================================================================================================================================
    /**
     * 【核心修改】從 `createOrderFromCart` 複製並修改而來，用於建立一筆「待付款」的訂單
     * 主要差異是不會清理購物車、不更新優惠券狀態，並將付款狀態設為 0 (待付款)
     */
    @Transactional
    public OrdersVO createPendingOrder(Integer memberId, OrdersVO orderFormData, List<CartVO> checkoutItems, Integer selectedMemCouId) {
        // ... (步驟 1-3 與 createOrderFromCart 完全相同：獲取資料、建立主物件、處理訂單明細)
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("訂單建立失敗：找不到會員ID " + memberId));
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            throw new IllegalStateException("訂單建立失敗：沒有需要結帳的商品。");
        }
        StoreVO orderStore = checkoutItems.get(0).getProduct().getStore();

        OrdersVO order = new OrdersVO();
        order.setMember(member);
        order.setStore(orderStore);
        order.setOrdAddr(orderFormData.getOrdAddr());
        order.setPayMethod(orderFormData.getPayMethod());
        order.setDeliver(orderFormData.getDeliver());
        order.setComment(orderFormData.getComment());
        order.setOrdDate(new Timestamp(System.currentTimeMillis()));

        // ★★★ 關鍵差異點 ★★★
        order.setPaymentStatus(0); // 0: 待付款
        order.setOrderStatus(0);   // 0: 處理中 (但未付款)

        Integer ordSum = 0;
        Set<OrdDetVO> orderDetails = new HashSet<>();
        for (CartVO cartItem : checkoutItems) {
            OrdDetVO ordDet = new OrdDetVO();
            ordDet.setOrders(order);
            ordDet.setProduct(cartItem.getProduct());
            ordDet.setProdQty(cartItem.getProdN());
            ordDet.setProdPrice(cartItem.getProduct().getProdPrice());
            orderDetails.add(ordDet);
            ordSum += cartItem.getProdN() * cartItem.getProduct().getProdPrice();
        }
        order.setOrdSum(ordSum);
        order.setOrdDet(orderDetails);

        // ... (步驟 4-5 與 createOrderFromCart 相同：處理優惠券、計算最終金額)
        // 但此處「不」更新優惠券使用狀態
        Integer couponDiscount = 0;
        if (selectedMemCouId != null && selectedMemCouId > 0) {
            MemCouponVO memCoupon = memCouponRepo.findById(selectedMemCouId)
                    .orElseThrow(() -> new IllegalStateException("優惠券無效或已不存在。"));
            // ... (省略驗證邏輯)
            if (ordSum >= memCoupon.getCoupon().getCouMinOrd()) {
                couponDiscount = memCoupon.getCoupon().getCouDiscount().intValue();
                order.setCoupon(memCoupon.getCoupon());
            }
        }
        order.setCouponDiscount(couponDiscount);
        order.setPromoDiscount(0);

        Integer actualPayment = ordSum - couponDiscount - order.getPromoDiscount();
        order.setActualPayment(Math.max(0, actualPayment));

        // ★★★ 關鍵差異點：只儲存訂單，不清空購物車，不更新優惠券狀態 ★★★
        return ordersRepo.save(order);
    }

    /**
     * 呼叫 LINE Pay Request API 以取得真實支付 URL，並使用 HMAC 簽章認證。
     */
    public String initiateLinePayPayment(OrdersVO order) {
        String nonce = UUID.randomUUID().toString();
        String requestBodyJson;

        // ==================== 1. 組裝請求主體 (Request Body) ====================
        LinePayRequestDTO requestDTO = LinePayRequestDTO.builder()
                // ... (此處的 builder 邏輯與您附件中的版本完全相同，保持不變) ...
                .amount(order.getActualPayment().longValue())
                .currency("TWD")
                .orderId(order.getOrdId().toString())
                .packages(List.of(
                        LinePayRequestDTO.Package.builder()
                                .id("package-" + order.getOrdId())
                                .amount(order.getActualPayment().longValue())
                                .products(order.getOrdDet().stream().map(det ->
                                        LinePayRequestDTO.Product.builder()
                                                .name(det.getProduct().getProdName())
                                                .quantity(det.getProdQty())
                                                .price(det.getProdPrice().longValue())
                                                .build()
                                ).collect(Collectors.toList()))
                                .build()
                ))
                .redirectUrls(LinePayRequestDTO.RedirectUrls.builder()
                        // 修改點：將使用者導向我們新的確認端點
                        .confirmUrl("http://localhost:8080/orders/linepay/confirm?orderId=" + order.getOrdId())
                        .cancelUrl("http://localhost:8080/cart/cart") // 取消時導回購物車
                        .build())
                .build();

        try {
            // 將 DTO 物件序列化為 JSON 字串，用於產生簽章和發送請求
            requestBodyJson = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化 LINE Pay 請求失敗", e);
        }

        // ==================== 2. 產生簽章 ====================
        String signature = createHmacSignature(linePayChannelSecret, linePayApiUri, requestBodyJson, nonce);

        // ==================== 3. 組裝請求標頭 (Headers) - ★★★ 這是核心修改點 ★★★ ====================
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-LINE-ChannelId", linePayChannelId);
        headers.set("X-LINE-Authorization-Nonce", nonce);
        // 【重點】使用計算出的簽章，放入 X-LINE-Authorization 標頭
        headers.set("X-LINE-Authorization", signature);
        // 【移除】不再需要直接傳送 ChannelSecret
        // headers.set("X-LINE-ChannelSecret", linePayChannelSecret); // <-- 移除此行

        // ==================== 4. 發送請求 ====================
        HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

        try {
            ResponseEntity<LinePayResponseDTO> response = restTemplate.postForEntity(linePayApiUrl, entity, LinePayResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                LinePayResponseDTO responseBody = response.getBody();
                if ("0000".equals(responseBody.getReturnCode())) {
                    return responseBody.getInfo().getPaymentUrl().getWeb();
                } else {
                    throw new RuntimeException("LINE Pay 請求失敗: " + responseBody.getReturnMessage());
                }
            } else {
                throw new RuntimeException("LINE Pay 請求失敗，伺服器無回應。");
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("LINE Pay API 請求錯誤: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
    }
    /**
     * 【新增】當收到 Webhook 或確認支付成功後，更新訂單狀態
     * @param orderId 要確認的訂單 ID
     * @return 更新後的訂單
     */
    @Transactional
    public OrdersVO confirmPayment(Integer orderId) {
        // ==================== 步驟 1：查找訂單 (與您原版相同) ====================
        OrdersVO order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("確認付款失敗：找不到訂單 #" + orderId));

        // ==================== 步驟 2：冪等性檢查 (與您原版相同) ====================
        if (order.getPaymentStatus() == 1) {
            log.info("訂單 #{} 已處理過，跳過重複確認。", orderId);
            return order;
        }

        // ==================== 步驟 3：更新訂單狀態 (與您原版相同) ====================
        order.setPaymentStatus(1); // 1: 已付款
        order.setOrderStatus(1);   // 假設 1: 待出貨

        // ==================== 步驟 4：【核心修正】執行與 createOrderFromCart 同步的後續業務邏輯 ====================

        // a. 將使用的優惠券標記為「已使用」
        if (order.getCoupon() != null) {
            memCouponRepo.findByMemberAndCoupon(order.getMember(), order.getCoupon())
                    .ifPresent(memCoupon -> {
                        memCoupon.setUseStatus(1);
                        memCouponRepo.save(memCoupon);
                        log.info("訂單 #{} 使用的優惠券 (ID: {}) 已標記為已使用。", orderId, memCoupon.getMemCouId());
                    });
        }

        // b. 從會員的購物車中移除已經結帳的商品
        List<ProductVO> productsInOrder = order.getOrdDet().stream()
                .map(OrdDetVO::getProduct)
                .collect(Collectors.toList());

        if (!productsInOrder.isEmpty()) {
            cartService.deleteItemsByMemberAndProducts(order.getMember(), productsInOrder);
            log.info("已從購物車中移除訂單 #{} 的商品。", orderId);
        }

        // ==================== 步驟 5：儲存更新後的訂單 ====================
        OrdersVO savedOrder = ordersRepo.save(order);

        // ==================== 步驟 6：【核心修正】觸發 WebSocket 即時推播通知 ====================
        triggerNotification(savedOrder);

        return savedOrder;
    }

    /**
     * 【新增】產生 LINE Pay API 所需的 HMAC-SHA256 簽章。
     *
     * @param channelSecret 您的 Channel Secret
     * @param uri           您要請求的 API 路徑 (e.g., /v3/payments/request)
     * @param requestBody   您的請求主體 (JSON 字串)
     * @param nonce         一次性的隨機字串
     * @return Base64 編碼後的簽章字串
     */
    private String createHmacSignature(String channelSecret, String uri, String requestBody, String nonce) {
        try {
            // 1. 組合要進行加密的原始字串
            String stringToSign = channelSecret + uri + requestBody + nonce;

            // 2. 建立 HMAC-SHA256 加密器
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(channelSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            // 3. 執行加密
            byte[] hmacBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

            // 4. 將加密後的 byte[] 進行 Base64 編碼
            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch (Exception e) {
            // 在生產環境中，應使用日誌框架記錄此類嚴重錯誤
            throw new RuntimeException("產生 LINE Pay 簽章失敗", e);
        }
    }
}