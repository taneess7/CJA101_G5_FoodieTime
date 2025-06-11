package com.foodietime.gbprod.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GbprodRepository extends JpaRepository<GbprodVO, Integer> {

}
