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
	
	// 查詢所有參與者
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

	// 查詢團長所有地址
    public List<ParticipantsVO> getLeaderAddresses(Integer memId) {
        return participantsRepository.findByMember_MemIdAndLeader(memId, (byte)0);
    }

    // 新增地址
    public ParticipantsVO addAddress(ParticipantsVO address, Integer memId) {
        // 設置為團長地址（leader = 0）
        address.setLeader((byte) 0);
        // 設置會員ID
        MemberVO member = new MemberVO();
        member.setMemId(memId);
        address.setMember(member);
        // 設置預設值
        if (address.getParPurchaseQuantity() == null) {
            address.setParPurchaseQuantity(1);
        }
        if (address.getPaymentStatus() == null) {
            address.setPaymentStatus((byte) 0);
        }
        // 預設經緯度為 0
        if (address.getParLongitude() == null) {
            address.setParLongitude(java.math.BigDecimal.ZERO);
        }
        if (address.getParLatitude() == null) {
            address.setParLatitude(java.math.BigDecimal.ZERO);
        }

        return participantsRepository.save(address);
    }

    
    // 刪除
    public void deleteById(Integer parId) {
        participantsRepository.deleteById(parId);
    }
    
    // 查詢某會員的所有參與者記錄
    public List<ParticipantsVO> getParticipantsByMemberId(Integer memId) {
        return participantsRepository.findByMember_MemId(memId);
    }
}

	

