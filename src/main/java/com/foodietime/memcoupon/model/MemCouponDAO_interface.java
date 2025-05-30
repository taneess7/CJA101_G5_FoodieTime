package com.foodietime.memcoupon.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MemCouponDAO_interface {
    //==============MySQL基本語法===========
    // 新增會員優惠卷
    public void insert(MemCouponVO memCouponVO);
    // 修改會員優惠卷
    public void update(MemCouponVO memCouponVO);
    // 刪除會員優惠卷
    public void delete(Integer memCouId);

    //==============MySQL查詢語法===========
    // 依據主鍵查詢會員優惠卷
    public MemCouponVO findByPrimaryKey(Integer memCouId);

    // 查詢所有會員優惠卷
    public List<MemCouponVO> getAll();

    // 查詢某會員持有的會員優惠卷
    public List<MemCouponVO> findByMemId(Integer memId);

    // 查詢某會員某會員優惠卷是否已存在
    public boolean existsByMemIdAndCouId(Integer memId, Integer couId);
    //使用狀態相關查詢=============================================================
    // 查詢某會員未使用的優惠券 (USE_STATUS = 0)
    public List<MemCouponVO> findUnusedByMemId(Integer memId);

    // 查詢某會員已使用的優惠券 (USE_STATUS = 1)
    public List<MemCouponVO> findUsedByMemId(Integer memId);

    // 更新優惠券使用狀態（標記為已使用）
    public void updateUseStatus(Integer memCouId, Integer useStatus);

    //優惠券統計查詢=============================================================
    // 查詢某優惠券被多少會員持有
    public Integer countMembersByCouId(Integer couId);

    // 查詢某優惠券的使用情況統計
    public Map<String, Integer> getCouponUsageStats(Integer couId);

    // 查詢會員持有優惠券總數
    public Integer countTotalByMemId(Integer memId);

    // 查詢會員未使用優惠券數量
    public Integer countUnusedByMemId(Integer memId);

    //批次操作=============================================================
    // 批次新增會員優惠券（活動發放時使用）
    public void batchInsert(List<MemCouponVO> memCoupons);

    // 批次更新優惠券使用狀態
    public void batchUpdateUseStatus(List<Integer> memCouIds, Integer useStatus);

    //進階查詢=============================================================
    // 根據優惠券類型查詢會員持有的優惠券（需要JOIN COUPON表）
    public List<MemCouponVO> findByMemIdAndCouponType(Integer memId, String couponType);

    // 查詢即將到期的會員優惠券（需要JOIN COUPON表查詢到期日）
    public List<MemCouponVO> findExpiringCoupons(Integer memId, Date expiryDate);



}
