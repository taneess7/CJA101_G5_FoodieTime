package com.foodietime.product.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryVO, Integer>{

	ProductCategoryVO findByProdCateId(Integer prodCateId);
	
	void deleteByProdCateId(Integer prodCateId);

	// 根據分類名稱查找分類
//    ProductCategoryVO findByProdCate(String prodCate);
}
