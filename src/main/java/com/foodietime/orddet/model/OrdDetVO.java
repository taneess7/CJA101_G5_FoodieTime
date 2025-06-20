package com.foodietime.orddet.model;

import com.foodietime.orders.model.OrdersVO;
import com.foodietime.product.model.ProductVO;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

// ==================== 1. 移除 @Data，使用更精確的 Lombok 註解 ====================
@Getter
@Setter
@Entity
@Table(name = "order_details")
public class OrdDetVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ORD_DET_ID")
	private Integer ordDetId;   // 訂單明細編號 (主鍵)

	@ManyToOne
	@EqualsAndHashCode.Exclude
	@JoinColumn(name="ORD_ID",referencedColumnName = "ORD_ID")
	private OrdersVO orders;       // 訂單編號 (外鍵)
	//private Integer ordId;      // 訂單編號 (外鍵)

	@ManyToOne
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "PROD_ID",referencedColumnName = "PROD_ID")
	private ProductVO product;     // 商品編號 (外鍵)

	@Column(name="PROD_QTY")
	private Integer prodQty;    // 商品數量

	@Column(name="PROD_PRICE")
	private Integer prodPrice;  // 商品單價

	@Column(name="ORD_DESC")
	private String ordDesc;     // 訂單備註

	// ==================== 2. 手動實作 equals 和 hashCode ====================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrdDetVO ordDetVO = (OrdDetVO) o;
		// 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
		return ordDetId != null && Objects.equals(ordDetId, ordDetVO.ordDetId);
	}

	@Override
	public int hashCode() {
		// 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
		// 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
		// 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
		return getClass().hashCode();
	}
}
