package com.foodietime.product.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ProductVO implements Serializable{
	private Integer prodId;
	private Integer storId;
	private Integer prodCateId;
	private String prodName;
	private String prodDesc;
	private Integer prodPrice;
	private Timestamp prodUpdateTime;
	private Boolean prodStatus;
	private byte[] prodPhoto;
	private Timestamp prodLastUpdate;
	private Integer prodReportCount;
	public Integer getProdId() {
		return prodId;
	}
	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}
	public Integer getStorId() {
		return storId;
	}
	public void setStorId(Integer storId) {
		this.storId = storId;
	}
	public Integer getProdCateId() {
		return prodCateId;
	}
	public void setProdCateId(Integer prodCateId) {
		this.prodCateId = prodCateId;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getProdDesc() {
		return prodDesc;
	}
	public void setProdDesc(String prodDesc) {
		this.prodDesc = prodDesc;
	}
	public Integer getProdPrice() {
		return prodPrice;
	}
	public void setProdPrice(Integer prodPrice) {
		this.prodPrice = prodPrice;
	}
	public Timestamp getProdUpdateTime() {
		return prodUpdateTime;
	}
	public void setProdUpdateTime(Timestamp prodUpdateTime) {
		this.prodUpdateTime = prodUpdateTime;
	}
	public Boolean getProdStatus() {
		return prodStatus;
	}
	public void setProdStatus(Boolean prodStatus) {
		this.prodStatus = prodStatus;
	}
	public byte[] getProdPhoto() {
		return prodPhoto;
	}
	public void setProdPhoto(byte[] prodPhoto) {
		this.prodPhoto = prodPhoto;
	}
	public Timestamp getProdLastUpdate() {
		return prodLastUpdate;
	}
	public void setProdLastUpdate(Timestamp prodLastUpdate) {
		this.prodLastUpdate = prodLastUpdate;
	}
	public Integer getProdReportCount() {
		return prodReportCount;
	}
	public void setProdReportCount(Integer prodReportCount) {
		this.prodReportCount = prodReportCount;
	}

}
