package com.foodietime.gbprod.model;

import java.io.Serializable;
import java.util.Date;

public class GbprodVO implements Serializable{
	private Integer gbProdId;
	private Integer storId;
	private Integer gbCaseId;
	private String  gbProdName;
	private String  gbProdDescription;
	private Integer gbProdQuantity;
	private Byte 	gbProdStatus;
	private Date	updateAt;
	private String	gbProdSpecification;
	private Byte []	gbProdPhoto;
	private String	gbProdEvaluate;
	private Byte	gbProdReportCount;
	
	public Integer getGbProdId() {
		return gbProdId;
	}
	public void setGbProdId(Integer gbProdId) {
		this.gbProdId = gbProdId;
	}
	public Integer getStorId() {
		return storId;
	}
	public void setStorId(Integer storId) {
		this.storId = storId;
	}
	public Integer getGbCaseId() {
		return gbCaseId;
	}
	public void setGbCaseId(Integer gbCaseId) {
		this.gbCaseId = gbCaseId;
	}
	public String getGbProdName() {
		return gbProdName;
	}
	public void setGbProdName(String gbProdName) {
		this.gbProdName = gbProdName;
	}
	public String getGbProdDescription() {
		return gbProdDescription;
	}
	public void setGbProdDescription(String gbProdDescription) {
		this.gbProdDescription = gbProdDescription;
	}
	public Integer getGbProdQuantity() {
		return gbProdQuantity;
	}
	public void setGbProdQuantity(Integer gbProdQuantity) {
		this.gbProdQuantity = gbProdQuantity;
	}
	public Byte getGbProdStatus() {
		return gbProdStatus;
	}
	public void setGbProdStatus(Byte gbProdStatus) {
		this.gbProdStatus = gbProdStatus;
	}
	public Date getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	public String getGbProdSpecification() {
		return gbProdSpecification;
	}
	public void setGbProdSpecification(String gbProdSpecification) {
		this.gbProdSpecification = gbProdSpecification;
	}
	public Byte[] getGbProdPhoto() {
		return gbProdPhoto;
	}
	public void setGbProdPhoto(Byte[] gbProdPhoto) {
		this.gbProdPhoto = gbProdPhoto;
	}
	public String getGbProdEvaluate() {
		return gbProdEvaluate;
	}
	public void setGbProdEvaluate(String gbProdEvaluate) {
		this.gbProdEvaluate = gbProdEvaluate;
	}
	public Byte getGbProdReportCount() {
		return gbProdReportCount;
	}
	public void setGbProdReportCount(Byte gbProdReportCount) {
		this.gbProdReportCount = gbProdReportCount;
	}
	
}
