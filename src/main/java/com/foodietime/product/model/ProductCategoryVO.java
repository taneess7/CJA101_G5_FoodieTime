package com.foodietime.product.model;

import java.io.Serializable;

public class ProductCategoryVO implements Serializable{
	private Integer prodCateId;
	private String prodCate;
	public Integer getProdCateId() {
		return prodCateId;
	}
	public void setProdCateId(Integer prodCateId) {
		this.prodCateId = prodCateId;
	}
	public String getProdCate() {
		return prodCate;
	}
	public void setProdCate(String prodCate) {
		this.prodCate = prodCate;
	}
	
}
