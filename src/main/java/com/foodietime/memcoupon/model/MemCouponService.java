package com.foodietime.memcoupon.model;

import com.foodietime.coupon.model.CouponRepository;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberRepository;
import com.foodietime.member.model.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemCouponService {
    private final MemCouponRepository memCouponRepo;
    private final MemberRepository memberRepo;
    private final CouponRepository couponRepo;

    @Autowired
    public MemCouponService(MemCouponRepository memCouponRepository, MemberRepository memberRepo, CouponRepository couponRepo) {
        this.memCouponRepo = memCouponRepository;
        this.memberRepo = memberRepo;
        this.couponRepo = couponRepo;
    }

    /**
     * @param memberId 會員ID，來自當前登入的使用者
     * @param statusFilter 狀態篩選器，來自前端的下拉選單
     * @return 根據條件過濾後的優惠券列表
     */
    public List<MemCouponVO> findCouponsByMemberAndStatus(Integer memberId, String statusFilter) {
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("訂單建立失敗：找不到會員ID " + memberId));

        List<MemCouponVO> allCoupons = memCouponRepo.findByMemberIdWithDetails(member);
        final Timestamp now = new Timestamp(System.currentTimeMillis());

        return allCoupons.stream()
                .filter(memCoupon -> {
                    CouponVO coupon = memCoupon.getCoupon();

                    // ------------------ ★★★ 重新定義各種狀態 ★★★ ------------------
                    // 1. 已使用：狀態碼為 1
                    boolean isUsed = memCoupon.getUseStatus() == 1;

                    // 2. 已過期：未使用，且當前時間已經超過了結束日期
                    boolean isExpired = !isUsed && now.after(coupon.getCouEndDate());

                    // 3. 可使用：未使用、未過期，且當前時間已經到達或超過了開始日期
                    boolean isAvailable = !isUsed && !isExpired && !now.before(coupon.getCouStartDate());


                    // ------------------ 根據 filter 參數回傳對應的結果 ------------------
                    switch (statusFilter) {
                        case "available":
                            return isAvailable;
                        case "used":
                            return isUsed;
                        case "expired":
                            return isExpired;
                        case "all":
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<MemCouponVO> findAvailableCouponsByMemberAndStore(Integer memberId, Integer storeId) {
        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("找不到會員ID " + memberId));

        // 先獲取會員所有的優惠券
        List<MemCouponVO> allCoupons = memCouponRepo.findByMemberIdWithDetails(member);
        final Timestamp now = new Timestamp(System.currentTimeMillis());

        // 使用 Stream 進行過濾
        return allCoupons.stream()
                .filter(memCoupon -> {
                    CouponVO coupon = memCoupon.getCoupon();

                    // 條件1: 必須是未使用的
                    boolean isUnused = memCoupon.getUseStatus() == 0;
                    // 條件2: 必須在有效期內
                    boolean isNotExpired = !now.after(coupon.getCouEndDate());
                    boolean isStarted = !now.before(coupon.getCouStartDate());
                    // 條件3: 必須屬於該店家 (或為全平台通用券，store為null)
                    boolean isStoreMatch = (coupon.getStore() == null || coupon.getStore().getStorId().equals(storeId));

                    return isUnused && isNotExpired && isStarted && isStoreMatch;
                })
                .collect(Collectors.toList());
    }

    /**
     * 會員領取優惠券的業務邏輯。
     *
     * @param memberVO 當前登入的會員實體
     * @param couponId 欲領取的優惠券 ID
     * @return 領取結果的訊息
     */
    @Transactional // 標註為事務性操作，確保資料一致性
    public String claimCoupon(MemberVO memberVO, Integer couponId) {
        // ==================== 1. 驗證會員和優惠券 ====================
        if (memberVO == null || memberVO.getMemId() == null) {
            return "錯誤：請先登入。";
        }

        CouponVO coupon = couponRepo.findById(couponId)
                .orElse(null); // 如果找不到優惠券則為 null

        if (coupon == null) {
            return "錯誤：找不到指定的優惠券。";
        }

        // ==================== 2. 檢查優惠券是否已過期或未開始 ====================
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.before(coupon.getCouStartDate())) {
            return "優惠券尚未開放領取。";
        }
        if (now.after(coupon.getCouEndDate())) {
            return "優惠券已過期，無法領取。";
        }

        // ==================== 3. 檢查會員是否已領取過此優惠券 ====================
        // 可以透過查詢 memCouponRepo 來判斷
        // 這裡需要 MemCouponRepository 提供一個查詢方法
        // 例如: MemCouponVO findByMemberAndCoupon(MemberVO member, CouponVO coupon);
        if (memCouponRepo.findByMemberAndCoupon(memberVO, coupon) != null) {
            return "您已領取過此優惠券。";
        }

        // ==================== 4. 建立新的 MemCouponVO 實例 ====================
        MemCouponVO memCoupon = new MemCouponVO();
        memCoupon.setMember(memberVO);  // 設定會員
        memCoupon.setCoupon(coupon);    // 設定優惠券
        memCoupon.setUseStatus(0);      // 預設為未使用 (0)

        // ==================== 5. 儲存到資料庫 ====================
        try {
            memCouponRepo.save(memCoupon);
            return "SUCCESS"; // 返回成功標誌
        } catch (Exception e) {
            // 記錄錯誤日誌
            System.err.println("領取優惠券失敗: " + e.getMessage());
            return "系統忙碌，請稍後再試。";
        }
    }


}
