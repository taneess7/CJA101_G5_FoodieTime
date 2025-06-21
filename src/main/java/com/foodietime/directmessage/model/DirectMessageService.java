package com.foodietime.directmessage.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.member.model.MemberVO;
import com.foodietime.smg.model.SmgVO;
import com.foodietime.directmessage.model.DirectMessageVO;

@Service
public class DirectMessageService {
	 @Autowired
	    private DirectMessageRepository messageRepo;

	    // 新增訊息
	    public DirectMessageVO addMessage(DirectMessageVO message) {
	        message.setMessTime(LocalDateTime.now());
	        return messageRepo.save(message);
	    }
	    
	    // 查詢全部訊息（工單列表用）
	    public List<DirectMessageVO> getAllMessages() {
	        return messageRepo.findAllByOrderByMessTimeDesc();
	    }


	    // 查詢會員的訊息
	    public List<DirectMessageVO> getMessagesByMemberId(Integer memId) {
	        return messageRepo.findByMember_MemIdOrderByMessTimeAsc(memId);
	    }

	    // 查詢管理員的訊息
	    public List<DirectMessageVO> getMessagesBySmgrId(SmgVO smgr) {
	        return messageRepo.findBySmgrOrderByMessTimeAsc(smgr);
	    }

	    // 刪除訊息
	    public void deleteMessage(Integer dmId) {
	        messageRepo.deleteById(dmId);
	    }

	 // 取得單筆訊息 by dmId
	    public DirectMessageVO getById(Integer dmId) {
	        return messageRepo.findById(dmId).orElse(null);
	    }
	    
	 // 批量發送用
	    public void addMessage(MemberVO member, SmgVO smgr, String content) {
	        DirectMessageVO message = new DirectMessageVO();
	        message.setMember(member);
	        message.setSmgr(smgr);
	        message.setMessContent(content);
	        message.setMessTime(LocalDateTime.now());
	        message.setMessDirection(DirectMessageVO.MessageDirection.ADMIN_TO_MEMBER);

	        messageRepo.save(message);
	    }
	    
	    
	    
	    public List<DirectMessageDTO> getMessagesDtoByMemberId(Integer memId) {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	        return messageRepo.findByMember_MemIdOrderByMessTimeAsc(memId)
	            .stream()
	            .map(vo -> {
	                DirectMessageDTO dto = new DirectMessageDTO();
	                dto.setDmId(vo.getDmId());
	                dto.setMemId(vo.getMember().getMemId());
	                dto.setMemName(vo.getMember().getMemName());
	                dto.setMessContent(vo.getMessContent());
	                dto.setMessTime(vo.getMessTime());
	                dto.setMessDirection(vo.getMessDirection().ordinal());
	                dto.setReplyContent("");  // 你看要不要給 reply
	                dto.setFormattedTime(vo.getMessTime().format(formatter));  // 這一行
	                return dto;
	            })
	            .collect(Collectors.toList());
	    }

	    public void replyAdd(DirectMessageVO replyMsg) {
	        messageRepo.save(replyMsg); // 新增
	    }
	    
	    public List<DirectMessageVO> getAllMemberMessages() {
	        return messageRepo.findAll().stream()
	            .filter(msg -> msg.getMessDirection() == DirectMessageVO.MessageDirection.MEMBER_TO_ADMIN)
	            .toList();
	    }
	    
	    public List<DirectMessageDTO> getAllMemberMessagesWithLatestReply() {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	        List<DirectMessageVO> memberMessages = messageRepo.findAll().stream()
	                .filter(msg -> msg.getMessDirection() == DirectMessageVO.MessageDirection.MEMBER_TO_ADMIN)
	                .toList();

	        List<DirectMessageVO> allReplies = messageRepo.findAll().stream()
	                .filter(msg -> msg.getMessDirection() == DirectMessageVO.MessageDirection.ADMIN_TO_MEMBER)
	                .toList();

	        return memberMessages.stream().map(vo -> {
	            DirectMessageDTO dto = new DirectMessageDTO();
	            dto.setDmId(vo.getDmId());
	            dto.setMemId(vo.getMember().getMemId());
	            dto.setMemName(vo.getMember().getMemName());
	            dto.setMessContent(vo.getMessContent());
	            dto.setMessTime(vo.getMessTime());
	            dto.setFormattedTime(vo.getMessTime().format(formatter));
	            dto.setMessDirection(vo.getMessDirection().ordinal());

	            // 查最新回覆時間
	            allReplies.stream()
	                .filter(reply -> reply.getMember().getMemId().equals(vo.getMember().getMemId()))
	                .filter(reply -> reply.getMessTime().isAfter(vo.getMessTime()))
	                .max((r1, r2) -> r1.getMessTime().compareTo(r2.getMessTime()))
	                .ifPresent(latestReply -> {
	                    dto.setReplyContent(latestReply.getMessContent());
	                    dto.setReplyAdminName(latestReply.getSmgr() != null ? latestReply.getSmgr().getSmgrAccount() : "未指派");
	                    dto.setReplyStatus("已回覆");
	                });

	            // 如果沒有回覆
	            if (dto.getReplyStatus() == null) {
	                dto.setReplyStatus("待處理");
	                dto.setReplyContent("");
	                dto.setReplyAdminName("");
	            }

	            return dto;
	        }).toList();
	    }
	    
	 // 撈該會員的所有管理員回覆 (ADMIN_TO_MEMBER)
	    public List<DirectMessageVO> getAllAdminRepliesForMember(Integer memId) {
	        return messageRepo.findAll().stream()
	                .filter(msg -> msg.getMember().getMemId().equals(memId))
	                .filter(msg -> msg.getMessDirection() == DirectMessageVO.MessageDirection.ADMIN_TO_MEMBER)
	                .toList();
	    }
	    
	 // 查這一筆留言之後的第一筆回覆
	    public DirectMessageVO findFirstReplyAfterMessage(DirectMessageVO message) {
	        return messageRepo.findAll().stream()
	                .filter(reply -> reply.getMember().getMemId().equals(message.getMember().getMemId()))
	                .filter(reply -> reply.getMessDirection() == DirectMessageVO.MessageDirection.ADMIN_TO_MEMBER)
	                .filter(reply -> reply.getMessTime().isAfter(message.getMessTime()))
	                .min((r1, r2) -> r1.getMessTime().compareTo(r2.getMessTime()))
	                .orElse(null);
	    }



}
