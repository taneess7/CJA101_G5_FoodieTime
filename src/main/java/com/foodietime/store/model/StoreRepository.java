package com.foodietime.store.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import com.foodietime.storeCate.model.StoreCateVO;

public interface StoreRepository extends JpaRepository<StoreVO, Integer> {
	
	@Transactional
	@Modifying //非查詢，會動到資料庫的操作
	@Query(value = "delet from store where storId =?1", nativeQuery = true)
	void deleteByStorId(int storId);
	

	
	//● (自訂)條件查詢
	@Query(value = "from StoreVO where storDeliver=?1 and storOpen=?2 order by storId")
	List<StoreVO> findByOthers(byte storDeliver, byte storOpen);

	
	
	StoreVO findByEmail(String email);
}
