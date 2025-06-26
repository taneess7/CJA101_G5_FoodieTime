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

    // 來自遠端的查詢方法
    @Query("""
          SELECT p 
          FROM GbprodVO p
          JOIN p.groupOrdersList go
          WHERE p.gbProdStatus = 1
          """)
    List<GbprodVO> findStoresWithOrdersByStatus();

    // 本地新增的查詢方法
    List<GbprodVO> findByStore(StoreVO store);
}
