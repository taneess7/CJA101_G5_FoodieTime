package com.foodietime.gbprodcg.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GbprodcgRepository extends JpaRepository<GbprodcgVO, Integer> {
   
}
