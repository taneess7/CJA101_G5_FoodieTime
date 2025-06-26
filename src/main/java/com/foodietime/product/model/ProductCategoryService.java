package com.foodietime.product.model;

import java.util.List;

import com.foodietime.store.model.StoreVO;

public interface ProductCategoryService {

	ProductCategoryVO findById(Integer cataId);
	List<StoreVO> getStoresByCategoryId(Integer cateId);

	List<StoreVO> searchStoresByKeyword(String keyword);
}
