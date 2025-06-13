package com.foodietime.storeCate.model;

import java.io.Serializable;
import java.util.Set;


import com.foodietime.store.model.StoreVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "store_category")
public class StoreCateVO implements Serializable {

	// 寫上所有欄位
	
	// 1.類型編號
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STORE_CATE_ID", updatable = false)
	private Integer storCateId; // 店家分類編號.
	
	@OneToMany(mappedBy = "storeCate", cascade = CascadeType.ALL)
	@OrderBy("storId asc")
	private Set<StoreVO> store;
	
	
	// 2.類型名稱
	@Column(name = "STORE_CATE")
	private String storCatName; 

	// 取得or設置

	public StoreCateVO() {
		super();
	}

}
