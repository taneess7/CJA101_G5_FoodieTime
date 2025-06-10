package com.foodietime.storeCate.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.store.model.StoreVO;

public interface StoreCateRepository extends JpaRepository<StoreCateVO, Integer>{

	@Transactional
	@Modifying //非查詢，會動到資料庫的操作
	@Query(value = "delet from store_category where storeCateId =?1", nativeQuery = true)
	void deleteByStorCateId(int storeCateId);
	

}
