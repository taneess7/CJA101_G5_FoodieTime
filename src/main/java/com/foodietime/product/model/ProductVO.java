package com.foodietime.product.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.foodietime.cart.model.CartVO;
import com.foodietime.memfavlist.model.FavoriteListVO;
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "product")
public class ProductVO implements Serializable{
	
	@Id //主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) //自動增加
	@Column(name = "prod_id")
	private Integer prodId;
	
	@Column(name = "stor_id",nullable = false, insertable = false, updatable = false)
	private Integer storId;
	
	@ManyToOne
    @JoinColumn(name = "stor_id", referencedColumnName = "stor_id")
    private StoreVO store;
	
	@ManyToOne
	@JoinColumn(name ="prod_cate_id",nullable = false) // 外鍵，連到 product_category.prod_cate_id
	private ProductCategoryVO productCategory;
	
	@Column(name ="prod_name",nullable = false,length = 45)
	private String prodName;
	
	@Column(name = "prod_desc",nullable = false,length = 45)
	private String prodDesc;
	
	@Column(name = "prod_price",nullable = false)
	private Integer prodPrice;
	
	@Column(name = "prod_updatetime",nullable = false)
	private Timestamp prodUpdateTime;
	
	@Column(name = "prod_status",nullable = false)
	private Boolean prodStatus;
	
	@Lob
	@Column(name = "prod_photo")
	private byte[] prodPhoto;
	
	@Column(name = "prod_last_update",nullable = false)
	private Timestamp prodLastUpdate;
	
	@Column( name = "prod_report_count")
	private Integer prodReportCount;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrdDetVO> orderDetails;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<CartVO> cart;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<FavoriteListVO> favoriteList;
}
