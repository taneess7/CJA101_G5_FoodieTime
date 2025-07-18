package com.foodietime.member.model;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface MemberRepository extends JpaRepository<MemberVO, Integer>{
	  // 根據帳號查會員（登入或驗證用）
    MemberVO findByMemAccount(String memAccount);

    // 檢查 Email 是否存在
    boolean existsByMemEmail(String memEmail);

    // 檢查帳號是否存在
    boolean existsByMemAccount(String memAccount);
    
    // 狀態會員數
    long countByMemStatus(MemberVO.MemberStatus status);
    
    // 新增會員（註冊日期區間）
    long countByMemTimeBetween(Timestamp start, Timestamp end);
    
    MemberVO findByMemCode(String memCode);
    
    MemberVO findByMemEmail(String memEmail);

}
