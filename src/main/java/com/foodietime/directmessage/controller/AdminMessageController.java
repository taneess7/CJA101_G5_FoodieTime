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
    public String listTickets(@RequestParam(required = false, defaultValue = "pending") String type,Model model) {
        List<DirectMessageDTO> allMessages = dmService.getAllMemberMessagesWithLatestReply();
        
    	// 判斷 type：pending → 撈待處理，completed → 撈已處理
        List<DirectMessageDTO> messageList;

        switch (type) {
        case "pending":
            messageList = allMessages.stream()
                .filter(m -> "待處理".equals(m.getReplyStatus()))
                .toList();
            break;
        case "completed":
            messageList = allMessages.stream()
                .filter(m -> "已回覆".equals(m.getReplyStatus()))
                .toList();
            break;
        case "all":
        default:
            messageList = allMessages;
            break;
        }

        model.addAttribute("messages", messageList);
        model.addAttribute("pendingCount", allMessages.stream().filter(m -> "待處理".equals(m.getReplyStatus())).count());
        model.addAttribute("type", type); // 回傳 type，讓前端可以做 active 按鈕
        model.addAttribute("completedCount", allMessages.stream().filter(m -> "已回覆".equals(m.getReplyStatus())).count());

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
        DirectMessageVO latestReply = dmService.findFirstReplyAfterMessage(entity);

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
        dmService.replyAdd(replyMsg);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }


   
}

