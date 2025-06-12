package com.foodietime.grouporders.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name="group_orders")
public class GroupOrdersVO implements Serializable{ 
	
	@Id
	@Column(name="GB_OR_ID", nullable = false)
	private Integer gbOrId;  //團購訂單編號
	
	
	@ManyToOne
	@JoinColumn(name="GB_ID", nullable = false)
	private GroupBuyingCasesVO groupbuyingcases;  //團購編號
	
	
	@ManyToOne
	@JoinColumn(name="STOR_ID", nullable = false)
	private StoreVO store;  //店家編號
	
	
	@ManyToOne
	@JoinColumn(name="GB_PROD_ID", nullable = false)
	private GbprodVO gbprod;  //商品編號
	
	
	@Column(name = "JOIN_TIME", nullable = false, updatable = false)
	@NotNull(message = "參與時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime joinTime;  // 參與時間

	
	
	@Column(name="AMOUNT", nullable = false)
	@NotNull(message = "訂單總金額: 請勿空白")
	@Min(value = 0, message = "訂單總金額: 最少須為 0")
	private Integer amount;  //訂單總金額
	
	
	@Column(name="QUANTITY", nullable = false)
	@NotNull(message = "購買數量: 請勿空白")
	@Min(value = 1, message = "購買數量: 最少須為 1")
	private Integer quantity;  //購買數量
	
	
	@Column(name = "PAY_METHOD", nullable = false)
    @NotNull(message = "付款方式: 請選擇付款方式")
    @Min(value = 0, message = "付款方式: 無效的付款方式")
    @Max(value = 2, message = "付款方式: 無效的付款方式")
    private Byte payMethod;  // 付款方式 (0: 信用卡, 1: 現金, 2: 第三方)
	
	
	@Column(name = "ORDER_STATUS", nullable = false)
    @NotNull(message = "訂單狀態: 請指定訂單狀態")
    @Min(value = 0, message = "訂單狀態: 最小值為 0")
    @Max(value = 3, message = "訂單狀態: 最大值為 3")
    private Byte orderStatus;  // 訂單狀態 (0: 未接單, 1: 接單, 2: 完成, 3: 取消)

	
	@Column(name = "PAYMENT_STATUS", nullable = false)
    @NotNull(message = "付款狀態: 請指定付款狀態")
    @Min(value = 0, message = "付款狀態: 最小值為 0")
    @Max(value = 1, message = "付款狀態: 最大值為 1")
    private Byte paymentStatus;  // 付款狀態 (0: 未付款, 1: 已付款)

	
	@Column(name = "SHIPPING_STATUS", nullable = false)
    @NotNull(message = "出貨狀態: 請指定出貨狀態")
    @Min(value = 0, message = "出貨狀態: 最小值為 0")
    @Max(value = 1, message = "出貨狀態: 最大值為 1")
    private Byte shippingStatus;  // 出貨狀態 (0: 未出貨, 1: 已出貨)

	
	@Column(name = "PAR_NAME", nullable = false, length = 45)
    @NotBlank(message = "收件人姓名: 請勿空白")
    @Size(max = 45, message = "收件人姓名: 最多 45 個字元")
    private String parName;  // 收件人姓名

	
	@Column(name = "PAR_ADDRESS", nullable = false, length = 45)
    @NotBlank(message = "收件人地址: 請勿空白")
    @Size(max = 45, message = "收件人地址: 最多 45 個字元")
    private String parAddress;  // 收件人地址
	
	
	@Column(name = "PAR_LONGITUDE", nullable = false, precision = 10, scale = 6)
    @NotNull(message = "地址經度: 請勿空白")
    @DecimalMin(value = "-180.0", inclusive = true, message = "地址經度: 最小值為 -180.0")
    @DecimalMax(value = "180.0", inclusive = true, message = "地址經度: 最大值為 180.0")
    private BigDecimal parLongitude;  // 地址經度
	
	
	@Column(name = "PAR_LATITUDE", nullable = false, precision = 10, scale = 6)
	@NotNull(message = "地址緯度: 請勿空白")
	@DecimalMin(value = "-90.0", inclusive = true, message = "地址緯度: 最小值為 -90.0")
	@DecimalMax(value = "90.0", inclusive = true, message = "地址緯度: 最大值為 90.0")
	private BigDecimal parLatitude;  // 地址緯度

	
	@Column(name = "PAR_PHONE", nullable = false, length = 10)
    @NotBlank(message = "收件人聯絡電話: 請勿空白")
    @Pattern(regexp = "^\\d{10}$", message = "收件人聯絡電話: 必須為 10 位數字")
    private String parPhone;  // 收件人聯絡電話
	
	
	@Column(name = "DELIVERY_METHOD", nullable = false)
    @NotNull(message = "配送方式: 請選擇配送方式")
    @Min(value = 0, message = "配送方式: 無效的配送方式")
    @Max(value = 1, message = "配送方式: 無效的配送方式")
    private Byte deliveryMethod;  // 配送方式 (0: 宅配, 1: 自取)
	
	
	@Column(name = "COMMENT", length = 255)
    @Size(max = 255, message = "評價: 最多 255 個字元")
    private String comment;  // 評價

	
	@Column(name = "RATING", nullable = false)
    @NotNull(message = "星等: 請指定評價星等")
    @Min(value = 1, message = "星等: 最少 1 顆星")
    @Max(value = 5, message = "星等: 最多 5 顆星")
    private Byte rating;  // 星等 (1: 1 顆星, 2: 2 顆星, ... 5: 5 顆星)

	
		
	public GroupOrdersVO() {  //必需有一個不傳參數建構子
		
	}
	
	
}
