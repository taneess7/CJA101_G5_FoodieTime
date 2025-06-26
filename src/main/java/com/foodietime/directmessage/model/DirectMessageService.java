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
	                int ordinal = vo.getMessDirection().ordinal();
	                System.out.println("💬 dmId=" + vo.getDmId() + ", messDirection=" + ordinal + " (" + vo.getMessDirection() + ")");

	                DirectMessageDTO dto = new DirectMessageDTO();
	                dto.setDmId(vo.getDmId());
	                dto.setMemId(vo.getMember().getMemId());
	                dto.setMemName(vo.getMember().getMemName());
	                dto.setMessContent(vo.getMessContent());
	                dto.setMessTime(vo.getMessTime());
	                dto.setMessDirection(ordinal); // 寫入 DTO
	                dto.setReplyContent("");
	                dto.setFormattedTime(vo.getMessTime().format(formatter));
	                return dto;
	            })

	            .collect(Collectors.toList());
	    }

	    public void replyAdd(DirectMessageVO replyMsg,DirectMessageVO replyTo) {
	    	replyMsg.setMessTime(LocalDateTime.now()); // 保證回覆在留言之後
	    	replyMsg.setReplyTo(replyTo); // ✅ 指定回覆對象
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
	        
	        // 所有回覆（用 replyTo 對應）
	        List<DirectMessageVO> replies = messageRepo.findAll().stream()
	                .filter(msg -> msg.getMessDirection() == DirectMessageVO.MessageDirection.ADMIN_TO_MEMBER)
	                .filter(msg -> msg.getReplyTo() != null)
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
	            
	            // 用 replyTo 找回覆
	            DirectMessageVO reply = replies.stream()
	                    .filter(r -> r.getReplyTo().getDmId().equals(vo.getDmId()))
	                    .findFirst()
	                    .orElse(null);
	            
	            if (reply != null) {
	                dto.setReplyContent(reply.getMessContent());
	                dto.setReplyAdminName(reply.getSmgr() != null ? reply.getSmgr().getSmgrAccount() : "未指派");
	                dto.setReplyStatus("已回覆");
	            } else {
	                dto.setReplyContent("");
	                dto.setReplyAdminName("");
	                dto.setReplyStatus("待處理");
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
	    
	    public DirectMessageVO getReplyByReplyToId(Integer replyToDmId) {
	        return messageRepo.findAll().stream()
	            .filter(reply -> reply.getReplyTo() != null)
	            .filter(reply -> reply.getReplyTo().getDmId().equals(replyToDmId))
	            .findFirst()
	            .orElse(null);
	    }




}
