package com.foodietime.directmessage.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageRepository extends JpaRepository<DirectMessageVO, Integer>{
	 // 依會員查詢所有訊息（可排序）
    List<DirectMessageVO> findByMember_MemIdOrderByMessTimeAsc(Integer memId);

    // 依管理者查詢所有訊息（可排序）
    List<DirectMessageVO> findBySmgrId_SmgrIdOrderByMessTimeAsc(Integer smgr);
}
