package com.foodietime.coupon.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupon")
public class CouponVO implements Serializable {

	// 寫上所有欄位
	// 1.優惠券編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COU_ID")
	private Integer couId; 
	
	// 2.店家編號
	@Column(name = "STOR_ID")
	private Integer storId; 
	
	// 3.優惠券類型
	@Column(name = "COU_TYPE")
	private String couType; 
	
	// 4.優惠券說明
	@Column(name = "COU_DES")
	private String couDes;
	

	// 5.最低消費金額限制
	@Column(name = "COU_MIN_ORD")
	private Integer couMinOrd; 
	
	// 6.使用期限
	@Column(name = "COU_DATE")
	private Timestamp couDate; 

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
