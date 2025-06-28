package com.foodietime.groupbuyingcases.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListVO;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.grouppurchasereport.model.GroupPurchaseReportVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.participants.model.ParticipantsVO;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity
@Data
@Table(name="group_buying_cases")
@ToString(exclude = {"participants","groupPurchaseReport","groupBuyingCollectionList","groupOrders"})
@EqualsAndHashCode(exclude = {"participants","groupPurchaseReport","groupBuyingCollectionList","groupOrders"})
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class,
		  property  = "gbId",    // 子類別用自己的 id 屬性
		  scope     = GroupBuyingCasesVO.class
		)
public class GroupBuyingCasesVO implements Serializable{

	@Id
	@Column(name="GB_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer gbId;  //團購編號
	
	
	@ManyToOne
	@JoinColumn(name="STOR_ID", nullable = false)
	private StoreVO store;  //店家編號
	
	
	@ManyToOne
	@JoinColumn(name="GB_PROD_ID", nullable = false)
	private GbprodVO gbProd;  //商品編號
	
	
	@ManyToOne
	@JoinColumn(name="MEM_ID", nullable = false)
	private MemberVO member;  //會員編號(團主)
	
	
	@Column(name = "GB_START_TIME", nullable = false)
	@NotNull(message = "開始時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  
	private LocalDateTime gbStartTime;  // 開始時間

	
	@Column(name = "GB_END_TIME", nullable = false)
	@NotNull(message = "結束時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  
	private LocalDateTime gbEndTime;  //結束時間
	
	
	@Column(name = "GB_TITLE", nullable = false, length = 45)
	@NotBlank(message = "團購標題: 請勿空白")
	@Size(max = 45, message = "團購標題: 最多 45 個字元")
	private String gbTitle;  //團購標題
	
	
	@Column(name = "GB_DESCRIPTION", nullable = false, length = 255)
	@NotBlank(message = "團購簡介: 請勿空白")
	@Size(max = 255, message = "團購簡介: 最多 255 個字元")
	private String gbDescription;  //團購簡介
	
	
	@Column(name = "GB_STATUS", nullable = false)
	@NotNull(message = "團購狀態: 請勿空白")
	@Min(value = 0, message = "團購狀態: 最小值為 0")
	@Max(value = 6, message = "團購狀態: 最大值為 6")
	private Byte gbStatus;  // 團購狀態 (0: 待開團, 1: 招募中, 2: 即將截止, 3: 已開團, 4: 已截止, 5: 已取消, 6: 開團失敗)

	
	@Column(name = "GB_CREATE_AT", nullable = false, updatable = false)
	@NotNull(message = "團購建立時間: 請勿空白")
	@PastOrPresent(message = "團購建立時間: 不可晚於目前時間")
	private LocalDateTime gbCreateAt;  //團購建立時間(條件成立才會生成團購建立時間)Service 手動 setGbCreateAt(new Date())
	
	
	@Column(name = "GB_MIN_PRODUCT_QUANTITY", nullable = false)
	@NotNull(message = "最低成團商品數量: 請勿空白")
	@Min(value = 1, message = "最低成團商品數量: 最少須為 1")
	private Integer gbMinProductQuantity;  // 最低成團商品數量


	@Column(name = "CANCEL_REASON", length = 65)
	@Size(max = 65, message = "取消原因: 最多 65 個字元")
	private String cancelReason;  // 取消原因

	
	@Column(name = "CUMULATIVE_PURCHASE_QUANTITY", nullable = false)
	@NotNull(message = "累計購買數量: 請勿空白")
	@Min(value = 0, message = "累計購買數量: 最少須為 0")
	private Integer cumulativePurchaseQuantity;  // 累計購買數量

	
	public GroupBuyingCasesVO() {  //必需有一個不傳參數建構子
		
	}
	
	@OneToMany(mappedBy = "groupBuyingCase", cascade = CascadeType.ALL)
	private List<ParticipantsVO> participants;
	
	@OneToMany(mappedBy = "groupBuyingCase", cascade = CascadeType.ALL)
    private List<GroupPurchaseReportVO> groupPurchaseReport;
	
	@OneToMany(mappedBy = "groupBuyingCase", cascade = CascadeType.ALL)
    private List<GroupBuyingCollectionListVO> groupBuyingCollectionList;
	
	@OneToMany(mappedBy = "groupBuyingCase", cascade = CascadeType.ALL)
    private List<GroupOrdersVO> groupOrders;
	
	public String getFormattedGbStartTime() {
        return gbStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

	public String getFormattedGbEndTime() {
        return gbEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
	
}
