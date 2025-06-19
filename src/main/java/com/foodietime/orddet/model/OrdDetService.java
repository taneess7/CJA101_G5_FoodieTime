package com.foodietime.orddet.model;

import com.foodietime.orders.model.OrdersRepository;
import com.foodietime.orders.model.OrdersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdDetService {

    private final OrdDetRepository ordDetRepo;
    private final OrdersRepository ordersRepo; // 用於查詢 OrdersVO，以便根據 OrdersVO 查詢其明細

    /**
     * 建構子注入依賴。
     * @param ordDetRepo 訂單明細資料庫操作介面。
     * @param ordersRepo 訂單資料庫操作介面 (用於查詢相關訂單)。
     */
    @Autowired
    public OrdDetService(OrdDetRepository ordDetRepo, OrdersRepository ordersRepo) {
        this.ordDetRepo = ordDetRepo;
        this.ordersRepo = ordersRepo;
    }

    // =======================================================================================================
    // 訂單明細查詢方法
    // =======================================================================================================

    /**
     * 查詢所有訂單明細。
     * @return List<OrdDetVO> 參數用途：所有訂單明細的列表。
     */
    public List<OrdDetVO> getAllOrderDetails() {
        // ================== 步驟1：透過 Repository 查詢所有訂單明細 ==================
        return ordDetRepo.findAll();
    }

    /**
     * 根據訂單明細ID查詢單一訂單明細。
     * @param ordDetId 參數用途：要查詢的訂單明細的唯一識別ID。
     *                 資料來源：由控制器從請求參數獲取。
     * @return Optional<OrdDetVO> 參數用途：如果找到訂單明細則返回 OrdDetVO 物件，否則返回空的 Optional。
     */
    public Optional<OrdDetVO> getOneOrderDetail(Integer ordDetId) {
        // ================== 步驟1：透過 Repository 查詢單一訂單明細 ==================
        return ordDetRepo.findById(ordDetId);
    }

    /**
     * 根據訂單ID查詢該訂單下的所有訂單明細。
     * @param ordId 參數用途：所屬訂單的ID。
     *              資料來源：由控制器從請求參數獲取。
     * @return List<OrdDetVO> 參數用途：指定訂單下的所有訂單明細列表。
     * @throws IllegalStateException 如果找不到指定訂單。
     */
    public List<OrdDetVO> getOrderDetailsByOrderId(Integer ordId) {
        // ================== 步驟1：查詢所屬訂單物件 ==================
        OrdersVO order = ordersRepo.findById(ordId)
                .orElseThrow(() -> new IllegalStateException("查詢訂單明細失敗：找不到訂單ID " + ordId));

        // ================== 步驟2：透過 Repository 查詢該訂單的所有明細 ==================
        return ordDetRepo.findByOrders(order);
    }

    // 備註：通常訂單明細的建立、修改、刪除會透過 OrdersService 的事務來管理，
    // 因為它們是訂單的組成部分。例如，當 OrdersService 建立一個訂單時，
    // 會一併建立其下的所有 OrdDetVO。如果需要獨立修改某個明細，
    // 則需要仔細考慮業務邏輯對主訂單的影響。
    // 如果確實存在獨立修改單一訂單明細的需求，則可以在此處添加相應的方法。
    // 例如：
    // @Transactional
    // public OrdDetVO updateOrderDetail(Integer ordDetId, Integer newProdQty, String newOrdDesc) {
    //     OrdDetVO ordDet = ordDetRepo.findById(ordDetId)
    //             .orElseThrow(() -> new IllegalStateException("訂單明細不存在: " + ordDetId));
    //     ordDet.setProdQty(newProdQty);
    //     ordDet.setOrdDesc(newOrdDesc);
    //     // 注意：更新明細數量或價格後，可能需要更新其所屬 OrdersVO 的 ordSum 和 actualPayment
    //     // 這通常意味著此類操作最好仍在 OrdersService 中進行，以確保資料一致性。
    //     return ordDetRepo.save(ordDet);
    // }
}