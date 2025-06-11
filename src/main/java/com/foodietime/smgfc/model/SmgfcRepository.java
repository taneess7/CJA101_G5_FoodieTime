package com.foodietime.smgfc.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmgfcRepository extends JpaRepository<SmgfcVO, Integer> {
    // JpaRepository 內建 CRUD 與分頁功能
    // 可以根據需求自訂方法，例如：
    // Optional<SmgVO> findBySmgrAccount(String account);
	SmgVO findBySmgrAccount(String smgrAccount); 
}
