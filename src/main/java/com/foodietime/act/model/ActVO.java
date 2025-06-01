package com.foodietime.act.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ActVO implements Serializable {

	// 寫上所有欄位
	private Integer actId; // 活動編號
	private Integer storId; // 店家編號
	private String actCate; // 活動類型
	private String actName; // 活動名稱
	private String actContent; // 活動內容
	private Timestamp actSetTime; // 活動建立時間
	private Timestamp actStartTime; // 活動開始時間
	private Timestamp actEndTime; // 活動結束時間
	private Byte actStatus; // 活動狀態
	private Byte actDiscount; // 折扣類型
	private Double actDiscValue; // 折扣值
	private byte[] actPhoto; // 活動圖片
	private Timestamp actLastUpdate; // 最後更新時間

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

	public Integer getStorId() {
		return storId;
	}

	public void setStorId(Integer storId) {
		this.storId = storId;
	}

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