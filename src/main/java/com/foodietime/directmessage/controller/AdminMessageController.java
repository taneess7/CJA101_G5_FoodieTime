package com.foodietime.directmessage.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.directmessage.model.DirectMessageDTO;
import com.foodietime.directmessage.model.DirectMessageService;
import com.foodietime.directmessage.model.DirectMessageVO;
import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/smg/admin-service-tickets")
public class AdminMessageController {

    @Autowired
    private DirectMessageService dmService;
    
    @Autowired
    private MemService memService;
    
    @GetMapping
    public String listTickets(@RequestParam(required = false, defaultValue = "all") String type,@RequestParam(required = false) String keyword,@RequestParam(required = false) String dateRange
,Model model) {
        List<DirectMessageDTO> allMessages = dmService.getAllMemberMessagesWithLatestReply();
        
        // keyword 過濾
        if (keyword != null && !keyword.isBlank()) {
            allMessages = allMessages.stream()
                .filter(m -> m.getMemName().contains(keyword)
                          || String.valueOf(m.getDmId()).contains(keyword))
                .toList();
        }
        
        // 日期篩選
        if (dateRange != null && !dateRange.isBlank()) {
            allMessages = allMessages.stream()
                .filter(m -> m.getMessTime().toLocalDate().toString().equals(dateRange))
                .toList();
        }
        
     // 預設
        List<DirectMessageDTO> messageList = allMessages;

        // 條件篩選
        if ("pending".equals(type)) {
            messageList = allMessages.stream()
                .filter(m -> "待處理".equals(m.getReplyStatus()))
                .toList();
        } else if ("completed".equals(type)) {
            messageList = allMessages.stream()
                .filter(m -> "已回覆".equals(m.getReplyStatus()))
                .toList();
        }
        
        // 統計數
        long pendingCount = allMessages.stream()
            .filter(m -> "待處理".equals(m.getReplyStatus()))
            .count();

        long completedCount = allMessages.stream()
            .filter(m -> "已回覆".equals(m.getReplyStatus()))
            .count();
	
	
	    model.addAttribute("messages", messageList);
	    model.addAttribute("pendingCount", pendingCount);
	    model.addAttribute("completedCount", completedCount);
	    model.addAttribute("type", type);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("dateRange", dateRange);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<String> formattedTimes = messageList.stream()
                .map(msg -> msg.getMessTime().format(formatter))
                .toList();

        model.addAttribute("formattedTimes", formattedTimes);

        return "admin/smg/admin-service-tickets";
    }
    
    // 查詢單筆工單 (view)
    @GetMapping("/view/{dmId}")
    @ResponseBody
    public DirectMessageDTO viewTicket(@PathVariable Integer dmId) {
        DirectMessageVO entity = dmService.getById(dmId);
        MemberVO member = memService.getById(entity.getMember().getMemId());

        DirectMessageDTO dto = new DirectMessageDTO();
        dto.setDmId(entity.getDmId());
        dto.setMemId(entity.getMember().getMemId());
        dto.setMemName(member != null ? member.getMemName() : "未知會員");
        dto.setMessContent(entity.getMessContent());
        dto.setMessTime(entity.getMessTime());
        dto.setMessDirection(entity.getMessDirection().ordinal());

        // 不用再判斷 hasReplyForMember（因為用最新 service 方法來顯示即可）
        
        // ⭐ 查最新回覆內容
        DirectMessageVO latestReply = dmService.getReplyByReplyToId(entity.getDmId());


        if (latestReply != null) {
            dto.setReplyContent(latestReply.getMessContent());
            dto.setReplyAdminName(latestReply.getSmgr() != null ? latestReply.getSmgr().getSmgrAccount() : "未指派");
            dto.setReplyStatus("已回覆");
        } else {
            dto.setReplyContent("");
            dto.setReplyAdminName("");
            dto.setReplyStatus("待處理");
        }


        return dto;
    }
    
 // 提交回覆
    @PostMapping("/reply")
    @ResponseBody
    public Map<String, Object> replyTicket(HttpSession session,@RequestBody Map<String, Object> payload) {
    	Integer dmId = Integer.valueOf(payload.get("dmId").toString());
        String replyContent = (String) payload.get("replyContent");
        
        // 先查原本的會員訊息 → 拿 member
        DirectMessageVO originalMsg = dmService.getById(dmId);
        MemberVO member = originalMsg.getMember();
        
        SmgVO loggedInAdmin = (SmgVO) session.getAttribute("loggedInSmg");
        
        // 新增一筆 → 管理員回覆訊息
        DirectMessageVO replyMsg = new DirectMessageVO();
        replyMsg.setMessContent(replyContent);
        replyMsg.setMessDirection(DirectMessageVO.MessageDirection.ADMIN_TO_MEMBER);
        replyMsg.setMessTime(LocalDateTime.now());
        replyMsg.setMember(member);
        replyMsg.setSmgr(loggedInAdmin); // 塞管理員

        // Update DB	
        dmService.replyAdd(replyMsg, originalMsg); // ✅ 傳入被回覆的訊息


        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }


   
}

