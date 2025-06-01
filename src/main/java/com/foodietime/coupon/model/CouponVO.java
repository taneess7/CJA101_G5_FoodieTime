package com.foodietime.coupon.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class CouponVO implements Serializable {

	// 寫上所有欄位
	private Integer couId; // 優惠券編號
	private Integer storId; // 店家編號
	private String couDes; // 優惠券說明
	private String couType; // 優惠券類型
	private Integer couMinOrd; // 最低消費金額限制
	private Timestamp couDate; // 使用期限

	// 取得or設置

	public CouponVO() {
		super();

	}

	public Integer getCouId() {
		return couId;
	}

	public void setCouId(Integer couId) {
		this.couId = couId;
	}

	public Integer getStorId() {
		return storId;
	}

	public void setStorId(Integer storId) {
		this.storId = storId;
	}

	public String getCouDes() {
		return couDes;
	}

	public void setCouDes(String couDes) {
		this.couDes = couDes;
	}

	public String getCouType() {
		return couType;
	}

	public void setCouType(String couType) {
		this.couType = couType;
	}

	public Integer getCouMinOrd() {
		return couMinOrd;
	}

	public void setCouMinOrd(Integer couMinOrd) {
		this.couMinOrd = couMinOrd;
	}

	public Timestamp getCouDate() {
		return couDate;
	}

	public void setCouDate(Timestamp couDate) {
		this.couDate = couDate;
	}

}
