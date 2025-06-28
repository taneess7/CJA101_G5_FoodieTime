package com.foodietime.orders.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberRepository;
import com.foodietime.member.model.MemberVO;
import com.foodietime.memcoupon.model.MemCouponRepository;
import com.foodietime.memcoupon.model.MemCouponVO;
import com.foodietime.orddet.model.OrdDetRepository;
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.orders.dto.OrderNotificationDTO;
import com.foodietime.orders.websocket.OrderNotificationWebSocket;
import com.foodietime.product.model.ProductVO; // 確保 ProductVO 可以被引用
import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;
import jakarta.transaction.Transactional; // 使用 Jakarta EE 的 @Transactional 確保交易原子性
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrdersService {
    // ★★★ 新增 Logger 和 ObjectMapper ★★★
    private static final Logger log = LoggerFactory.getLogger(OrdersService.class);

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot 會自動配置此 Bean，用於序列化

    // 依據使用者要求，修改參數名稱
    private final OrdersRepository ordersRepo;
    private final CartService cartService;
    private final MemberRepository memberRepo;
    private final OrdDetRepository ordDetRepo;
    private final MemCouponRepository memCouponRepo;
    private final StoreRepository storeRepo;

    /**
     * 建構子注入依賴
     * @param ordersRepo   訂單資料庫操作介面
     * @param cartService  購物車服務
     * @param memberRepo   會員資料庫操作介面
     * @param ordDetRepo   訂單明細資料庫操作介面
     * @param storeRepo    店家資料庫操作介面
     */
    @Autowired
    public OrdersService(OrdersRepository ordersRepo,
                         CartService cartService,
                         MemberRepository memberRepo,
                         OrdDetRepository ordDetRepo, MemCouponRepository memCouponRepo,
                         StoreRepository storeRepo) {
        this.ordersRepo = ordersRepo;
        this.cartService = cartService;
        this.memberRepo = memberRepo;
        this.ordDetRepo = ordDetRepo;
        this.memCouponRepo = memCouponRepo;
        this.storeRepo = storeRepo;
    }

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
        try {
            // 7.1 獲取需要通知的商家 ID
            Integer storeId = savedOrder.getStore().getStorId();

            // 7.2 將訂單實體(VO)轉換為專門用於通知的資料傳輸物件(DTO)
            OrderNotificationDTO notificationDTO = new OrderNotificationDTO(
                    savedOrder.getOrdId(),
                    savedOrder.getMember().getMemName(),
                    savedOrder.getOrdDate(),
                    savedOrder.getActualPayment(),
                    "待處理" // 直接給予初始狀態文字
            );

            // 7.3 將 DTO 物件序列化為 JSON 字串
            String orderJson = objectMapper.writeValueAsString(notificationDTO);

            // 7.4 呼叫 WebSocket 端點的靜態方法，向指定商家推送新訂單訊息
            OrderNotificationWebSocket.sendNotificationToStore(storeId, orderJson);
            log.info("新訂單 #{} 已成功觸發 WebSocket 推播至商家 #{}", savedOrder.getOrdId(), storeId);

        } catch (Exception e) {
            // 重要：推播失敗不應影響主訂單流程，僅記錄錯誤日誌
            log.error("為訂單 #{} 觸發 WebSocket 推播時發生錯誤", savedOrder.getOrdId(), e);
        }
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
        if (newStatus < 0 || newStatus > 99) { // 假設狀態碼範圍為 0-99
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
        order.setOrderStatus(99); // 假設 99 代表已取消狀態

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
}