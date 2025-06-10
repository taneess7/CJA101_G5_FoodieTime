package com.foodietime.act.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.storeCate.model.StoreCateVO;

public interface ActRepository extends JpaRepository<ActVO, Integer>{
	
	@Transactional
	@Modifying //非查詢，會動到資料庫的操作
	@Query(value = "delet from activity where ACT_ID =?1", nativeQuery = true)
	void deleteByactId(int actId);
	

}
