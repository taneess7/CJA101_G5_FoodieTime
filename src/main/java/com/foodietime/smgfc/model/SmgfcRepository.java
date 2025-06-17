package com.foodietime.smgfc.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmgfcRepository extends JpaRepository<SmgfcVO, Integer> {
    
}
