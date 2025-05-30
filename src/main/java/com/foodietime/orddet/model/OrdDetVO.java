package com.foodietime.orddet.model;

import java.io.Serializable;

public class OrdDetVO implements Serializable{
    private Integer ordDetId;   // 訂單明細編號 (主鍵)
    private Integer ordId;      // 訂單編號 (外鍵)
    private Integer prodId;     // 商品編號 (外鍵)
    private Integer prodQty;    // 商品數量
    private Integer prodPrice;  // 商品單價
    private String ordDesc;     // 訂單備註
    
    
	public Integer getOrdDetId() {
		return ordDetId;
	}
	public void setOrdDetId(Integer ordDetId) {
		this.ordDetId = ordDetId;
	}
	public Integer getOrdId() {
		return ordId;
	}
	public void setOrdId(Integer ordId) {
		this.ordId = ordId;
	}
	public Integer getProdId() {
		return prodId;
	}
	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}
	public Integer getProdQty() {
		return prodQty;
	}
	public void setProdQty(Integer prodQty) {
		this.prodQty = prodQty;
	}
	public Integer getProdPrice() {
		return prodPrice;
	}
	public void setProdPrice(Integer prodPrice) {
		this.prodPrice = prodPrice;
	}
	public String getOrdDesc() {
		return ordDesc;
	}
	public void setOrdDesc(String ordDesc) {
		this.ordDesc = ordDesc;
	}
    
    
}
