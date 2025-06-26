package com.foodietime.store.model;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import com.foodietime.accrec.model.AccrecVO;
import com.foodietime.act.model.ActVO;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.gbpromotion.model.GbpromotionVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.memcoupon.model.MemCouponVO;
import com.foodietime.orders.model.OrdersVO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.storeCate.model.StoreCateVO;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// ==================== 1. 移除 @Data，使用更精確的 Lombok 註解 ====================
@Getter
@Setter
// 使用 @ToString 並排除所有集合屬性，防止無限循環和延遲載入問題
@ToString(exclude = {"product", "act", "gbprod", "groupOrders", "groupBuyingCases", "coupon", "accrec", "orders"})
@Entity
@Table(name = "store")
//@NamedQuery(name = "getAllStores", query = "from store where storId > :storId order by storId desc")
public class StoreVO implements Serializable {

	// 寫上所有欄位
	// 1.店家編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STOR_ID", updatable = false)
	private Integer storId; 
	


	//2.店家分類物件（外鍵：STORE_CATE_ID）
	@ManyToOne// 多對一，指向分類，新增store時，關聯物件一起新增
    @JoinColumn(name = "STORE_CATE_ID", referencedColumnName = "STORE_CATE_ID") // 外鍵名稱
	private StoreCateVO storeCate; 
	  
	

	
	// 3.店家名稱
	@NotBlank(message="店家名稱: 請勿空白")
	@Column(name = "STOR_NAME", length = 255)
	private String storName; 
	
	// 4.店家敘述
	@NotBlank(message="店家敘述: 請勿空白")
	@Column(name = "STOR_DESC", length = 255)
	private String storDesc; 
	
	// 5.店家地址
	@NotBlank(message="店家地址: 請勿空白")
	@Column(name = "STOR_ADDR")
	private String storAddr; 
	
	// 6.地址經度
	@NotNull(message = "經度不可為空")
	@Column(name = "STOR_LONGITUDE")
	private Double storLon; 
	
	// 7.地址緯度
	@NotNull(message = "緯度不可為空")
	@Column(name = "STOR_LATITUDE")
	private Double storLat; 
	
	// 8.店家電話
	@NotBlank(message="店家電話: 請勿空白")
	@Column(name = "STOR_PHONE", length = 20)
	private String storPhone; 
	
	// 9.店家訂位網址
	@Column(name = "STOR_WEB", length = 255)
	private String storWeb; 
	
	// 10.店家開店時間
	@NotNull(message="開店時間: 請勿空白")
	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "STOR_OPEN_TIME")
	private LocalTime storOnTime;
	
	// 11.店家關店時間
	@NotNull(message="關店時間: 請勿空白")
	@DateTimeFormat(pattern = "HH:mm")
	@Column(name = "STOR_CLOSE_TIME")
	private LocalTime storOffTime; 
	
	// 12.店家公休日 (0:星期日,1:星期一, 2:星期二,3:星期三,4:星期四,5:星期五,6:星期六)
	@Column(name = "STOR_WEEKLY_OFF_DAY")
	private String storOffDay; 
	
	// 13.提供外送  (1:提供,0:未提供)
	@Column(name = "STOR_DELIVER")
	private Byte storDeliver; 
	
	// 14.營業狀態   (1:營業,0:未營業)
	@Column(name = "STOR_OPEN")
	private Byte storOpen; 
	
	// 15.店家照片
	@Lob
	@Column(name = "STOR_PHOTO", columnDefinition = "longblob")
	private byte[] storPhoto; 
	
	// 16.店家被檢舉次數
	@Min(value = 0)
	@Column(name = "STOR_REPORT_COUNT")
	private Byte storReportCount; 
	
	// 17.總評價數  (星星數)
	@Column(name = "TOTAL_STAR_NUM")
	private Integer starNum; 
	
	// 18.總評價人數
	@Column(name = "TOTAL_REVIEWS")
	private Integer reviews; 
	
	
	// 19.上架狀態 (1:上架 2:未上架)
	@Column(name = "STOR_STATUS")
	private Byte storStatus;  
	
	
	// 20.店家信箱
	@NotBlank(message="不可為空")
	@Pattern(
		    regexp = "^(?!\\.)[\\w!#$%&'*+/=?^`{|}~.-]+(?<!\\.)@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$",
		    message = "信箱格式不符合"
		)
	@Column(name = "STOR_EMAIL", updatable = false)
	private String storEmail; 
	

	public StoreVO() {
		super();
	}
	
	//OneToMany
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<ProductVO> product;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	private List<ActVO> act;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<GbprodVO> gbprod;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<GroupOrdersVO> groupOrders;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<GroupBuyingCasesVO> groupBuyingCases;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<CouponVO> coupon;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<AccrecVO> accrec;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<OrdersVO> orders;

	// 取得or設置 
	// ==================== 2. 手動實作 equals 和 hashCode ====================
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StoreVO storeVO = (StoreVO) o;
		// 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
		return storId != null && Objects.equals(storId, storeVO.storId);
	}

	@Override
	public int hashCode() {
		// 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
		// 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
		// 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
		return getClass().hashCode();
	}
}
