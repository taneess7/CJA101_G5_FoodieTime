package com.foodietime.orders.model;

import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberRepository;
import com.foodietime.member.model.MemberVO;
import com.foodietime.orddet.model.OrdDetRepository;
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.product.model.ProductVO; // 確保 ProductVO 可以被引用
import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;
import jakarta.transaction.Transactional; // 使用 Jakarta EE 的 @Transactional 確保交易原子性
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

    // 依據使用者要求，修改參數名稱
    private final OrdersRepository ordersRepo;
    private final CartService cartService;
    private final MemberRepository memberRepo;
    private final OrdDetRepository ordDetRepo;
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
                         OrdDetRepository ordDetRepo,
                         StoreRepository storeRepo) {
        this.ordersRepo = ordersRepo;
        this.cartService = cartService;
        this.memberRepo = memberRepo;
        this.ordDetRepo = ordDetRepo;
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
    public OrdersVO createOrderFromCart(Integer memberId, OrdersVO orderFormData, List<CartVO> checkoutItems) { // <<<<<< 【修改點 1】新增第三個參數

        // ======================================== 步驟1：獲取必要基礎資料 ========================================
        // 1.1 獲取會員資訊
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("訂單建立失敗：找不到會員ID " + memberId));

        // 1.2 【修改點 2】不再從資料庫撈取完整購物車，而是直接使用傳入的 checkoutItems
        // List<CartVO> cartItems = cartService.getByMemId(memberId); // <<<<<<<<<<< 刪除或註解掉此行

        // 1.3 驗證傳入的商品列表是否為空
        if (checkoutItems == null || checkoutItems.isEmpty()) {
            throw new IllegalStateException("訂單建立失敗：沒有需要結帳的商品。");
        }

        // 1.4 確定訂單所屬店家 (邏輯不變，但基於 checkoutItems)
        StoreVO orderStore = checkoutItems.get(0).getProduct().getStore();
        if (orderStore == null || orderStore.getStorId() == null) {
            throw new IllegalStateException("訂單建立失敗：商品未關聯到有效店家。");
        }

        // 1.5 店家驗證邏輯可以保留作為雙重保險，但理論上在控制器已驗證過
        boolean allFromSameStore = checkoutItems.stream()
                .allMatch(item -> item.getProduct().getStore().getStorId().equals(orderStore.getStorId()));
        if (!allFromSameStore) {
            throw new IllegalStateException("內部錯誤：嘗試用多個店家的商品建立訂單。");
        }

        // ======================================== 步驟2：建立 OrdersVO 主物件並設定屬性 ========================================
        OrdersVO order = new OrdersVO();
        order.setMember(member);
        order.setStore(orderStore);
        order.setOrdDate(new Timestamp(System.currentTimeMillis()));
        order.setPaymentStatus(0);
        order.setOrderStatus(0);
        order.setOrdAddr(orderFormData.getOrdAddr());
        order.setPayMethod(orderFormData.getPayMethod());
        order.setDeliver(orderFormData.getDeliver());
        order.setComment(orderFormData.getComment());

        // ======================================== 步驟3：處理訂單明細並計算金額 ========================================
        Integer ordSum = 0;
        Set<OrdDetVO> orderDetails = new HashSet<>();

        // 【修改點 3】迴圈遍歷 checkoutItems 而不是 cartItems
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

        Integer shippingFee = ordSum >= 500 ? 0 : 60;
        order.setPromoDiscount(0);
        order.setCouponDiscount(0);
        Integer actualPayment = ordSum + shippingFee - order.getPromoDiscount() - order.getCouponDiscount();
        order.setActualPayment(Math.max(0, actualPayment));
        order.setOrdDet(orderDetails);

        // ======================================== 步驟4：保存訂單至資料庫 ========================================
        OrdersVO savedOrder = ordersRepo.save(order);

        // ======================================== 步驟5：從購物車中移除已結帳的商品 ========================================
        // 【修改點 4】不再是清空整個購物車，而是只移除被結帳的商品
        // cartService.clearCart(memberId); // <<<<<<<<<<< 不再清空整個購物車
        List<Integer> checkedOutShopIds = checkoutItems.stream().map(CartVO::getShopId).collect(Collectors.toList());
        cartService.deleteCartItems(checkedOutShopIds); // 使用現有的 deleteCartItems 方法

        // ======================================== 步驟6：返回建立的訂單物件 ========================================
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
}