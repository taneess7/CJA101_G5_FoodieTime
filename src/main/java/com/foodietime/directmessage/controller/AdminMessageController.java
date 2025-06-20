package com.foodietime.directmessage.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.directmessage.model.DirectMessageDTO;
import com.foodietime.directmessage.model.DirectMessageService;
import com.foodietime.directmessage.model.DirectMessageVO;

@Controller
@RequestMapping("/smg/admin-service-tickets")
public class AdminMessageController {

    @Autowired
    private DirectMessageService dmService;
    
    @GetMapping
    public String listTickets(Model model) {
        List<DirectMessageVO> messageList = dmService.getAllMessages();
        model.addAttribute("messages", messageList);
        
     // 🟥 統計數字準備
        int pendingCount = (int) messageList.stream()
                .filter(m -> m.getMessDirection().ordinal() == 0)
                .count();

        int completedCount = (int) messageList.stream()
                .filter(m -> m.getMessDirection().ordinal() == 1)
                .count();
        
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completedCount", completedCount);

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
    public DirectMessageDTO viewTicket(@PathVariable Integer dmId){
    	DirectMessageVO entity = dmService.getById(dmId);
    	
    	DirectMessageDTO dto = new DirectMessageDTO();
        dto.setDmId(entity.getDmId());
        dto.setMemId(entity.getMember().getMemId());
        dto.setMemNickname(entity.getMember().getMemNickname());
        dto.setMessContent(entity.getMessContent());
        dto.setMessTime(entity.getMessTime());
        dto.setMessDirection(entity.getMessDirection().ordinal());
        
        return dto;
    }
    
 // 提交回覆
    @PostMapping("/reply")
    @ResponseBody
    public Map<String, Object> replyTicket(@RequestBody Map<String, Object> payload) {
    	Integer dmId = Integer.valueOf(payload.get("dmId").toString());
        Integer status = Integer.valueOf(payload.get("status").toString());
        String replyContent = (String) payload.get("replyContent");

        // Update DB	
        dmService.reply(dmId, status, replyContent);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }


   
}

