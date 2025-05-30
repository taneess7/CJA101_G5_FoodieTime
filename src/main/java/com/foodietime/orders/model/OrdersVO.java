package com.foodietime.orders.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class OrdersVO implements Serializable{
    private Integer ordId;           // 訂單編號
    private Integer memId;           // 會員編號
    private Integer storId;          // 店家編號
    private Timestamp ordDate;       // 訂單成立時間
    private Integer ordSum;          // 訂單金額
    private Integer paymentStatus;   // 付款狀態
    private Integer payMethod;       // 付款方式
    private Integer deliver;         // 取餐方式
    private Integer orderStatus;     // 訂單狀態
    private Integer actId;           // 活動編號
    private Integer couId;           // 優惠券編號
    private String cancelReason;     // 取消原因
    private String comment;          // 評價
    private Integer rating;          // 星等
    private Integer promoDiscount;   // 活動優惠金額
    private Integer couponDiscount;  // 優惠券優惠金額
    private Integer actualPayment;   // 實付金額
    private String ordAddr;          // 外送地址
    
    
	public Integer getOrdId() {
		return ordId;
	}
	public void setOrdId(Integer ordId) {
		this.ordId = ordId;
	}
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
		this.memId = memId;
	}
	public Integer getStorId() {
		return storId;
	}
	public void setStorId(Integer storId) {
		this.storId = storId;
	}
	public Timestamp getOrdDate() {
		return ordDate;
	}
	public void setOrdDate(Timestamp ordDate) {
		this.ordDate = ordDate;
	}
	public Integer getOrdSum() {
		return ordSum;
	}
	public void setOrdSum(Integer ordSum) {
		this.ordSum = ordSum;
	}
	public Integer getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}
	public Integer getDeliver() {
		return deliver;
	}
	public void setDeliver(Integer deliver) {
		this.deliver = deliver;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Integer getActId() {
		return actId;
	}
	public void setActId(Integer actId) {
		this.actId = actId;
	}
	public Integer getCouId() {
		return couId;
	}
	public void setCouId(Integer couId) {
		this.couId = couId;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public Integer getPromoDiscount() {
		return promoDiscount;
	}
	public void setPromoDiscount(Integer promoDiscount) {
		this.promoDiscount = promoDiscount;
	}
	public Integer getCouponDiscount() {
		return couponDiscount;
	}
	public void setCouponDiscount(Integer couponDiscount) {
		this.couponDiscount = couponDiscount;
	}
	public Integer getActualPayment() {
		return actualPayment;
	}
	public void setActualPayment(Integer actualPayment) {
		this.actualPayment = actualPayment;
	}
	public String getOrdAddr() {
		return ordAddr;
	}
	public void setOrdAddr(String ordAddr) {
		this.ordAddr = ordAddr;
	}
    
}
