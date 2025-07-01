package com.foodietime.act.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.store.model.StoreVO;

@Service
public class ActParticipationService {

	@Autowired
	private ActParticipationRepository partRepo;
	
	//新增關聯紀錄
	public void joinAct(StoreVO store, ActVO act) {
		if (!partRepo.existsByStoreAndAct(store, act)) {
		ActParticipationVO participation = new ActParticipationVO();
		participation.setStore(store);
		participation.setAct(act);
		participation.setJoinedTime(Timestamp.from(Instant.now()));
		partRepo.save(participation);
	}}
	
	//查詢是否有店家參加活動 true/false
	public boolean hasStoreJoinedAct(StoreVO store, ActVO act) {
		return partRepo.existsByStoreAndAct(store, act);
	}

	public void deleteByStoreAndAct(Integer storId, Integer actId) {
		partRepo.deleteByStore_StorIdAndAct_ActId(storId, actId);
		
	}

	public void save(ActParticipationVO participation) {
		partRepo.save(participation);
		
	}

	public boolean hasJoined(Integer storId, Integer actId) {
		return partRepo.existsByStore_StorIdAndAct_ActId(storId, actId);//檢查是否存在
	}
	
	public List<ActParticipationVO> findByStorId(Integer storId) {
	    return partRepo.findByStore_StorId(storId);

	}


	
	
	

}
