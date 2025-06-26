package com.foodietime.grouporders.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupOrdersRepository extends JpaRepository<GroupOrdersVO, Integer> {

	 /**
     * 撈一筆符合 orderId，且該訂單所屬場次中，你是 leader 的訂單
     */
	@Query("""
		      SELECT o 
		      FROM GroupOrdersVO o
		      JOIN o.groupBuyingCase c
		      JOIN c.participants p
		      WHERE o.gbOrId = :orderId
		        AND p.member.memId = :memId
		        AND p.leader = 0
		    """)
		    Optional<GroupOrdersVO> findLeaderOrder(
		      @Param("orderId") Integer orderId,
		      @Param("memId")    Integer memId
		    );
    
	 //根據多個 gbId 撈出所有對應訂單
    List<GroupOrdersVO> findByGroupBuyingCase_GbIdIn(List<Integer> gbIds);
    
    // 查詢某會員的所有團購訂單，通過參與的團購（GroupBuyingCasesVO）中的會員ID
    List<GroupOrdersVO> findByGroupBuyingCase_Member_MemId(Integer memId);

    // 根據訂單狀態查詢團購訂單
    List<GroupOrdersVO> findByOrderStatus(Byte orderStatus);

    // 根據付款狀態查詢團購訂單
    List<GroupOrdersVO> findByPaymentStatus(Byte paymentStatus);

    // 根據出貨狀態查詢團購訂單
    List<GroupOrdersVO> findByShippingStatus(Byte shippingStatus);

    // 查詢某一團購的所有團購訂單
    List<GroupOrdersVO> findByGroupBuyingCase_GbId(Integer gbId);

//    // 根據收件人姓名查詢團購訂單
//    List<GroupOrdersVO> findByParName(String parName);

    // 查詢某一商品的所有團購訂單
    List<GroupOrdersVO> findByGbprod_GbProdId(Integer gbProdId);

    // 查詢某一店家的所有團購訂單
    List<GroupOrdersVO> findByStore_StorId(Integer storId);

//    // 根據配送方式查詢團購訂單
//    List<GroupOrdersVO> findByDeliveryMethod(Byte deliveryMethod);

//    // 查詢已經付款的團購訂單
//    List<GroupOrdersVO> findByPaymentStatusAndOrderStatus(Byte paymentStatus, Byte orderStatus);
}
