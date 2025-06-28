package com.foodietime.gbpromotion.model;

import com.foodietime.smg.model.SmgVO;
import com.foodietime.store.model.StoreVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GbpromotionRepository extends JpaRepository<GbpromotionVO, Integer> {

    List<GbpromotionVO> findByGbprod_Store(StoreVO store);

}
