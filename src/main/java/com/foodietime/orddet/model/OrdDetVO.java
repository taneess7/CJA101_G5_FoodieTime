package com.foodietime.orddet.model;

import com.foodietime.orders.model.OrdersVO;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
@Entity
@Data
@Table(name = "order_details")
public class OrdDetVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ORD_DET_ID")
	private Integer ordDetId;   // 訂單明細編號 (主鍵)
	@ManyToOne
	@JoinColumn(name="ORD_ID",referencedColumnName = "ORD_ID")
	private OrdersVO ordId;       // 訂單編號 (外鍵)
	//private Integer ordId;      // 訂單編號 (外鍵)

	@Column(name="PROD_ID")
	private Integer prodId;     // 商品編號 (外鍵)
	@Column(name="PROD_QTY")
	private Integer prodQty;    // 商品數量
	@Column(name="PROD_PRICE")
	private Integer prodPrice;  // 商品單價
	@Column(name="ORD_DESC")
	private String ordDesc;     // 訂單備註


	public Integer getOrdDetId() {
		return ordDetId;
	}
	public void setOrdDetId(Integer ordDetId) {
		this.ordDetId = ordDetId;
	}
	public OrdersVO getOrdId() {
		return ordId;
	}
	public void setOrdId(OrdersVO ordId) {
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
