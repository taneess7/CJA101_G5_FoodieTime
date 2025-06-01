package com.foodietime.participants.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ParticipantsVO implements Serializable {

	private Integer parId;
	private Integer memId;
	private Integer gbId;
	private String parPhone;
	private String parName;
	private String parAddress;
	private BigDecimal parLongitude;
	private BigDecimal parLatitude;
	private boolean isLeader;
	private Integer parPurchaseQuantity;
	private byte paymentStatus;
	public Integer getParId() {
		return parId;
	}
	public void setParId(Integer parId) {
		this.parId = parId;
	}
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
		this.memId = memId;
	}
	public Integer getGbId() {
		return gbId;
	}
	public void setGbId(Integer gbId) {
		this.gbId = gbId;
	}
	public String getParPhone() {
		return parPhone;
	}
	public void setParPhone(String parPhone) {
		this.parPhone = parPhone;
	}
	public String getParName() {
		return parName;
	}
	public void setParName(String parName) {
		this.parName = parName;
	}
	public String getParAddress() {
		return parAddress;
	}
	public void setParAddress(String parAddress) {
		this.parAddress = parAddress;
	}
	public BigDecimal getParLongitude() {
		return parLongitude;
	}
	public void setParLongitude(BigDecimal parLongitude) {
		this.parLongitude = parLongitude;
	}
	public BigDecimal getParLatitude() {
		return parLatitude;
	}
	public void setParLatitude(BigDecimal parLatitude) {
		this.parLatitude = parLatitude;
	}
	public boolean isLeader() {
		return isLeader;
	}
	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}
	public Integer getParPurchaseQuantity() {
		return parPurchaseQuantity;
	}
	public void setParPurchaseQuantity(Integer parPurchaseQuantity) {
		this.parPurchaseQuantity = parPurchaseQuantity;
	}
	public byte getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(byte paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	
	
	
	
	
}
