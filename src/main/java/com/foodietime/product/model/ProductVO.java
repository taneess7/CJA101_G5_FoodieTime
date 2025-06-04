package com.foodietime.product.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "product")
public class ProductVO implements Serializable{
	
	@Id //主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) //自動增加
	@Column(name = "prod_id")
	private Integer prodId;
	
	@Column(name = "stor_id",nullable = false)
	private Integer storId;
	
	@Column(name ="prod_cate_id",nullable = false)
	private Integer prodCateId;
	
	@Column(name ="prod_name",nullable = false,length = 45)
	private String prodName;
	
	@Column(name = "prod_desc",nullable = false,length = 45)
	private String prodDesc;
	
	@Column(name = "prod_price",nullable = false)
	private Integer prodPrice;
	
	@Column(name = "prod_update_time",nullable = false)
	private Timestamp prodUpdateTime;
	
	@Column(name = "prod_status",nullable = false)
	private Boolean prodStatus;
	
	@Lob
	@Column(name = "prod_photo")
	private byte[] prodPhoto;
	
	@Column(name = "prod_last_update",nullable = false)
	private Timestamp prodLastUpdate;
	
	@Column( name = "prod_report_count")
	private Integer prodReportCount;
	
	
//	public Integer getProdId() {
//		return prodId;
//	}
//	public void setProdId(Integer prodId) {
//		this.prodId = prodId;
//	}
//	public Integer getStorId() {
//		return storId;
//	}
//	public void setStorId(Integer storId) {
//		this.storId = storId;
//	}
//	public Integer getProdCateId() {
//		return prodCateId;
//	}
//	public void setProdCateId(Integer prodCateId) {
//		this.prodCateId = prodCateId;
//	}
//	public String getProdName() {
//		return prodName;
//	}
//	public void setProdName(String prodName) {
//		this.prodName = prodName;
//	}
//	public String getProdDesc() {
//		return prodDesc;
//	}
//	public void setProdDesc(String prodDesc) {
//		this.prodDesc = prodDesc;
//	}
//	public Integer getProdPrice() {
//		return prodPrice;
//	}
//	public void setProdPrice(Integer prodPrice) {
//		this.prodPrice = prodPrice;
//	}
//	public Timestamp getProdUpdateTime() {
//		return prodUpdateTime;
//	}
//	public void setProdUpdateTime(Timestamp prodUpdateTime) {
//		this.prodUpdateTime = prodUpdateTime;
//	}
//	public Boolean getProdStatus() {
//		return prodStatus;
//	}
//	public void setProdStatus(Boolean prodStatus) {
//		this.prodStatus = prodStatus;
//	}
//	public byte[] getProdPhoto() {
//		return prodPhoto;
//	}
//	public void setProdPhoto(byte[] prodPhoto) {
//		this.prodPhoto = prodPhoto;
//	}
//	public Timestamp getProdLastUpdate() {
//		return prodLastUpdate;
//	}
//	public void setProdLastUpdate(Timestamp prodLastUpdate) {
//		this.prodLastUpdate = prodLastUpdate;
//	}
//	public Integer getProdReportCount() {
//		return prodReportCount;
//	}
//	public void setProdReportCount(Integer prodReportCount) {
//		this.prodReportCount = prodReportCount;
//	}
}
