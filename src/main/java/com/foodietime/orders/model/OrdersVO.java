package com.foodietime.orders.model;

import com.foodietime.act.model.ActVO;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.store.model.StoreVO;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

// ==================== 1. 移除 @Data，使用更精確的 Lombok 註解 ====================
@Getter
@Setter
@ToString(exclude = {"ordDet"})
@Entity
@Table(name = "orders")
public class OrdersVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ORD_ID")
	private Integer ordId;           // 訂單編號

	@ManyToOne
	@JoinColumn(name = "MEM_ID",referencedColumnName = "MEM_ID")
	private MemberVO member;           // 會員編號

	@ManyToOne
	@JoinColumn(name = "STOR_ID",referencedColumnName = "STOR_ID")
	private StoreVO store;          // 店家編號

	@Column(name="ORD_DATE")
	private Timestamp ordDate;       // 訂單成立時間

	@Column(name="ORD_SUM")
	private Integer ordSum;          // 訂單金額

	@Column(name="PAYMENT_STATUS")
	private Integer paymentStatus;   // 付款狀態

	@Column(name="PAY_METHOD")
	private Integer payMethod;       // 付款方式

	@Column(name="DELIVER")
	private Integer deliver;         // 取餐方式

	@Column(name="ORDER_STATUS")
	private Integer orderStatus;     // 訂單狀態

	@ManyToOne
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "ACT_ID",referencedColumnName = "ACT_ID")
	private ActVO act;           // 活動編號

	@ManyToOne
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "COU_ID",referencedColumnName = "COU_ID")
	private CouponVO coupon;           // 優惠券編號
//
//	@Column(name= "COU_ID")
//	private Integer coupon;

	@Column(name="CANCEL_REASON")
	private String cancelReason;     // 取消原因

	@Column(name="COMMENT")
	private String comment;          // 評價

	@Column(name="RATING")
	private Integer rating;          // 星等

	@Column(name="PROMO_DISCOUNT")
	private Integer promoDiscount;   // 活動優惠金額

	@Column(name="COUPON_DISCOUNT")
	private Integer couponDiscount;  // 優惠券優惠金額

	@Column(name="ACTUAL_PAYMENT")
	private Integer actualPayment;   // 實付金額

	@Column(name="ORD_ADDR")
	private String ordAddr;          // 外送地址

	@OneToMany(mappedBy = "orders",cascade = {CascadeType.PERSIST, CascadeType.MERGE},
			orphanRemoval = true)
	private Set<OrdDetVO> ordDet;

	// ==================== 2. 手動實作 equals 和 hashCode ====================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrdersVO ordersVO = (OrdersVO) o;
		// 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
		return ordId != null && Objects.equals(ordId, ordersVO.ordId);
	}

	@Override
	public int hashCode() {
		// 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
		// 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
		// 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
		return getClass().hashCode();
	}
}
