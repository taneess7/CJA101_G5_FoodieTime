package com.foodietime.smgauth.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SmgauthRepository extends JpaRepository<SmgauthVO, SmgauthId> {
    // JpaRepository 內建 CRUD 與分頁功能
    
    // 根據管理員ID刪除所有權限
    @Transactional
    @Modifying
    void deleteByIdSmgId(Integer smgId);
    
}
