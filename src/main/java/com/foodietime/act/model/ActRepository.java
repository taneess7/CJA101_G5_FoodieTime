package com.foodietime.act.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;



public interface ActRepository extends JpaRepository<ActVO, Integer>{
	
	@Transactional
	@Modifying //非查詢，會動到資料庫的操作
	@Query(value = "delet from activity where actId =?1", nativeQuery = true)
	void deleteByactId(int actId);
	
	
	//取得活動區間
	@Query("SELECT a FROM ActVO a WHERE a.actStartTime >= :start AND a.actEndTime <= :end")
	List<ActVO> findActsBetween(@Param("start") String start, @Param("end") String end);
	
	//取得活動照片
	@Query("SELECT a.actPhoto FROM ActVO a WHERE a.actId = :id")
	byte[] findActPhotoById(@Param("id") Integer id);
	
	 
	
}



