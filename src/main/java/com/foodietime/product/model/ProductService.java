package com.foodietime.product.model;

import com.foodietime.product.dto.ProductCardDTO;

import java.util.List;

public interface ProductService {

		List<ProductVO> searchProductsByKeyword(String keyword);

	    List<ProductVO> findByStoreId(Integer storeId);

	    ProductVO addProduct(ProductVO vo, Integer categoryId);

	    ProductVO updateProduct(Integer prodId, ProductVO newData, Integer categoryId);

	    void deleteProduct(Integer prodId);

	    ProductVO getProductById(Integer prodId);

	    List<ProductVO> getAllProducts();

	    // ProductService.java (介面)
	    List<ProductCardDTO> getProductCardsByStoreId(Integer storeId);
	    //美食轉盤
	    ProductVO getRandomProduct();
}
