package com.foodietime.act.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.store.model.StoreVO;

import io.lettuce.core.dynamic.annotation.Param;

public interface ActParticipationRepository extends JpaRepository<ActParticipationVO, Integer>{
     boolean existsByStoreAndAct(StoreVO store, ActVO act); //抓物件，查詢是否存在這筆參加紀錄 true; false;
     
     boolean existsByStore_StorIdAndAct_ActId(Integer storId, Integer actId);  //抓id，查詢是否存在參加紀錄
     
     Optional<ActParticipationVO> findByStoreAndAct(StoreVO store, ActVO act);
     
     List<ActParticipationVO> findByStore(StoreVO store);//可查出所有參加活動
     
     @Transactional
	 void deleteByStore_StorIdAndAct_ActId(Integer storId, Integer actId);
	
	 
	 List<ActParticipationVO> findByStore_StorId(Integer storId);
	 

}
