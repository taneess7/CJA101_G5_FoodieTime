package com.foodietime.directmessage.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodietime.member.model.MemberVO;
import com.foodietime.smg.model.SmgVO;

public interface DirectMessageRepository extends JpaRepository<DirectMessageVO, Integer>{
	 // 依會員查詢所有訊息（可排序）
    List<DirectMessageVO> findByMember_MemIdOrderByMessTimeAsc(MemberVO member);

    // 依管理者查詢所有訊息（可排序）
    List<DirectMessageVO> findBySmgrId_SmgrIdOrderByMessTimeAsc(SmgVO smgr);
}
