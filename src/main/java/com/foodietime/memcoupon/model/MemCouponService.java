package com.foodietime.memcoupon.model;

import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberRepository;
import com.foodietime.member.model.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemCouponService {
    private final MemCouponRepository memCouponRepo;
    private final MemberRepository memberRepo;

    @Autowired
    public MemCouponService(MemCouponRepository memCouponRepository, MemberRepository memberRepo) {
        this.memCouponRepo = memCouponRepository;
        this.memberRepo = memberRepo;
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
}
