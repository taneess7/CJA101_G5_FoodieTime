package com.foodietime.accrec.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccrecRepository extends JpaRepository<AccrecVO, Integer> {
     
}
