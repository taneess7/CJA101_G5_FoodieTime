package com.foodietime.participants.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ParticipantsRepository extends JpaRepository<ParticipantsVO, Integer>{

	 // 根據「某個團購案的 gbId」去找所有參與者
	 List<ParticipantsVO> findByGroupBuyingCase_GbId(Integer gbId);
}
