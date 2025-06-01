package com.foodietime.gbprodcg.model;

import java.io.Serializable;

public class GbprodcgVO implements Serializable{
	private Integer gbCateId;
	private String gbCateName;
	
	
	public Integer getGbCateId() {
		return gbCateId;
	}
	public void setGbCateId(Integer gbCateId) {
		this.gbCateId = gbCateId;
	}
	public String getGbCateName() {
		return gbCateName;
	}
	public void setGbCateName(String gbCateName) {
		this.gbCateName = gbCateName;
	}
}
