package com.foodietime.orders.model;

import com.foodietime.orddet.model.OrdDetVO;
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
	@OneToMany(mappedBy = "ordId", cascade = CascadeType.ALL)
	private Set<OrdDetVO> ordDet;

	@Column(name="MEM_ID")
	private Integer memId;           // 會員編號
	@Column(name="STOR_ID")
	private Integer storId;          // 店家編號
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
	@Column(name="ACT_ID")
	private Integer actId;           // 活動編號
	@Column(name="COU_ID")
	private Integer couId;           // 優惠券編號
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

}
