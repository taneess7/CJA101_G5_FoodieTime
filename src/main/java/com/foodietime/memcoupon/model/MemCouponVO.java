package com.foodietime.memcoupon.model;

import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

// ==================== 1. 移除 @Data，使用更精確的 Lombok 註解 ====================
@Getter
@Setter
@Entity
@Table(name = "member_coupon")
public class MemCouponVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="MEM_COU_ID")
	private Integer memCouId;   // 會員優惠券編號（主鍵）

	@ManyToOne
	@JoinColumn(name = "COU_ID",referencedColumnName = "COU_ID")
    private CouponVO coupon;      // 優惠券編號（外鍵，對應COUPON表）

	@ManyToOne
	@JoinColumn(name = "mem_id",referencedColumnName = "mem_id")
    private MemberVO member;      // 會員編號（外鍵，對應MEMBER表）

	@Column(name = "USE_STATUS")
    private Integer useStatus;  // 使用狀態（0:未使用, 1:已使用）

	// ==================== 2. 手動實作 equals 和 hashCode ====================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MemCouponVO memCouponVO = (MemCouponVO) o;
		// 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
		return memCouId != null && Objects.equals(memCouId, memCouponVO.memCouId);
	}

	@Override
	public int hashCode() {
		// 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
		// 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
		// 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
		return getClass().hashCode();
	}
}
