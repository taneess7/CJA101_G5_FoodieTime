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
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.orders.dto.LinePayRequestDTO;
import com.foodietime.orders.dto.LinePayResponseDTO;
import com.foodietime.orders.dto.NewOrderNotificationDTO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
     * 【修改後版本】
     * 根據會員選定的購物車內容、由 Controller 計算好的金額來建立新訂單。
     *
     * @param memberId         當前登入會員的ID
     * @param orderFormData    從結帳表單收集的訂單基本資訊
     * @param checkoutItems    使用者勾選的商品列表
     * @param selectedMemCouId 使用者選擇的會員優惠券ID
     * @param subtotalAmount   【新參數】由 Controller 計算好的商品小計
     * @param shippingFee      【新參數】由 Controller 計算好的運費
     * @return 成功建立的訂單物件
     * @throws IllegalStateException 當發生業務邏輯錯誤時拋出
     */
    @Transactional
    public OrdersVO createOrderFromCart(Integer memberId, OrdersVO orderFormData, List<CartVO> checkoutItems, Integer selectedMemCouId, Integer subtotalAmount, Integer shippingFee) {

        // ==================== 1. 獲取基礎資料 (不變) ====================
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("訂單建立失敗：找不到會員ID " + memberId));
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            throw new IllegalStateException("訂單建立失敗：沒有需要結帳的商品。");
        }
        StoreVO orderStore = checkoutItems.get(0).getProduct().getStore();

        // ==================== 2. 建立 OrdersVO 主物件並設定屬性 (不變) ====================
        OrdersVO order = new OrdersVO();
        order.setMember(member);
        order.setStore(orderStore);
        order.setOrdAddr(orderFormData.getOrdAddr());
        order.setPayMethod(orderFormData.getPayMethod());
        order.setDeliver(orderFormData.getDeliver());
        order.setComment(orderFormData.getComment());
        order.setOrdDate(new Timestamp(System.currentTimeMillis()));
        order.setPaymentStatus(1); // 假設非 LINE Pay 即為已付款或貨到付款，狀態直接設為1
        order.setOrderStatus(0);   // 處理中

        // ==================== 3. 處理訂單明細 (不變) ====================
        Set<OrdDetVO> orderDetails = new HashSet<>();
        for (CartVO cartItem : checkoutItems) {
            OrdDetVO ordDet = new OrdDetVO();
            ordDet.setOrders(order);
            ordDet.setProduct(cartItem.getProduct());
            ordDet.setProdQty(cartItem.getProdN());
            ordDet.setProdPrice(cartItem.getProduct().getProdPrice());
            orderDetails.add(ordDet);
        }
        order.setOrdDet(orderDetails);

        // 直接使用 Controller 傳入的金額，不再重新計算
        order.setOrdSum(subtotalAmount);
        order.setShippingFee(shippingFee);

        // ==================== 4. 處理優惠券折扣 (邏輯不變) ====================
        Integer couponDiscount = 0;
        if (selectedMemCouId != null && selectedMemCouId > 0) {
            MemCouponVO memCoupon = memCouponRepo.findById(selectedMemCouId)
                    .orElseThrow(() -> new IllegalStateException("優惠券無效或已不存在。"));

            // ... 安全性驗證 ...
            if (!memCoupon.getMember().getMemId().equals(memberId)) throw new IllegalStateException("權限錯誤：不能使用不屬於您的優惠券。");
            if (memCoupon.getUseStatus() != 0) throw new IllegalStateException("該優惠券已被使用或失效。");
            if (subtotalAmount < memCoupon.getCoupon().getCouMinOrd()) throw new IllegalStateException("訂單金額未達到優惠券最低消費門檻。");

            couponDiscount = memCoupon.getCoupon().getCouDiscount().intValue();
            order.setCoupon(memCoupon.getCoupon());
            memCoupon.setUseStatus(1);
            memCouponRepo.save(memCoupon);
        }
        order.setCouponDiscount(couponDiscount);
        order.setPromoDiscount(0); // 活動優惠暫設為0

        // 修正實付金額公式，必須加上運費
        Integer actualPayment = subtotalAmount - couponDiscount - order.getPromoDiscount() + shippingFee;
        order.setActualPayment(Math.max(0, actualPayment));

        // ==================== 5. 保存訂單並清理購物車 (不變) ====================
        OrdersVO savedOrder = ordersRepo.save(order);
        List<Integer> checkedOutShopIds = checkoutItems.stream().map(CartVO::getShopId).collect(Collectors.toList());
        cartService.deleteCartItems(checkedOutShopIds);
        triggerNotification(savedOrder);

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
     * 【修改後版本】
     * 建立一筆「待付款」的訂單，用於 LINE Pay 流程。
     *
     * @param memberId         當前登入會員的ID
     * @param orderFormData    從結帳表單收集的訂單基本資訊
     * @param checkoutItems    使用者勾選的商品列表
     * @param selectedMemCouId 使用者選擇的會員優惠券ID
     * @param subtotalAmount   【新參數】由 Controller 計算好的商品小計
     * @param shippingFee      【新參數】由 Controller 計算好的運費
     * @return 成功建立的待付款訂單物件
     */
    @Transactional
    public OrdersVO createPendingOrder(Integer memberId, OrdersVO orderFormData, List<CartVO> checkoutItems, Integer selectedMemCouId, Integer subtotalAmount, Integer shippingFee) {

        // ==================== 1. 獲取基礎資料 (不變) ====================
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("訂單建立失敗：找不到會員ID " + memberId));
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            throw new IllegalStateException("訂單建立失敗：沒有需要結帳的商品。");
        }
        StoreVO orderStore = checkoutItems.get(0).getProduct().getStore();

        // ==================== 2. 建立 OrdersVO 主物件 (不變) ====================
        OrdersVO order = new OrdersVO();
        order.setMember(member);
        order.setStore(orderStore);
        order.setOrdAddr(orderFormData.getOrdAddr());
        order.setPayMethod(orderFormData.getPayMethod());
        order.setDeliver(orderFormData.getDeliver());
        order.setComment(orderFormData.getComment());
        order.setOrdDate(new Timestamp(System.currentTimeMillis()));
        order.setPaymentStatus(0); // ★ 關鍵：待付款
        order.setOrderStatus(0);   // ★ 關鍵：處理中

        // ==================== 3. 處理訂單明細 (不變) ====================
        Set<OrdDetVO> orderDetails = new HashSet<>();
        for (CartVO cartItem : checkoutItems) {
            OrdDetVO ordDet = new OrdDetVO();
            ordDet.setOrders(order);
            ordDet.setProduct(cartItem.getProduct());
            ordDet.setProdQty(cartItem.getProdN());
            ordDet.setProdPrice(cartItem.getProduct().getProdPrice());
            orderDetails.add(ordDet);
        }
        order.setOrdDet(orderDetails);

        // 同樣直接使用 Controller 傳入的金額
        order.setOrdSum(subtotalAmount);
        order.setShippingFee(shippingFee);

        // ==================== 4. 處理優惠券 (不更新使用狀態) ====================
        Integer couponDiscount = 0;
        if (selectedMemCouId != null && selectedMemCouId > 0) {
            MemCouponVO memCoupon = memCouponRepo.findById(selectedMemCouId)
                    .orElseThrow(() -> new IllegalStateException("優惠券無效或已不存在。"));
            if (subtotalAmount >= memCoupon.getCoupon().getCouMinOrd()) {
                couponDiscount = memCoupon.getCoupon().getCouDiscount().intValue();
                order.setCoupon(memCoupon.getCoupon());
            }
        }
        order.setCouponDiscount(couponDiscount);
        order.setPromoDiscount(0);

        // 修正實付金額公式，必須加上運費
        Integer actualPayment = subtotalAmount - couponDiscount - order.getPromoDiscount() + shippingFee;
        order.setActualPayment(Math.max(0, actualPayment));

        // ==================== 5. 只儲存訂單 (不刪購物車，不更新優惠券) ====================
        return ordersRepo.save(order);
    }

    /**
     * 呼叫 LINE Pay Request API 以取得真實支付 URL。
     *
     * @param order 包含所有訂單資訊的「待付款」訂單物件
     * @return 可供使用者點擊前往付款的 LINE Pay URL
     * @throws RuntimeException 當 API 請求失敗或簽章產生錯誤時拋出
     */
    public String initiateLinePayPayment(OrdersVO order) {
        String nonce = UUID.randomUUID().toString();
        String requestBodyJson;

        // ==================== 1. 精確計算並建立商品列表 (★★ 核心修正點 ★★) ====================
        // 1a. 建立一個 List 來收集所有要傳給 LINE Pay 的項目
        List<LinePayRequestDTO.Product> linePayProducts = new ArrayList<>();

        // 1b. 將訂單中的所有實際商品加入列表
        // 註：在金融計算中，強烈建議使用 BigDecimal 以避免精度問題。
        //     此處為符合您現有 VO 的 Integer/long 型別，暫用 long 進行計算。
        for (OrdDetVO det : order.getOrdDet()) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name(det.getProduct().getProdName())
                    .quantity(det.getProdQty())
                    .price(det.getProdPrice().longValue())
                    .build());
        }

        // 1c. 如果有運費，將運費作為一個獨立的商品加入
        if (order.getShippingFee() != null && order.getShippingFee() > 0) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name("運費")
                    .quantity(1)
                    .price(order.getShippingFee().longValue())
                    .build());
        }

        // 1d. 如果有優惠券折扣，將折扣作為一個獨立的商品加入 (使用負值)
        // 注意：支付閘道 API 普遍接受負值價格來表示訂單層級的折扣。
        if (order.getCouponDiscount() != null && order.getCouponDiscount() > 0) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name("優惠券折扣")
                    .quantity(1)
                    .price(-order.getCouponDiscount().longValue()) // 價格設為負數
                    .build());
        }

        // 1e. 如果有其他活動折扣，也一併加入 (保留未來擴充性)
        if (order.getPromoDiscount() != null && order.getPromoDiscount() > 0) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name("活動折扣")
                    .quantity(1)
                    .price(-order.getPromoDiscount().longValue())
                    .build());
        }

        // ==================== 2. 計算校驗後的總金額 ====================
        // 重新計算一次列表內所有項目的總和，此總額應等於 `order.getActualPayment()`
        long totalAmount = linePayProducts.stream()
                .mapToLong(p -> p.getPrice() * p.getQuantity())
                .sum();

        // 安全校驗：如果計算出的總額與訂單的實付金額不符，拋出例外，防止送出錯誤請求
        if (totalAmount != order.getActualPayment()) {
            log.error("LINE Pay 金額計算校驗失敗！訂單ID: {}, 計算總額: {}, 預期實付: {}",
                    order.getOrdId(), totalAmount, order.getActualPayment());
            throw new IllegalStateException("LINE Pay 金額計算錯誤，請聯繫系統管理員。");
        }


        // ==================== 3. 組裝請求主體 (Request Body) ====================
        LinePayRequestDTO.Package linePayPackage = LinePayRequestDTO.Package.builder()
                .id("package-" + order.getOrdId())
                .amount(totalAmount) // ★ 使用我們剛計算出的總額
                .products(linePayProducts) // ★ 使用我們剛建立的、包含所有項目的列表
                .build();

        LinePayRequestDTO requestDTO = LinePayRequestDTO.builder()
                .amount(totalAmount) // ★ 總金額也使用我們計算出的總額
                .currency("TWD")
                .orderId(order.getOrdId().toString())
                .packages(List.of(linePayPackage))
                .redirectUrls(LinePayRequestDTO.RedirectUrls.builder()
                        .confirmUrl("http://localhost:8080/orders/linepay/confirm?orderId=" + order.getOrdId())
                        .cancelUrl("http://localhost:8080/cart/cart")
                        .build())
                .build();

        try {
            requestBodyJson = objectMapper.writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化 LINE Pay 請求失敗", e);
        }

        // ==================== 4. 產生簽章與發送請求 (此部分邏輯不變) ====================
        String signature = createHmacSignature(linePayChannelSecret, linePayApiUri, requestBodyJson, nonce);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-LINE-ChannelId", linePayChannelId);
        headers.set("X-LINE-Authorization-Nonce", nonce);
        headers.set("X-LINE-Authorization", signature);

        HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

        try {
            ResponseEntity<LinePayResponseDTO> response = restTemplate.postForEntity(linePayApiUrl, entity, LinePayResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                LinePayResponseDTO responseBody = response.getBody();
                if ("0000".equals(responseBody.getReturnCode())) {
                    return responseBody.getInfo().getPaymentUrl().getWeb();
                } else {
                    log.error("LINE Pay API 返回錯誤。訂單ID: {}, Code: {}, Message: {}",
                            order.getOrdId(), responseBody.getReturnCode(), responseBody.getReturnMessage());
                    throw new RuntimeException("LINE Pay 請求失敗: " + responseBody.getReturnMessage());
                }
            } else {
                throw new RuntimeException("LINE Pay 請求失敗，伺服器無回應或回應體為空。");
            }
        } catch (HttpClientErrorException e) {
            log.error("LINE Pay API 請求發生錯誤。訂單ID: {}, Status: {}, Body: {}",
                    order.getOrdId(), e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("LINE Pay API 請求錯誤: " + e.getResponseBodyAsString());
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

    //===============================================================================================
    //        後臺方法
    //===============================================================================================
    /**
     * 查詢所有訂單資料。
     * 此方法返回資料庫中所有的訂單，主要用於後台管理頁面的主列表展示。
     * 為維持查詢效能，此方法預設不會載入關聯的訂單明細(OrdDetVO)。
     *
     * @return 包含所有 OrdersVO 的 List 集合。
     */
    public List<OrdersVO> findAllOrders() {
        // ==================== 1. 呼叫 Repository 進行查詢 ====================
        return ordersRepo.findAll();
    }

    /**
     * 根據訂單ID查詢單筆訂單的完整資料。
     * 這個方法會一併載入該訂單下的所有訂單明細(OrdDetVO)，
     * 以便在前端的「檢視訂單」彈出視窗中完整顯示，並避免 LazyInitializationException。
     *
     * @param ordId 要查詢的訂單ID (Integer)。
     * @return 返回一個 Optional 物件，如果找到訂單則包含 OrdersVO，否則為空。
     */
    @Transactional(readOnly = true) // 查詢操作，設為 readOnly 可提升資料庫效能
    public Optional<OrdersVO> findByIdWithDetails(Integer ordId) {
        // ==================== 1. 根據 ID 查找訂單實體 ====================
        Optional<OrdersVO> orderOpt = ordersRepo.findById(ordId);

        // ==================== 2. 若訂單存在，則強制初始化關聯的明細集合 ====================
        orderOpt.ifPresent(order -> {
            // 透過存取集合的 size() 方法，可以觸發 Hibernate 載入 LAZY 關聯的訂單明細
            order.getOrdDet().size();
        });

        return orderOpt;
    }

    /**
     * 更新訂單的狀態。
     * 此方法為一個交易操作，專門用於後台管理系統修改訂單的狀態，
     * 例如：訂單狀態、付款狀態以及取消原因。
     *
     * @param ordId         要更新的訂單ID。
     * @param orderStatus   新的訂單狀態碼 (參考前端頁面定義)。
     * @param paymentStatus 新的付款狀態碼 (參考前端頁面定義)。
     * @param cancelReason  取消原因 (僅在訂單狀態為「已取消」時有意義)。
     * @return 更新成功後的 OrdersVO 物件。
     * @throws RuntimeException 如果根據 ordId 找不到對應的訂單，則拋出例外。
     */
    @Transactional
    public OrdersVO updateOrderStatus(Integer ordId, Integer orderStatus, Integer paymentStatus, String cancelReason) {
        // ==================== 1. 查找訂單實體 ====================
        // 使用 orElseThrow確保我們操作的是一個存在的訂單，若不存在則直接拋出例外中斷執行
        OrdersVO order = ordersRepo.findById(ordId)
                .orElseThrow(() -> new RuntimeException("更新失敗：找不到對應的訂單，ID: " + ordId));

        // ==================== 2. 更新訂單屬性 ====================
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);

        // 根據前端設計，訂單狀態 5 為 "已取消"，只有在此狀態下才設定取消原因
        if (orderStatus != null && orderStatus == 5) {
            order.setCancelReason(cancelReason);
        } else {
            // 如果訂單不是取消狀態，應將取消原因清空，以保持資料一致性
            order.setCancelReason(null);
        }

        // ==================== 3. 儲存變更至資料庫 ====================
        // 在 @Transactional 方法中，JPA 會自動將變更的實體同步回資料庫，
        // 但明確呼叫 save() 是個好習慣，能讓程式碼意圖更清晰。
        return ordersRepo.save(order);
    }

    /**
     * 儲存或更新一筆訂單資料。
     * 如果傳入的 orderVO 物件沒有 ID，則為新增；如果有 ID，則為更新。
     *
     * @param orderVO 要儲存或更新的 OrdersVO 物件。
     * @return 儲存或更新後的 OrdersVO 物件，其中可能包含由資料庫生成的 ID。
     */
    @Transactional
    public OrdersVO save(OrdersVO orderVO) {
        return ordersRepo.save(orderVO);
    }

    /**
     * 根據訂單ID刪除一筆訂單。
     * 注意：這會一併刪除與此訂單關聯的訂單明細 (因 OrdersVO 中設定了 orphanRemoval = true)。
     *
     * @param ordId 要刪除的訂單ID。
     */
    @Transactional
    public void deleteById(Integer ordId) {
        // 在刪除前可以先檢查是否存在，避免不存在的ID造成錯誤
        if (ordersRepo.existsById(ordId)) {
            ordersRepo.deleteById(ordId);
        } else {
            // 可以選擇拋出例外或記錄日誌
            throw new RuntimeException("刪除失敗：找不到對應的訂單，ID: " + ordId);
        }
    }



}