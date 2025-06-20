package com.foodietime.store.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.product.model.ProductVO;




public interface StoreRepository extends JpaRepository<StoreVO, Integer> {
	
	
	
	
	@Transactional
	@Modifying //非查詢，會動到資料庫的操作
	@Query(value = "delete from store where STOR_ID =?1", nativeQuery = true) //nativeQuery = true 時，需要使用資料庫的表格和欄位名稱 (SQL)。
	void deleteByStorId(int storId);
	

	
//	//● (自訂)條件查詢
//	@Query(value = "from StoreVO where storDeliver =?1 and storOpen =?2 order by storId")
//	List<StoreVO> findByOthers(byte storDeliver, byte storOpen);

	//找店家分類  找出StoreVO ManyToOne storeCate屬性 呼叫 StoreCateVO 裡的storCatName屬性
	@Query("SELECT s FROM StoreVO s WHERE s.storeCate.storCatName LIKE :cateName")  //取得這間店的分類名稱，例如「泰式」或「中式」 比對是否等於你傳進來的參數，例如 :cateName = "泰式"
	List<StoreVO> findByStoreCateNameLike(@Param("cateName") String cateName);
	
	//List<StoreVO> 回傳值：回傳多筆店家資料（StoreVO 是一間店）
	//@Param("cateName") 對應到 JPQL 裡的 :cateName 參數
	//String cateName 傳入的分類名稱，例如 "泰式"、"日式"
	
	
	//ProductRepository 查屬於該店家id 的商品
	//List<ProductVO> findByStore_StorId(Integer storId);
	
	//撈商品 ID 列表（用 native SQL）
	@Query(value = "SELECT PROD_ID FROM PRODUCT WHERE STOR_Id =?1", nativeQuery = true) 
	List<Integer> findProdIdsByStorId(int storId);
	
	//撈整筆商品資料（使用 native SQL）
	@Query(value = "SELECT * FROM PRODUCT WHERE STOR_Id =?1", nativeQuery = true) 
	List<ProductVO> findProdsByStorId(int storId);
	
	//撈商品照片（使用 native SQL）
	@Query(value = "SELECT PROD_PHOTO FROM PRODUCT WHERE STOR_ID =?1", nativeQuery = true) 
	List<byte[]> findProdPhotosByStorId(int storId); 
	
	StoreVO findByStorEmail(String email);



	@Query("SELECT s FROM StoreVO s WHERE s.storeCate.storCateId = :cateId")
    List<StoreVO> findByStoreCateId(@Param("cateId") Integer cateId);
	
	

	
	List<StoreVO> findByStoreCate_StorCateId(Integer id);
	
}
