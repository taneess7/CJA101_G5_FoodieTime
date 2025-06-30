package com.foodietime.product.model;

import java.util.List;

public interface ProductService {

		List<ProductVO> searchProductsByKeyword(String keyword);

	    List<ProductVO> findByStoreId(Integer storeId);

	    ProductVO addProduct(ProductVO vo, Integer categoryId);

	    ProductVO updateProduct(Integer prodId, ProductVO newData, Integer categoryId);

	    void deleteProduct(Integer prodId);

	    ProductVO getProductById(Integer prodId);

	    List<ProductVO> getAllProducts();

	    //美食轉盤
	    ProductVO getRandomProduct();
}
