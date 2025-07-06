package com.foodietime.participants.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ParticipantsRepository extends JpaRepository<ParticipantsVO, Integer>{

	 // 根據「某個團購案的 gbId」去找所有參與者
	 List<ParticipantsVO> findByGroupBuyingCase_GbId(Integer gbId);
	
	 //找出這個團購 (gbId) 中 leader = 0 的參加者  
	 Optional<ParticipantsVO> findByGroupBuyingCase_GbIdAndLeader(Integer gbId, Byte leader);

	 // 查詢某會員(團長)的所有地址
     List<ParticipantsVO> findByMember_MemIdAndLeader(Integer memId, Byte leader);
     
     // 查詢某會員的所有參與者記錄
     List<ParticipantsVO> findByMember_MemId(Integer memId);
}
