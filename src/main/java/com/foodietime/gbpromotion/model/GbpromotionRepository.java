package com.foodietime.gbpromotion.model;

import com.foodietime.smg.model.SmgVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GbpromotionRepository extends JpaRepository<GbpromotionVO, Integer> {

}
