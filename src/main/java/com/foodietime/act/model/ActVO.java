package com.foodietime.act.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity")
public class ActVO implements Serializable {

	// 寫上所有欄位
	// 1.活動編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ACT_ID", updatable = false)
	private Integer actId; 
	
	//2.店家物件（外鍵：STORE_ID）
	@ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.PERSIST) // 多對一，指向分類，新增activity時，關聯物件一起新增
    @JoinColumn(name = "STOR_ID", referencedColumnName = "STOR_ID") // 外鍵名稱
	private StoreVO storId;
//	private Integer storId;
	
	public StoreVO getStorId() {
		return storId;
	}

	public void setStorId(StoreVO storId) {
		this.storId = storId;
	}
	
	
	// 3.活動類型
	@Column(name = "ACT_CATE")
	private String actCate; 
	

	// 4.活動名稱
	@Column(name = "ACT_NAME")
	private String actName; 
	
	// 5.活動內容
	@Column(name = "ACT_CONTENT")
	private String actContent; 
	
	// 6.活動建立時間
	@Column(name = "ACT_LAUNCHTIME" , updatable = false)
	private Timestamp actSetTime; 
	 
	//使用活動折扣需同時符合:活動狀態上架、活動開始時間
	// 7.活動開始時間
	@Column(name = "ACT_STARTTIME")
	private Timestamp actStartTime; 
	
	// 8.活動結束時間 
	@Column(name = "ACT_ENDTIME")
	private Timestamp actEndTime; 
	
	// 9.活動狀態 (0:未上架,1:上架) 
	@Column(name = "ACT_STATUS")
	private Byte actStatus; 
	
	// 10.折扣類型 (0:百分比,1:金額)
	@Column(name = "ACT_DISCOUNT")
	private Byte actDiscount; 
	
	// 11.折扣值  
	@Column(name = "ACT_DISCOUNT_VALUE")
	private Double actDiscValue; 
	
	// 12.活動圖片
	@Column(name = "ACT_PHOTO")
	private byte[] actPhoto; 
	
	// 13.最後更新時間
	@Column(name = "ACT_LAST_UPDATE")
	private Timestamp actLastUpdate; 

	// 取得or設置
	public ActVO() {
		super();
	}

	public Integer getActId() {
		return actId;
	}

	public void setActId(Integer actId) {
		this.actId = actId;
	}
	

//	public Integer getStorId() {
//		return storId;
//	}
//
//	public void setStorId(Integer storId) {
//		this.storId = storId;
//	}

	public String getActCate() {
		return actCate;
	}

	public void setActCate(String actCate) {
		this.actCate = actCate;
	}

	public String getActName() {
		return actName;
	}

	public void setActName(String actName) {
		this.actName = actName;
	}

	public String getActContent() {
		return actContent;
	}

	public void setActContent(String actContent) {
		this.actContent = actContent;
	}

	public Timestamp getActSetTime() {
		return actSetTime;
	}

	public void setActSetTime(Timestamp actSetTime) {
		this.actSetTime = actSetTime;
	}

	public Timestamp getActStartTime() {
		return actStartTime;
	}

	public void setActStartTime(Timestamp actStartTime) {
		this.actStartTime = actStartTime;
	}

	public Timestamp getActEndTime() {
		return actEndTime;
	}

	public void setActEndTime(Timestamp actEndTime) {
		this.actEndTime = actEndTime;
	}

	public Byte getActStatus() {
		return actStatus;
	}

	public void setActStatus(Byte actStatus) {
		this.actStatus = actStatus;
	}

	public Byte getActDiscount() {
		return actDiscount;
	}

	public void setActDiscount(Byte actDiscount) {
		this.actDiscount = actDiscount;
	}

	public Double getActDiscValue() {
		return actDiscValue;
	}

	public void setActDiscValue(Double actDiscValue) {
		this.actDiscValue = actDiscValue;
	}

	public byte[] getActPhoto() {
		return actPhoto;
	}

	public void setActPhoto(byte[] actPhoto) {
		this.actPhoto = actPhoto;
	}

	public Timestamp getActLastUpdate() {
		return actLastUpdate;
	}

	public void setActLastUpdate(Timestamp actLastUpdate) {
		this.actLastUpdate = actLastUpdate;
	}

}