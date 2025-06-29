package com.foodietime.gbprod.model;

import com.foodietime.smg.model.SmgVO;
import com.foodietime.store.model.StoreVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GbprodRepository extends JpaRepository<GbprodVO, Integer> {


	
	@Query("""
		      SELECT p 
		      FROM GbprodVO p
		      WHERE p.gbProdStatus = 1
		      """)
		List<GbprodVO> findStoresWithOrdersByStatus();

	@Query("""
	        SELECT p FROM GbprodVO p
	        WHERE LOWER(p.gbProdName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	           OR CAST(p.gbProdId AS string) = :keyword
	           OR CAST(p.store.storId AS string) = :keyword
	    """)
	    List<GbprodVO> searchByNameOrProdIdOrStoreId(@Param("keyword") String keyword);
	
	List<GbprodVO> findByStore(StoreVO store);
}

