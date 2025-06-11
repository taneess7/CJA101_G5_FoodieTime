package com.foodietime.smgauth.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmgauthRepository extends JpaRepository<SmgauthVO, SmgauthId> {
    // JpaRepository 內建 CRUD 與分頁功能
    
}
