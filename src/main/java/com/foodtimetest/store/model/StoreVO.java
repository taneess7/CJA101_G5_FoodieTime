package com.foodtimetest.store.model;

import java.io.Serializable;
import java.sql.Time;

public class StoreVO implements Serializable {

	// 寫上所有欄位
	private Integer storId; // 店家編號
	private Integer storeCateId; // 店家分類編號
	private String storName; // 店家名稱
	private String storDesc; // 店家敘述
	private String storAddr; // 店家地址
	private Double storLon; // 地址經度
	private Double storLat; // 地址緯度
	private String storPhone; // 店家電話
	private String storWeb; // 店家訂位網址
	private Time storOnTime; // 店家開店時間
	private Time storOffTime; // 店家關店時間
	private String storOffDay; // 店家公休日
	private Byte storDeliver; // 提供外送
	private Byte storStatus; // 營業狀態
	private byte[] storPhoto; // 店家照片
	private Byte storReportCount; // 店家被檢舉次數
	private Integer starNum; // 總評價數
	private Integer reviews; // 總評價人數

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

	public Integer getStoreCateId() {
		return storeCateId;
	}

	public void setStoreCateId(Integer storeCateId) {
		this.storeCateId = storeCateId;
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

	public Byte getStorStatus() {
		return storStatus;
	}

	public void setStorStatus(Byte storStatus) {
		this.storStatus = storStatus;
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

}
