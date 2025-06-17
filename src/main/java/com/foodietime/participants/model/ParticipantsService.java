package com.foodietime.participants.model;

import com.foodietime.participants.model.ParticipantsVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.participants.model.ParticipantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ParticipantsService {
	
	@Autowired
	private ParticipantsRepository participantsRepository;
	
	// 查詢所有會員
    public List<ParticipantsVO> getAll() {
        return participantsRepository.findAll();
    }
    
	// 查詢某個團購案的所有參與者
	public List<ParticipantsVO> getParticipantsByGroupBuyingCaseId(Integer gbId) {
		return participantsRepository.findByGroupBuyingCase_GbId(gbId);
	}
	
	// 根據參與者 ID 查找單個參與者
	public ParticipantsVO getParticipantById(Integer parId) {
		return participantsRepository.findById(parId).orElse(null);
	}
	
	// 新增或修改參與者
	public ParticipantsVO save(ParticipantsVO participant) {
	    if (participant.getLeader() == null) {
	        participant.setLeader((byte) 1);  // 設置預設為非團長（1）
	    }
	    if (participant.getParPurchaseQuantity() == null) {
	        participant.setParPurchaseQuantity(1);  // 設置預設為 1
	    }
	    if (participant.getPaymentStatus() == null) {
	        participant.setPaymentStatus((byte) 0);  // 設置預設為未付款（0）
	    }

	    return participantsRepository.save(participant);
	}


}

	

