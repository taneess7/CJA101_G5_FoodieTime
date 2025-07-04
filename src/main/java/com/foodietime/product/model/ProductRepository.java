package com.foodietime.product.model;

import java.util.List;

import com.foodietime.product.dto.ProductCardDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface ProductRepository extends JpaRepository<ProductVO, Integer>{

	ProductVO findByProdId(Integer prodId);
	
	void deleteByProdId(Integer prodId);
	
	List<ProductVO> findByStore_StorId(Integer storeId);  // ✔ 注意是 StoreVO 裡面的屬性

	@Query("SELECT new com.foodietime.product.dto.ProductCardDTO(" +
			"p.prodId, p.prodName, p.prodDesc, p.prodPrice, s.storName, pc.prodCate) " +
			"FROM ProductVO p " +
			"JOIN p.store s " +
			"JOIN p.productCategory pc " +
			"WHERE s.storId = :storeId")
	List<ProductCardDTO> findProductCardsByStoreId(@Param("storeId") Integer storeId);

	//搜尋「商品名稱 + 商品描述」
	@Query("SELECT p FROM ProductVO p WHERE " +
		       "LOWER(p.prodName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(p.prodDesc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
		List<ProductVO> searchByKeyword(@Param("keyword") String keyword);
	
	//美食轉盤
	@Query("SELECT p.prodId FROM ProductVO p WHERE p.prodStatus = true") // 可加條件過濾下架商品
	List<Integer> findAllProductIds();
}
