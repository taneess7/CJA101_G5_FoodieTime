package com.foodietime.orders.model;

import com.foodietime.act.model.ActVO;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.store.model.StoreVO;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Data
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
	@JoinColumn(name = "ACT_ID",referencedColumnName = "ACT_ID")
	private ActVO act;           // 活動編號

	@ManyToOne
	@JoinColumn(name = "COU_ID",referencedColumnName = "COU_ID")
	private CouponVO coupon;           // 優惠券編號

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

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private Set<OrdDetVO> ordDet;
}
