package com.foodietime.memcoupon.model;

import com.foodietime.coupon.model.CouponRepository;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberRepository;
import com.foodietime.member.model.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    @Transactional
    public String claimCoupon(MemberVO memberVO, Integer couponId) {
        // ==================== 1. 驗證會員和優惠券 (維持不變) ====================
        if (memberVO == null || memberVO.getMemId() == null) {
            return "錯誤：請先登入。";
        }

        CouponVO coupon = couponRepo.findById(couponId)
                .orElse(null);

        if (coupon == null) {
            return "錯誤：找不到指定的優惠券。";
        }

        // ==================== 2. 檢查優惠券是否已過期或未開始 (維持不變) ====================
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.before(coupon.getCouStartDate())) {
            return "優惠券尚未開放領取。";
        }
        if (now.after(coupon.getCouEndDate())) {
            return "優惠券已過期，無法領取。";
        }

        // ==================== 3. 【核心修正】使用 .isPresent() 檢查會員是否已領取 ====================
        // findByMemberAndCoupon 應返回 Optional<MemCouponVO>
        // 我們需要檢查這個 Optional 容器中「是否存在」值
        if (memCouponRepo.findByMemberAndCoupon(memberVO, coupon).isPresent()) {
            // 如果 .isPresent() 為 true，表示已領取過，返回錯誤訊息
            return "您已領取過此優惠券。";
        }

        // ==================== 4. 建立新的 MemCouponVO 實例 (維持不變) ====================
        MemCouponVO memCoupon = new MemCouponVO();
        memCoupon.setMember(memberVO);
        memCoupon.setCoupon(coupon);
        memCoupon.setUseStatus(0);

        // ==================== 5. 儲存到資料庫 (維持不變) ====================
        try {
            memCouponRepo.save(memCoupon);
            return "SUCCESS";
        } catch (Exception e) {
            System.err.println("領取優惠券失敗: " + e.getMessage());
            return "系統忙碌，請稍後再試。";
        }
    }

    /**
     * 根據會員ID，查詢該會員所有已領取的優惠券 ID。
     * 這個方法專門為前端渲染檢查狀態而設計，效能較高。
     *
     * @param memberId 會員的 ID
     * @return 一個包含所有已領取 Coupon ID 的 Set 集合。如果會員未領取任何優惠券，則返回空集合。
     */
    public Set<Integer> getClaimedCouponIdsByMemberId(Integer memberId) {
        // ==================== 1. 查找會員實體 ====================
        // 這裡直接使用 findById，因為如果會員不存在，本來就沒有優惠券
        MemberVO member = memberRepo.findById(memberId).orElse(null);
        if (member == null) {
            // 如果找不到會員，直接返回一個空的 Set
            return Collections.emptySet();
        }

        // ==================== 2. 查詢關聯的優惠券紀錄 ====================
        // 重複使用您已有的 findByMemberIdWithDetails 方法來獲取所有相關紀錄
        List<MemCouponVO> allCoupons = memCouponRepo.findByMemberIdWithDetails(member);

        // ==================== 3. 提取並返回 Coupon ID 集合 ====================
        // 使用 Java Stream API，從 List<MemCouponVO> 中提取出每一個 Coupon 的 ID，
        // 並收集成一個 Set<Integer>。使用 Set 可以自動去重，且查詢效率高。
        return allCoupons.stream()
                .map(memCoupon -> memCoupon.getCoupon().getCouId())
                .collect(Collectors.toSet());
    }
}
