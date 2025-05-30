package com.foodietime.orddet.model;

import java.util.List;

public interface OrdDetDAO_interface {
    //==============MySQL基本語法===========
    // 新增訂單明細
    public void insert(OrdDetVO ordDetVO);
    // 修改訂單明細
    public void update(OrdDetVO ordDetVO);
    // 刪除訂單明細
    public void delete(Integer ordDetId);

    //==============MySQL查詢語法===========
    // 依據主鍵查詢訂單明細
    public OrdDetVO findByPrimaryKey(Integer ordDetId);
    // 查詢所有訂單明細
    public List<OrdDetVO> getAll();
    // 查詢某會員的訂單明細
    public List<OrdDetVO> findByMemberId(Integer memId);
    // 查詢某會員某訂單明細是否已存在
    public Boolean existsByMemberAndOrderDetail(Integer memId, Integer ordDetId);

    //==============業務邏輯相關方法===========

    /**
     * 依據訂單編號查詢訂單明細
     * @param ordId 訂單編號
     * @return 該訂單的所有明細清單
     */
    public List<OrdDetVO> findByOrderId(Integer ordId);

    /**
     * 依據商品編號查詢訂單明細（需要JOIN PRODUCT表格）
     * @param prodId 商品編號
     * @return 包含該商品的訂單明細清單
     */
    public List<OrdDetVO> findByProductId(Integer prodId);

    /**
     * 查詢某訂單的明細總數量
     * @param ordId 訂單編號
     * @return 該訂單的商品總數量
     */
    public Integer getTotalQuantityByOrderId(Integer ordId);

    /**
     * 查詢某訂單的明細總金額
     * @param ordId 訂單編號
     * @return 該訂單的明細總金額
     */
    public Integer getTotalAmountByOrderId(Integer ordId);

    /**
     * 查詢某商品的銷售統計（需要JOIN PRODUCT表格）
     * @param prodId 商品編號
     * @return 該商品的總銷售數量
     */
    public Integer getTotalSoldQuantityByProductId(Integer prodId);

    /**
     * 查詢熱門商品排行（需要JOIN PRODUCT表格）
     * @param limit 限制筆數
     * @return 熱門商品統計清單 [商品編號, 總銷售數量, 訂單數量]
     */
    public List<Object[]> getPopularProductsRanking(Integer limit);

    /**
     * 查詢某店家的商品銷售統計（需要JOIN ORDERS和PRODUCT表格）
     * @param storId 店家編號
     * @return 該店家的商品銷售統計
     */
    public List<Object[]> getProductSalesByStore(Integer storId);

    /**
     * 依據價格範圍查詢訂單明細
     * @param minPrice 最低價格
     * @param maxPrice 最高價格
     * @return 符合價格範圍的訂單明細清單
     */
    public List<OrdDetVO> findByPriceRange(Integer minPrice, Integer maxPrice);

    /**
     * 批次新增訂單明細
     * @param ordDetList 訂單明細清單
     */
    public void batchInsert(List<OrdDetVO> ordDetList);

    /**
     * 批次刪除某訂單的所有明細
     * @param ordId 訂單編號
     */
    public void deleteByOrderId(Integer ordId);

    /**
     * 更新訂單明細數量
     * @param ordDetId 訂單明細編號
     * @param quantity 新數量
     * @return 更新筆數
     */
    public Integer updateQuantity(Integer ordDetId, Integer quantity);

    /**
     * 更新訂單明細價格
     * @param ordDetId 訂單明細編號
     * @param price 新價格
     * @return 更新筆數
     */
    public Integer updatePrice(Integer ordDetId, Integer price);

    /**
     * 查詢某時間範圍內的訂單明細（需要JOIN ORDERS表格）
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @return 時間範圍內的訂單明細清單
     */
    public List<OrdDetVO> findByDateRange(java.sql.Timestamp startDate, java.sql.Timestamp endDate);

    /**
     * 查詢包含特定備註的訂單明細
     * @param keyword 關鍵字
     * @return 包含關鍵字的訂單明細清單
     */
    public List<OrdDetVO> findByDescriptionKeyword(String keyword);

}
