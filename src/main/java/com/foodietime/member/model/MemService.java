package com.foodietime.member.model;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    
    // 總會員數
    public long countAllMembers() {
        return memberRepository.count();
        // 或寫 Query: return memberRepository.countAllMembers();
    }

    // 狀態會員數
    public long countByStatus(MemberVO.MemberStatus status) {
        return memberRepository.countByMemStatus(status);
    }

    // 新增會員
    public long countByRegTimeBetween(Timestamp start, Timestamp end) {
        return memberRepository.countByMemTimeBetween(start, end);
    }
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("會員註冊驗證碼");
        message.setText("您好，您的驗證碼是：" + code + "\n請於 10 分鐘內完成驗證！");
        mailSender.send(message);
    }
    
    public MemberVO getByMemCode(String code) {
        return memberRepository.findByMemCode(code);
    }

    public void sendActivationEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("會員啟用信");
        message.setText("您好，請點擊以下連結啟用您的帳號：\n" + link + "\n\n如果不是您本人操作，請忽略此郵件。");
        mailSender.send(message);
    }




}
