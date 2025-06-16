package com.foodietime.member.model;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemService {
	@Autowired
    private MemberRepository memberRepository;

    // 新增或修改會員
    public MemberVO save(MemberVO member) {
        if (member.getMemCode() == null) {
            member.setMemCode(generateRandomCode(8));
        }
        if (member.getMemStatus() == null) {
            member.setMemStatus(MemberVO.MemberStatus.ACTIVE);
        }
        if (member.getMemNoSpeak() == null) {
            member.setMemNoSpeak(MemberVO.NoSpeakStatus.INACTIVE);
        }
        if (member.getMemNoPost() == null) {
            member.setMemNoPost(MemberVO.NoPostStatus.INACTIVE);
        }
        if (member.getMemNoGroup() == null) {
            member.setMemNoGroup(MemberVO.NoGroupStatus.INACTIVE);
        }
        if (member.getMemNoJoingroup() == null) {
            member.setMemNoJoingroup(MemberVO.NoJoingroupStatus.INACTIVE);
        }
        if (member.getTotalStarNum() == null) {
            member.setTotalStarNum(0);
        }
        if (member.getTotalReviews() == null) {
            member.setTotalReviews(0);
        }

        return memberRepository.save(member);
    }

    // 查詢所有會員
    public List<MemberVO> getAll() {
        return memberRepository.findAll();
    }

    // 根據 ID 查詢
    public MemberVO getById(Integer memId) {
        return memberRepository.findById(memId).orElse(null);
    }

    // 根據帳號查詢
    public MemberVO getByAccount(String memAccount) {
        return memberRepository.findByMemAccount(memAccount);
    }

    // 檢查帳號是否存在
    public boolean isAccountExist(String memAccount) {
        return memberRepository.existsByMemAccount(memAccount);
    }

    // 刪除會員
    public void delete(Integer memId) {
        memberRepository.deleteById(memId);
    }

    // 更新頭像
    public void updateAvatar(Integer memId, byte[] avatar) {
        Optional<MemberVO> optional = memberRepository.findById(memId);
        if (optional.isPresent()) {
            MemberVO member = optional.get();
            member.setMemAvatar(avatar);
            memberRepository.save(member); // 自動 merge
        }
    }
    public MemberVO login(String memAccount, String memPassword) {
        MemberVO member = memberRepository.findByMemAccount(memAccount);
        if (member != null && member.getMemPassword().equals(memPassword)) {
            return member;
        }
        return null;
    }

    public boolean isAccountExists(String memAccount) {
        return memberRepository.existsByMemAccount(memAccount);
    }

    public boolean isEmailExists(String memEmail) {
        return memberRepository.existsByMemEmail(memEmail);
    }
    
    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
