package com.foodietime.participants.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"groupBuyingCase","member"})
@EqualsAndHashCode(exclude = {"groupBuyingCase","member"})
@Table(name="participants")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class,
		  property  = "parId",
		  scope     = ParticipantsVO.class
		)
public class ParticipantsVO implements Serializable {

	@Id
	@Column(name="PAR_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer parId;  //參與編號
	
	
	@ManyToOne
	@JoinColumn(name="MEM_ID", nullable = false, updatable = false)
	private MemberVO member;  //會員編號(團員)
	
	
	@ManyToOne
	@JoinColumn(name="GB_ID", nullable = false)
	private GroupBuyingCasesVO groupBuyingCase;   //團購編號
	
	
	@Column(name="PAR_PHONE", nullable = false, length = 10)
	@NotEmpty(message="收件人連絡電話: 請勿空白")
	@Pattern(
		    regexp = "^\\d{10}$",
		    message = "收件人連絡電話須為 10 位數字"
		)
	private String parPhone;  //收件人連絡電話
	
	
	@Column(name = "PAR_NAME", nullable = false, length = 45)
	@NotEmpty(message = "收件人姓名: 請勿空白")
	@Pattern(
	    regexp = "^[\u4e00-\u9fa5a-zA-Z0-9_]{2,10}$",
	    message = "收件人姓名: 只能包含中文、英文字母、數字或底線，且長度須在2到10之間"
	)
	private String parName;  //收件人姓名
	
	
	@Column(name = "PAR_ADDRESS", nullable = false, length = 45)
	@NotEmpty(message = "收件人地址: 請勿空白")
	@Pattern(
	    regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9\\s]{5,45}$",
	    message = "收件人地址: 只能包含中文、英文字母、數字或空格，且長度須在5到45字之間"
	)
	private String parAddress;  //收件人地址

	
	@Column(name = "PAR_LONGITUDE", nullable = false, precision = 10, scale = 6)
	@NotNull(message = "地址經度: 不可為空")
	@DecimalMin(value = "-180.0", inclusive = true, message = "地址經度: 最小值為 -180.0")
	@DecimalMax(value = "180.0", inclusive = true, message = "地址經度: 最大值為 180.0")
	private BigDecimal parLongitude;  //地址經度

	
	@Column(name = "PAR_LATITUDE", nullable = false, precision = 10, scale = 6)
    @NotNull(message = "地址緯度: 不可為空")
    @DecimalMin(value = "-90.0", inclusive = true, message = "地址緯度: 最小值為 -90.0")
    @DecimalMax(value = "90.0", inclusive = true, message = "地址緯度: 最大值為 90.0")
    private BigDecimal parLatitude;  //地址緯度
	
	
	@Column(name = "IS_LEADER", nullable = false)
	@NotNull(message = "是否為團長: 請指定 0（團長）或 1（非團長）")
	@Min(value = 0, message = "是否為團長: 僅能為 0（團長）或 1（非團長）")
	@Max(value = 1, message = "是否為團長: 僅能為 0（團長）或 1（非團長）")
	private Byte leader;  // 是否為團長
	

	@Column(name = "PAR_PURCHASE_QUANTITY", nullable = false)
	@NotNull(message = "購買數量: 請勿空白")
	@Min(value = 0, message = "購買數量: 最少須為 0")
	private Integer parPurchaseQuantity;  //購買數量
	

	@Column(name = "PAYMENT_STATUS", nullable = false)
	@NotNull(message = "付款狀態: 請指定 0（未付款）或 1（已付款）")
	@Min(value = 0, message = "付款狀態: 僅能為 0（未付款）或 1（已付款）")
	@Max(value = 1, message = "付款狀態: 僅能為 0（未付款）或 1（已付款）")
	private Byte paymentStatus;  //付款狀態

	
	
	public ParticipantsVO() {  //必需有一個不傳參數建構子
	
	}
		
}
