package com.foodietime.memcoupon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "member_coupon")
public class MemCouponVO implements Serializable{
	private Integer memCouId;   // 會員優惠券編號（主鍵）
    private Integer couId;      // 優惠券編號（外鍵，對應COUPON表）
    private Integer memId;      // 會員編號（外鍵，對應MEMBER表）
    private Integer useStatus;  // 使用狀態（0:未使用, 1:已使用）
    
    
    public Integer getMemCouId() {
		return memCouId;
	}
	public void setMemCouId(Integer memCouId) {
		this.memCouId = memCouId;
	}
	public Integer getCouId() {
		return couId;
	}
	public void setCouId(Integer couId) {
		this.couId = couId;
	}
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
		this.memId = memId;
	}
	public Integer getUseStatus() {
		return useStatus;
	}
	public void setUseStatus(Integer useStatus) {
		this.useStatus = useStatus;
	}
	
}
