package com.foodietime.smg.model;

import com.foodietime.smg.model.SmgVO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SmgRepository extends JpaRepository<SmgVO, Integer> {
    // JpaRepository 內建 CRUD 與分頁功能
    // 可以根據需求自訂方法，例如：
    // Optional<SmgVO> findBySmgrAccount(String account);
	SmgVO findBySmgrAccount(String smgrAccount); 
	
	
	
	
	@Query("SELECT s FROM SmgVO s WHERE " +
		       "(:account IS NULL OR s.smgrAccount LIKE CONCAT('%', :account, '%')) AND " +
		       "(:name IS NULL OR s.smgrName LIKE CONCAT('%', :name, '%')) AND " +
		       "(:email IS NULL OR s.smgrEmail LIKE CONCAT('%', :email, '%'))")
		List<SmgVO> findByConditions(@Param("account") String account,
		                             @Param("name") String name,
		                             @Param("email") String email);
}
