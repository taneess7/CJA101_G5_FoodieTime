package com.foodietime.product.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface ProductRepository extends JpaRepository<ProductVO, Integer>{

	ProductVO findByProdId(Integer prodId);
	
	void deleteByProdId(Integer prodId);
	
	List<ProductVO> findByStore_StorId(Integer storeId);  // ✔ 注意是 StoreVO 裡面的屬性
	
	//搜尋「商品名稱 + 商品描述」
	@Query("SELECT p FROM ProductVO p WHERE " +
		       "LOWER(p.prodName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(p.prodDesc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
		List<ProductVO> searchByKeyword(@Param("keyword") String keyword);
	
	//美食轉盤
//	@Query(value = "SELECT * FROM product ORDER BY RAND() LIMIT 1", nativeQuery = true)
//	ProductVO findRandomProduct();
	
}
