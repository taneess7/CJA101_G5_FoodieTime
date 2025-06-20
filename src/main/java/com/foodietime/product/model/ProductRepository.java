package com.foodietime.product.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodietime.store.model.StoreVO;

public interface ProductRepository extends JpaRepository<ProductVO, Integer>{

	ProductVO findByProdId(Integer prodId);
	
	void deleteByProdId(Integer prodId);
	
	
}
