package com.foodietime.act.model;

import java.sql.Timestamp;
import java.time.Instant;

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
	
}
