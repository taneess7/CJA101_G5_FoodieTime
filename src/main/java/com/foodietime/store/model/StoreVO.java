package com.foodietime.store.model;

import java.io.Serializable;
import java.sql.Time;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import com.foodietime.storeCate.model.StoreCateVO;

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
	
	
	// 2.店家分類編號
	@ManyToOne(cascade = CascadeType.PERSIST) // 多對一，指向分類，新增store時，關聯物件一起新增
    @JoinColumn(name = "STORE_CATE_ID", referencedColumnName = "STORE_CATE_ID") // 外鍵名稱
	private StoreCateVO storeCateId; 
	
	
	public StoreCateVO getStoreCate() {
		return storeCateId;
	}
	
	public void setStoreCate(StoreCateVO storeCateId) {
		this.storeCateId = storeCateId;
	}
	
	// 3.店家名稱
	@Column(name = "STOR_NAME")
	private String storName; 
	
	// 4.店家敘述
	@Column(name = "STOR_DESC")
	private String storDesc; 
	
	// 5.店家地址
	@Column(name = "STOR_ADDR")
	private String storAddr; 
	
	// 6.地址經度
	@Column(name = "STOR_LONGITUDE")
	private Double storLon; 
	
	// 7.地址緯度
	@Column(name = "STOR_LATITUDE")
	private Double storLat; 
	
	// 8.店家電話
	@Column(name = "STOR_PHONE")
	private String storPhone; 
	
	// 9.店家訂位網址
	@Column(name = "STOR_WEB")
	private String storWeb; 
	
	// 10.店家開店時間
	@Column(name = "STOR_OPEN_TIME")
	private Time storOnTime;
	
	// 11.店家關店時間
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

	public StoreVO() {
		super();
	}

	// 取得or設置

	public Integer getStorId() {
		return storId;
	}

	public void setStorId(Integer storId) {
		this.storId = storId;
	}



	public String getStorName() {
		return storName;
	}

	public void setStorName(String storName) {
		this.storName = storName;
	}

	public String getStorDesc() {
		return storDesc;
	}

	public void setStorDesc(String storDesc) {
		this.storDesc = storDesc;
	}

	public String getStorAddr() {
		return storAddr;
	}

	public void setStorAddr(String storAddr) {
		this.storAddr = storAddr;
	}

	public Double getStorLon() {
		return storLon;
	}

	public void setStorLon(Double storLon) {
		this.storLon = storLon;
	}

	public Double getStorLat() {
		return storLat;
	}

	public void setStorLat(Double storLat) {
		this.storLat = storLat;
	}

	public String getStorPhone() {
		return storPhone;
	}

	public void setStorPhone(String storPhone) {
		this.storPhone = storPhone;
	}

	public String getStorWeb() {
		return storWeb;
	}

	public void setStorWeb(String storWeb) {
		this.storWeb = storWeb;
	}

	public Time getStorOnTime() {
		return storOnTime;
	}

	public void setStorOnTime(Time storOnTime) {
		this.storOnTime = storOnTime;
	}

	public Time getStorOffTime() {
		return storOffTime;
	}

	public void setStorOffTime(Time storOffTime) {
		this.storOffTime = storOffTime;
	}

	public String getStorOffDay() {
		return storOffDay;
	}

	public void setStorOffDay(String storOffDay) {
		this.storOffDay = storOffDay;
	}

	public Byte getStorDeliver() {
		return storDeliver;
	}

	public void setStorDeliver(Byte storDeliver) {
		this.storDeliver = storDeliver;
	}

	public Byte getStorOpen() {
		return storOpen;
	}

	public void setStorOpen(Byte storOpen) {
		this.storOpen = storOpen;
	}



	public byte[] getStorPhoto() {
		return storPhoto;
	}

	public void setStorPhoto(byte[] storPhoto) {
		this.storPhoto = storPhoto;
	}

	public Byte getStorReportCount() {
		return storReportCount;
	}

	public void setStorReportCount(Byte storReportCount) {
		this.storReportCount = storReportCount;
	}

	public Integer getStarNum() {
		return starNum;
	}

	public void setStarNum(Integer starNum) {
		this.starNum = starNum;
	}

	public Integer getReviews() {
		return reviews;
	}

	public void setReviews(Integer reviews) {
		this.reviews = reviews;
	}
	
	public Byte getStorStatus() {
		return storStatus;
	}

	public void setStorStatus(Byte storStatus) {
		this.storStatus = storStatus;
	}

}
