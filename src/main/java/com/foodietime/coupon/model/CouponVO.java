package com.foodietime.coupon.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.foodietime.memcoupon.model.MemCouponVO;
import com.foodietime.orders.model.OrdersVO;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
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
	@ManyToOne
    @JoinColumn(name = "STOR_ID", referencedColumnName = "STOR_ID") // 外鍵名稱
	private StoreVO store; 
	
	// 3.優惠券類型
	@NotEmpty(message="優惠券類型: 請勿空白")
	@Column(name = "COU_TYPE", length = 255)
	private String couType; 
	
	// 4.優惠券說明
	@NotEmpty(message="優惠券說明: 請勿空白")
	@Column(name = "COU_DES", length = 255)
	private String couDes;
	

	// 5.最低消費金額限制
	@Column(name = "COU_MIN_ORD")
	private Integer couMinOrd; 
	
	// 6.使用期限
	@NotEmpty(message="使用期限: 請勿空白")
	@Column(name = "COU_DATE")
	private Timestamp couDate; 

	// 取得or設置

	public CouponVO() {
		super();

	}
	
	//OneToMany
	@OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<MemCouponVO> memCoupon;
	
	@OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<OrdersVO> orders;


}
