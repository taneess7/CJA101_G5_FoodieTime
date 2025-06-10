package com.foodietime.member.model;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foodietime.member.model.MemberVO.Gender;
import com.foodietime.member.model.MemberVO.MemberStatus;
import com.foodietime.member.model.MemberVO.NoGroupStatus;
import com.foodietime.member.model.MemberVO.NoJoingroupStatus;
import com.foodietime.member.model.MemberVO.NoPostStatus;
import com.foodietime.member.model.MemberVO.NoSpeakStatus;

@Service
public class MemService {
	private MemDAO_interface dao;
	public MemService() {
		dao = new MemberDAO();
	}
	
	public MemberVO addMem(String memEmail,String memAccount,String memPassword,String memNickname,String memName,String memPhone,Gender memGender,String memCity,String memCityarea,String memAddress,byte[] memAvatar,Timestamp memTime) {
		MemberVO memVO = new MemberVO();
		
		memVO.setMemEmail(memEmail);
		memVO.setMemAccount(memAccount);
		memVO.setMemPassword(memPassword);
		memVO.setMemNickname(memNickname);
		memVO.setMemName(memName);
		memVO.setMemPhone(memPhone);
		memVO.setMemGender(memGender);
		memVO.setMemCity(memCity);
		memVO.setMemCityarea(memCityarea);
		memVO.setMemAddress(memAddress);
		memVO.setMemAvatar(memAvatar);
		memVO.setMemTime(memTime);
//		memVO.setMemCode(memCode);
//		memVO.setTotalStarNum(totalStarNum);
//		memVO.setTotalReviews(totalReviews);
		dao.insert(memVO);
		
	
		return memVO;
	}
	public MemberVO updateMember(String memEmail,String memPassword,String memNickname,String memName,String memPhone,Gender memGender,String memCity,String memCityarea,String memAddress,byte[] memAvatar) {
		MemberVO memVO = new MemberVO();
		
		memVO.setMemEmail(memEmail);
		memVO.setMemPassword(memPassword);
		memVO.setMemNickname(memNickname);
		memVO.setMemName(memName);
		memVO.setMemPhone(memPhone);
		memVO.setMemGender(memGender);
		memVO.setMemCity(memCity);
		memVO.setMemCityarea(memCityarea);
		memVO.setMemAddress(memAddress);
		memVO.setMemAvatar(memAvatar);
//		memVO.setMemTime(memTime);
//		memVO.setTotalStarNum(totalStarNum);
//		memVO.setTotalReviews(totalReviews);
		dao.update(memVO);
		
		
		return memVO;
	}
	public MemberVO updateMemberPermission(MemberStatus memStatus,NoSpeakStatus memNoSpeak,NoPostStatus memNoPost,NoGroupStatus memNoGroup,NoJoingroupStatus memNoJoinGroup) {
		MemberVO memVO = new MemberVO();
		
		memVO.setMemStatus(memStatus);
		memVO.setMemNoSpeak(memNoSpeak);
		memVO.setMemNoPost(memNoPost);
		memVO.setMemNoGroup(memNoGroup);
		memVO.setMemNoJoingroup(memNoJoinGroup);
		dao.updatePermission(memVO);
		
		
		
		return memVO;
	}
	
	public MemberVO getOneMember(Integer memId) {
		return dao.findByPrimaryKey(memId);
	}
	public boolean isAccountExists(String memAccount) {
        return dao.isAccountExist(memAccount);
	}
	public List<MemberVO> getAll(){
		return dao.getAll();
	}
	
	public MemberVO findByAccount(String memAccount) {
	    return dao.findByAccount(memAccount);
	}
	
	

}
