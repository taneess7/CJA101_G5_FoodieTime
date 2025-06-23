package com.foodietime.coupon.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.storeCate.model.StoreCateVO;

public interface CouponRepository extends JpaRepository<CouponVO, Integer>{
	
	@Transactional
	@Modifying //非查詢，會動到資料庫的操作
	@Query(value = "delete from coupon where COU_ID =?1", nativeQuery = true)
	void deleteByCouId(int couId);
	

}
