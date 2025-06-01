package com.foodietime.gbpromotion.model;

import java.io.Serializable;
import java.util.Date;

public class GbpromotionVO implements Serializable{
	private Integer gbPromoId;                              
	private Integer gbProdId;
	private Integer gbMinQty;
	private Date promotStart;                 
	private Date promotEnd;                               
	private Integer gbProdSales;             
	private Integer gbProdSpe;
	public Integer getGbPromoId() {
		return gbPromoId;
	}
	public void setGbPromoId(Integer gbPromoId) {
		this.gbPromoId = gbPromoId;
	}
	public Integer getGbProdId() {
		return gbProdId;
	}
	public void setGbProdId(Integer gbProdId) {
		this.gbProdId = gbProdId;
	}
	public Integer getGbMinQty() {
		return gbMinQty;
	}
	public void setGbMinQty(Integer gbMinQty) {
		this.gbMinQty = gbMinQty;
	}
	public Date getPromotStart() {
		return promotStart;
	}
	public void setPromotStart(Date promotStart) {
		this.promotStart = promotStart;
	}
	public Date getPromotEnd() {
		return promotEnd;
	}
	public void setPromotEnd(Date promotEnd) {
		this.promotEnd = promotEnd;
	}
	public Integer getGbProdSales() {
		return gbProdSales;
	}
	public void setGbProdSales(Integer gbProdSales) {
		this.gbProdSales = gbProdSales;
	}
	public Integer getGbProdSpe() {
		return gbProdSpe;
	}
	public void setGbProdSpe(Integer gbProdSpe) {
		this.gbProdSpe = gbProdSpe;
	}
	
	
	     

}
