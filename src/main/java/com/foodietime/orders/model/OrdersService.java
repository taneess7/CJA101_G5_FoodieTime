package com.foodietime.orders.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodietime.cart.dto.CartItemDTO;
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
    private final String linePayApiUri = "/v3/payments/request";
    private final String linePayApiUrl = "https://sandbox-api-pay.line.me" + linePayApiUri;
    //=================================================================================================

    private final OrdersRepository ordersRepo;
    private final CartService cartService;
    private final MemberRepository memberRepo;
    private final MemCouponRepository memCouponRepo;
    private final StoreRepository storeRepo;
    private final NotificationService notificationService;
    private final MemService memService;

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

    /**
     * 【Redis 相容版】
     * 根據會員選定的購物車內容、由 Controller 計算好的金額來建立新訂單 (用於信用卡/貨到付款)。
     */
    @Transactional
    public OrdersVO createOrderFromCart(Integer memberId, OrdersVO orderFormData, List<CartItemDTO> checkoutItems, Integer selectedMemCouId, Integer subtotalAmount, Integer shippingFee) {

        // 1. 獲取基礎資料
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("訂單建立失敗：找不到會員ID " + memberId));
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            throw new IllegalStateException("訂單建立失敗：沒有需要結帳的商品。");
        }
        StoreVO orderStore = checkoutItems.get(0).getProduct().getStore();

        // 2. 建立 OrdersVO 主物件並設定屬性
        OrdersVO order = new OrdersVO();
        order.setMember(member);
        order.setStore(orderStore);
        order.setOrdAddr(orderFormData.getOrdAddr());
        order.setPayMethod(orderFormData.getPayMethod());
        order.setDeliver(orderFormData.getDeliver());
        order.setComment(orderFormData.getComment());
        order.setOrdDate(new Timestamp(System.currentTimeMillis()));
        order.setPaymentStatus(1);
        order.setOrderStatus(0);

        // 3. 處理訂單明細 (使用 CartItemDTO)
        Set<OrdDetVO> orderDetails = new HashSet<>();
        for (CartItemDTO item : checkoutItems) {
            OrdDetVO ordDet = new OrdDetVO();
            ordDet.setOrders(order);
            ordDet.setProduct(item.getProduct());
            ordDet.setProdQty(item.getQuantity());
            ordDet.setProdPrice(item.getProduct().getProdPrice());
            orderDetails.add(ordDet);
        }
        order.setOrdDet(orderDetails);

        order.setOrdSum(subtotalAmount);
        order.setShippingFee(shippingFee);

        // 4. 處理優惠券折扣
        Integer couponDiscount = 0;
        if (selectedMemCouId != null && selectedMemCouId > 0) {
            MemCouponVO memCoupon = memCouponRepo.findById(selectedMemCouId)
                    .orElseThrow(() -> new IllegalStateException("優惠券無效或已不存在。"));
            if (!memCoupon.getMember().getMemId().equals(memberId)) throw new IllegalStateException("權限錯誤：不能使用不屬於您的優惠券。");
            if (memCoupon.getUseStatus() != 0) throw new IllegalStateException("該優惠券已被使用或失效。");
            if (subtotalAmount < memCoupon.getCoupon().getCouMinOrd()) throw new IllegalStateException("訂單金額未達到優惠券最低消費門檻。");

            couponDiscount = memCoupon.getCoupon().getCouDiscount().intValue();
            order.setCoupon(memCoupon.getCoupon());
            memCoupon.setUseStatus(1);
            memCouponRepo.save(memCoupon);
        }
        order.setCouponDiscount(couponDiscount);
        order.setPromoDiscount(0);

        Integer actualPayment = subtotalAmount - couponDiscount - order.getPromoDiscount() + shippingFee;
        order.setActualPayment(Math.max(0, actualPayment));

        // 5. 保存訂單並清理 Redis 購物車
        OrdersVO savedOrder = ordersRepo.save(order);
        List<Integer> prodIdsToRemove = checkoutItems.stream()
                .map(item -> item.getProduct().getProdId())
                .collect(Collectors.toList());
        cartService.deleteCartItems(memberId, prodIdsToRemove);

        triggerNotification(savedOrder);
        return savedOrder;
    }

    /**
     * 【Redis 相容版】
     * 建立一筆「待付款」的訂單，用於 LINE Pay 流程。
     */
    @Transactional
    public OrdersVO createPendingOrder(Integer memberId, OrdersVO orderFormData, List<CartItemDTO> checkoutItems, Integer selectedMemCouId, Integer subtotalAmount, Integer shippingFee) {
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
        order.setPaymentStatus(0); // 待付款
        order.setOrderStatus(0);   // 處理中

        Set<OrdDetVO> orderDetails = new HashSet<>();
        for (CartItemDTO item : checkoutItems) {
            OrdDetVO ordDet = new OrdDetVO();
            ordDet.setOrders(order);
            ordDet.setProduct(item.getProduct());
            ordDet.setProdQty(item.getQuantity());
            ordDet.setProdPrice(item.getProduct().getProdPrice());
            orderDetails.add(ordDet);
        }
        order.setOrdDet(orderDetails);

        order.setOrdSum(subtotalAmount);
        order.setShippingFee(shippingFee);

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

        Integer actualPayment = subtotalAmount - couponDiscount - order.getPromoDiscount() + shippingFee;
        order.setActualPayment(Math.max(0, actualPayment));

        return ordersRepo.save(order);
    }

    /**
     * 【Redis 相容版】
     * 當收到 Webhook 或確認支付成功後，更新訂單狀態並清理購物車。
     */
    @Transactional
    public OrdersVO confirmPayment(Integer orderId) {
        OrdersVO order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("確認付款失敗：找不到訂單 #" + orderId));

        if (order.getPaymentStatus() == 1) {
            log.info("訂單 #{} 已處理過，跳過重複確認。", orderId);
            return order;
        }

        order.setPaymentStatus(1);
        order.setOrderStatus(1);

        if (order.getCoupon() != null) {
            memCouponRepo.findByMemberAndCoupon(order.getMember(), order.getCoupon())
                    .ifPresent(memCoupon -> {
                        memCoupon.setUseStatus(1);
                        memCouponRepo.save(memCoupon);
                    });
        }

        List<Integer> prodIdsToRemove = order.getOrdDet().stream()
                .map(ordDet -> ordDet.getProduct().getProdId())
                .collect(Collectors.toList());

        if (!prodIdsToRemove.isEmpty()) {
            cartService.deleteCartItems(order.getMember().getMemId(), prodIdsToRemove);
        }

        OrdersVO savedOrder = ordersRepo.save(order);
        triggerNotification(savedOrder);
        return savedOrder;
    }

    public Optional<OrdersVO> getOrderById(Integer ordId) {
        return ordersRepo.findById(ordId);
    }

    public List<OrdersVO> getOrdersByMemberId(Integer memId) {
        MemberVO member = memberRepo.findById(memId)
                .orElseThrow(() -> new IllegalStateException("查詢訂單失敗：找不到會員ID " + memId));
        return ordersRepo.findByMember(member);
    }

    public List<OrdersVO> getOrdersByStoreId(Integer storId) {
        StoreVO store = storeRepo.findById(storId)
                .orElseThrow(() -> new IllegalStateException("查詢訂單失敗：找不到店家ID " + storId));
        return ordersRepo.findByStore(store);
    }

    @Transactional
    public OrdersVO updateOrderStatus(Integer ordId, Integer newStatus) {
        OrdersVO order = ordersRepo.findById(ordId)
                .orElseThrow(() -> new IllegalStateException("更新訂單狀態失敗：找不到訂單ID " + ordId));
        if (newStatus < 0 || newStatus > 4) {
            throw new IllegalStateException("無效的訂單狀態碼：" + newStatus);
        }
        order.setOrderStatus(newStatus);
        return ordersRepo.save(order);
    }

    @Transactional
    public OrdersVO cancelOrder(Integer ordId, String cancelReason) {
        OrdersVO order = ordersRepo.findById(ordId)
                .orElseThrow(() -> new IllegalStateException("取消訂單失敗：找不到訂單ID " + ordId));
        if (order.getOrderStatus() != 0 && order.getOrderStatus() != 1) {
            throw new IllegalStateException("訂單狀態不允許取消，目前狀態為：" + order.getOrderStatus());
        }
        order.setCancelReason(cancelReason);
        order.setOrderStatus(4);
        return ordersRepo.save(order);
    }

    public boolean validateOrderBelongsToStore(Integer orderId, Integer storeId) {
        return ordersRepo.findById(orderId)
                .map(order -> order.getStore().getStorId().equals(storeId))
                .orElse(false);
    }

    @Transactional
    public OrdersVO createNewOrder(OrdersVO newOrderVO) {
        OrdersVO savedOrder = ordersRepo.save(newOrderVO);
        triggerNotification(savedOrder);
        return savedOrder;
    }

    private void triggerNotification(OrdersVO savedOrder) {
        try {
            String storeEmail = savedOrder.getStore().getStorEmail();
            if (storeEmail == null || storeEmail.isBlank()) {
                log.warn("無法為訂單 #{} 觸發推播，因為店家的 Email 為空。", savedOrder.getOrdId());
                return;
            }
            MemberVO storeMember = memService.getByMemEmail(storeEmail);
            if (storeMember != null) {
                Integer storeMemId = storeMember.getMemId();
                List<OrderDetailDTO> itemDTOs = savedOrder.getOrdDet().stream()
                        .map(detail -> new OrderDetailDTO(
                                detail.getProduct().getProdName(),
                                detail.getProdQty(),
                                detail.getProdPrice(),
                                detail.getOrdDesc()
                        ))
                        .collect(Collectors.toList());
                NewOrderNotificationDTO notificationDTO = new NewOrderNotificationDTO(
                        savedOrder.getOrdId(),
                        savedOrder.getOrdDate(),
                        savedOrder.getActualPayment(),
                        savedOrder.getOrderStatus(),
                        "待處理",
                        savedOrder.getComment(),
                        savedOrder.getMember().getMemName(),
                        savedOrder.getMember().getMemPhone(),
                        savedOrder.getOrdAddr(),
                        itemDTOs
                );
                String notificationJson = objectMapper.writeValueAsString(notificationDTO);
                notificationService.sendOrderNotificationAsync(storeMemId, notificationJson);
            } else {
                log.warn("無法為訂單 #{} 觸發推播，因為找不到 email 為 {} 的店家會員帳號。", savedOrder.getOrdId(), storeEmail);
            }
        } catch (Exception e) {
            log.error("為訂單 #{} 觸發 WebSocket 推播時發生未知錯誤", savedOrder.getOrdId(), e);
        }
    }

    public String initiateLinePayPayment(OrdersVO order) {
        String nonce = UUID.randomUUID().toString();
        String requestBodyJson;

        List<LinePayRequestDTO.Product> linePayProducts = new ArrayList<>();
        for (OrdDetVO det : order.getOrdDet()) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name(det.getProduct().getProdName())
                    .quantity(det.getProdQty())
                    .price(det.getProdPrice().longValue())
                    .build());
        }
        if (order.getShippingFee() != null && order.getShippingFee() > 0) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name("運費")
                    .quantity(1)
                    .price(order.getShippingFee().longValue())
                    .build());
        }
        if (order.getCouponDiscount() != null && order.getCouponDiscount() > 0) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name("優惠券折扣")
                    .quantity(1)
                    .price(-order.getCouponDiscount().longValue())
                    .build());
        }
        if (order.getPromoDiscount() != null && order.getPromoDiscount() > 0) {
            linePayProducts.add(LinePayRequestDTO.Product.builder()
                    .name("活動折扣")
                    .quantity(1)
                    .price(-order.getPromoDiscount().longValue())
                    .build());
        }

        long totalAmount = linePayProducts.stream()
                .mapToLong(p -> p.getPrice() * p.getQuantity())
                .sum();

        if (totalAmount != order.getActualPayment()) {
            throw new IllegalStateException("LINE Pay 金額計算錯誤，請聯繫系統管理員。");
        }

        LinePayRequestDTO.Package linePayPackage = LinePayRequestDTO.Package.builder()
                .id("package-" + order.getOrdId())
                .amount(totalAmount)
                .products(linePayProducts)
                .build();

        LinePayRequestDTO requestDTO = LinePayRequestDTO.builder()
                .amount(totalAmount)
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
                    throw new RuntimeException("LINE Pay 請求失敗: " + responseBody.getReturnMessage());
                }
            } else {
                throw new RuntimeException("LINE Pay 請求失敗，伺服器無回應或回應體為空。");
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("LINE Pay API 請求錯誤: " + e.getResponseBodyAsString());
        }
    }

    private String createHmacSignature(String channelSecret, String uri, String requestBody, String nonce) {
        try {
            String stringToSign = channelSecret + uri + requestBody + nonce;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(channelSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("產生 LINE Pay 簽章失敗", e);
        }
    }

    public List<OrdersVO> findAllOrders() {
        return ordersRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<OrdersVO> findByIdWithDetails(Integer ordId) {
        return ordersRepo.findById(ordId).map(order -> {
            order.getOrdDet().size();
            return order;
        });
    }

    @Transactional
    public OrdersVO updateOrderStatus(Integer ordId, Integer orderStatus, Integer paymentStatus, String cancelReason) {
        OrdersVO order = ordersRepo.findById(ordId)
                .orElseThrow(() -> new RuntimeException("更新失敗：找不到對應的訂單，ID: " + ordId));
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);
        if (orderStatus != null && orderStatus == 5) {
            order.setCancelReason(cancelReason);
        } else {
            order.setCancelReason(null);
        }
        return ordersRepo.save(order);
    }

    @Transactional
    public OrdersVO save(OrdersVO orderVO) {
        return ordersRepo.save(orderVO);
    }

    @Transactional
    public void deleteById(Integer ordId) {
        if (ordersRepo.existsById(ordId)) {
            ordersRepo.deleteById(ordId);
        } else {
            throw new RuntimeException("刪除失敗：找不到對應的訂單，ID: " + ordId);
        }
    }
}