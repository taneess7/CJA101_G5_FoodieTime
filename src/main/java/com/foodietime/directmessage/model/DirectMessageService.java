package com.foodietime.directmessage.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DirectMessageService {
	 @Autowired
	    private DirectMessageRepository messageRepo;

	    // 新增訊息
	    public DirectMessageVO addMessage(DirectMessageVO message) {
	        message.setMessTime(LocalDateTime.now());
	        return messageRepo.save(message);
	    }

	    // 查詢會員的訊息
	    public List<DirectMessageVO> getMessagesByMemberId(Integer memId) {
	        return messageRepo.findByMember_MemIdOrderByMessTimeAsc(memId);
	    }

	    // 查詢管理員的訊息
	    public List<DirectMessageVO> getMessagesBySmgrId(Integer smgr) {
	        return messageRepo.findBySmgrId_SmgrIdOrderByMessTimeAsc(smgr);
	    }

	    // 刪除訊息
	    public void deleteMessage(Integer dmId) {
	        messageRepo.deleteById(dmId);
	    }
	    
	 // 取得單筆訊息 by dmId
	    public DirectMessageVO getById(Integer dmId) {
	        return messageRepo.findById(dmId).orElse(null);
	    }
}
