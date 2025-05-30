package com.foodietime.orders.model;

import java.util.List;

public interface OrdersDAO_interface {
    //==============MySQL基本語法===========
    // 新增訂單
    public void insert(OrdersVO ordersVO);
    // 修改訂單
    public void update(OrdersVO ordersVO);
    // 刪除訂單
    public void delete(Integer ordId);

    //==============MySQL查詢語法===========
    // 依據主鍵查詢訂單
    public OrdersVO findByPrimaryKey(Integer ordId);
    // 查詢所有訂單
    public List<OrdersVO> getAll();
    // 查詢某會員的訂單
    public List<OrdersVO> findByMemberId(Integer memId);
    // 查詢某會員某訂單是否已存在
    public Boolean existsByMemberAndOrder(Integer memId, Integer ordId);
    //============== 業務邏輯 ===========
    /**
     * 依據訂單狀態查詢訂單
     * @param orderStatus 訂單狀態 (0:未接單, 1:接單, 2:完成, 3:取消)
     * @return 符合狀態的訂單清單
     */
    public List<OrdersVO> findByOrderStatus(Integer orderStatus);

    /**
     * 依據付款狀態查詢訂單
     * @param paymentStatus 付款狀態 (0:未付款, 1:已付款, 2:退款中)
     * @return 符合付款狀態的訂單清單
     */
    public List<OrdersVO> findByPaymentStatus(Integer paymentStatus);

    /**
     * 查詢某店家的訂單
     * @param storId 店家編號
     * @return 該店家的訂單清單
     */
    public List<OrdersVO> findByStoreId(Integer storId);

    /**
     * 依據日期範圍查詢訂單
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 日期範圍內的訂單清單
     */
    public List<OrdersVO> findByDateRange(java.sql.Timestamp startDate, java.sql.Timestamp endDate);

    /**
     * 查詢使用特定優惠券的訂單
     * @param couId 優惠券編號
     * @return 使用該優惠券的訂單清單
     */
    public List<OrdersVO> findByCouponId(Integer couId);

    /**
     * 查詢參與特定活動的訂單
     * @param actId 活動編號
     * @return 參與該活動的訂單清單
     */
    public List<OrdersVO> findByActivityId(Integer actId);

    /**
     * 更新訂單狀態
     * @param ordId 訂單編號
     * @param orderStatus 新的訂單狀態
     * @return 更新筆數
     */
    public Integer updateOrderStatus(Integer ordId, Integer orderStatus);

    /**
     * 更新付款狀態
     * @param ordId 訂單編號
     * @param paymentStatus 新的付款狀態
     * @return 更新筆數
     */
    public Integer updatePaymentStatus(Integer ordId, Integer paymentStatus);

    /**
     * 計算會員總消費金額
     * @param memId 會員編號
     * @return 總消費金額
     */
    public Integer getTotalSpentByMember(Integer memId);

    /**
     * 查詢熱門商品訂單統計
     * @param limit 限制筆數
     * @return 熱門商品統計清單
     */
    public List<Object[]> getPopularItemsStatistics(Integer limit);

}
