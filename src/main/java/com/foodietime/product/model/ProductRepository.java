package com.foodietime.product.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductVO, Integer>{

	ProductVO findByProdId(Integer prodId);
	
	void deleteByProdId(Integer prodId);
	
	
}
