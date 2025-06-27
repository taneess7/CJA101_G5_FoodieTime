package com.foodietime.act.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodietime.store.model.StoreVO;

public interface ActParticipationRepository extends JpaRepository<ActParticipationVO, Integer>{
     boolean existsByStoreAndAct(StoreVO store, ActVO act); //查詢是否存在這筆參加紀錄
     
     Optional<ActParticipationVO> findByStoreAndAct(StoreVO store, ActVO act);
     
     List<ActParticipationVO> findByStore(StoreVO store);//可查出所有參加活動
}
