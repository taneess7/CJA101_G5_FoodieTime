package com.foodietime.product.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
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
	
	// 一個分類對應多個商品
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVO> productList;
    
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
