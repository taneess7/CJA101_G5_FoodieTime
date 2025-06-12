package com.foodietime.store.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;
import java.util.Set;

import com.foodietime.accrec.model.AccrecVO;
import com.foodietime.act.model.ActVO;
import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.gbpromotion.model.GbpromotionVO;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
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
	
	//關聯活動
	@OneToMany(mappedBy = "storId", cascade = CascadeType.ALL)
	@OrderBy("storId asc")
	private Set<StoreVO> stores;
	
	
	public Set<StoreVO> getStores() {
		return stores;
	}

	public void setStores(Set<StoreVO> stores) {
		this.stores = stores;
	}

	//2.店家分類物件（外鍵：STORE_CATE_ID）
	@ManyToOne// 多對一，指向分類，新增store時，關聯物件一起新增
    @JoinColumn(name = "STORE_CATE_ID", referencedColumnName = "STORE_CATE_ID") // 外鍵名稱
	private StoreCateVO storeCateId; 
	
	
	public StoreCateVO getStoreCateId() {
		return storeCateId;
	}
	
	public void setStoreCateId(StoreCateVO storeCateId) {
		this.storeCateId = storeCateId;
	}
	
	// 3.店家名稱
	@NotEmpty(message="店家名稱: 請勿空白")
	@Column(name = "STOR_NAME", length = 255)
	private String storName; 
	
	// 4.店家敘述
	@NotEmpty(message="店家敘述: 請勿空白")
	@Column(name = "STOR_DESC", length = 255)
	private String storDesc; 
	
	// 5.店家地址
	@NotEmpty(message="店家地址: 請勿空白")
	@Column(name = "STOR_ADDR")
	private String storAddr; 
	
	// 6.地址經度
	@Column(name = "STOR_LONGITUDE")
	private Double storLon; 
	
	// 7.地址緯度
	@Column(name = "STOR_LATITUDE")
	private Double storLat; 
	
	// 8.店家電話
	@NotEmpty(message="店家電話: 請勿空白")
	@Column(name = "STOR_PHONE", length = 20)
	private String storPhone; 
	
	// 9.店家訂位網址
	@Column(name = "STOR_WEB", length = 255)
	private String storWeb; 
	
	// 10.店家開店時間
	@NotEmpty(message="開店時間: 請勿空白")
	@Column(name = "STOR_OPEN_TIME")
	private Time storOnTime;
	
	// 11.店家關店時間
	@NotEmpty(message="關店時間: 請勿空白")
	@Column(name = "STOR_CLOSE_TIME")
	private Time storOffTime; 
	
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
	@Pattern(regexp = "^(?!\\\\.)[\\\\w!#$%&'*+/=?^`{|}~.-]+(?<!\\\\.)@([A-Za-z0-9-]+\\\\.)+[A-Za-z]{2,}$", message = "信箱格式不正確")
	@NotEmpty(message="Email: 請勿空白")
	@Column(name = "STOR_EMAIL", updatable = false)
	private String storEmail; 
	

	public StoreVO() {
		super();
	}
	
	//OneToMany
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("storId asc")
	private List<StoreVO> store;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<ProductVO> product;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	@OrderBy("actLaunchTime desc") // 自訂排序，依照你的活動時間欄位
	private List<ActVO> act;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<GbprodVO> gbprod;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<GroupOrdersVO> groupOrders;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<GbpromotionVO> gbPromotion;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<MemCouponVO> memCoupon;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<AccrecVO> accrec;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<OrdersVO> orders;

	// 取得or設置

	
}
