package com.foodietime.grouporders.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="grouporders")
public class GroupOrdersVO implements Serializable{ 
	
	@Id
	@Column(name="GB_OR_ID", nullable = false)
	private Integer gbOrId;
	
	@Column(name="GB_ID", nullable = false)
	private Integer gbId;
	
	@Column(name="STOR_ID", nullable = false)
	private Integer storId;
	
	@Column(name="GB_PROD_ID", nullable = false)
	private Integer gbProdId;
	
	@Column(name="JOIN_TIME", nullable = false)
	private Date joinTime;
	
	@Column(name="AMOUNT", nullable = false)
	private Integer amount;
	
	@Column(name="QUANTITY", nullable = false)
	private Integer quantity;
	
	@Column(name="PAY_METHOD", nullable = false)
	private byte payMethod;
	
	@Column(name="ORDER_STATUS", nullable = false)
	private byte orderStatus;
	
	@Column(name="PAYMENT_STATUS", nullable = false)
	private byte paymentStatus;
	
	@Column(name="SHIPPING_STATUS", nullable = false)
	private byte shippingStatus;
	
	@Column(name="PAR_NAME", nullable = false)
	private String parName;
	
	@Column(name="PAR_ADDRESS", nullable = false)
	private String parAddress;
	
	@Column(name="PAR_LONGITUDE", nullable = false)
	private BigDecimal parLongitude;
	
	@Column(name="PAR_LATITUDE", nullable = false)
	private BigDecimal parLatitude;
	
	@Column(name="PAR_PHONE", nullable = false)
	private String parPhone;
	
	@Column(name="DELIVERY_METHOD", nullable = false)
	private byte deliveryMethod;
	
	@Column(name="COMMENT", nullable = false)
	private String comment;
	
	@Column(name="RATING", nullable = false)
	private Integer rating;
	
	
	public GroupOrdersVO() {  //必需有一個不傳參數建構子
		
	}
	
	

	
}
