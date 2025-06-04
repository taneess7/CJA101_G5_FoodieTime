package com.foodietime.product.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "product_category")
public class ProductCategoryVO implements Serializable{
	
	@Id //主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) //自動新增
	@Column(name = "prod_cate_id")
	private Integer prodCateId; //自動新增
	
	@Column(name = "prod_cate",nullable = false)
	private String prodCate;
	
//	public Integer getProdCateId() {
//		return prodCateId;
//	}
//	public void setProdCateId(Integer prodCateId) {
//		this.prodCateId = prodCateId;
//	}
//	public String getProdCate() {
//		return prodCate;
//	}
//	public void setProdCate(String prodCate) {
//		this.prodCate = prodCate;
//	}
}
