package com.foodietime.orddet.model;

import com.foodietime.orders.model.OrdersVO;
import com.foodietime.product.model.ProductVO;
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
	private OrdersVO order;       // 訂單編號 (外鍵)
	//private Integer ordId;      // 訂單編號 (外鍵)

	@ManyToOne
	@JoinColumn(name = "PROD_ID",referencedColumnName = "PROD_ID")
	private ProductVO product;     // 商品編號 (外鍵)

	@Column(name="PROD_QTY")
	private Integer prodQty;    // 商品數量

	@Column(name="PROD_PRICE")
	private Integer prodPrice;  // 商品單價

	@Column(name="ORD_DESC")
	private String ordDesc;     // 訂單備註

}
