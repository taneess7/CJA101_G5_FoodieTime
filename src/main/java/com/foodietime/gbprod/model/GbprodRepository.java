package com.foodietime.gbprod.model;

import com.foodietime.smg.model.SmgVO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GbprodRepository extends JpaRepository<GbprodVO, Integer> {

	
	@Query("""
		      SELECT p 
		      FROM GbprodVO p
		      JOIN p.groupOrdersList go
		      WHERE p.gbProdStatus = 1
		      """)
		List<GbprodVO> findStoresWithOrdersByStatus();

}
