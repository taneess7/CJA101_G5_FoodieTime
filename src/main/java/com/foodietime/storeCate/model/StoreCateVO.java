package com.foodietime.storeCate.model;

import java.io.Serializable;

public class StoreCateVO implements Serializable {

	// 寫上所有欄位
	private Integer storCateId; // 店家分類編號.
	private String storCat; // 類型名稱

	// 取得or設置

	public StoreCateVO() {
		super();
	}

	public Integer getStorCateId() {
		return storCateId;
	}

	public void setStorCateId(Integer storCateId) {
		this.storCateId = storCateId;
	}

	public String getStorCat() {
		return storCat;
	}

	public void setStorCat(String storCat) {
		this.storCat = storCat;
	}

}
