package com.foodietime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodietime.groupbuyingcases.model.GroupBuyingScheduler;

@RestController
@RequestMapping("/test")
public class TestNotificationController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private GroupBuyingScheduler scheduler;
    
    @GetMapping("/notification")
    public String testNotification(@RequestParam(defaultValue = "1") Integer memId) {
        // 統一用 /topic/member/{memId}
        messagingTemplate.convertAndSend("/topic/member/" + memId, "測試團購通知：您的團購已達標！");
        return "推播已發送給會員 ID: " + memId;
    }
    
    @GetMapping("/notification-success")
    public String testSuccessNotification(@RequestParam(defaultValue = "1") Integer memId) {
        messagingTemplate.convertAndSend("/topic/member/" + memId, "🎉 恭喜！您的團購已成功達標，請留意出貨通知！");
        return "成功通知已發送";
    }
    
    @GetMapping("/notification-refund")
    public String testRefundNotification(@RequestParam(defaultValue = "1") Integer memId) {
        messagingTemplate.convertAndSend("/topic/member/" + memId, "❌ 很抱歉，團購未達標，已自動退款至您的帳戶。");
        return "退款通知已發送";
    }
//    // 測試推播給商家
//    @GetMapping("/test/push/store/{storeId}")
//    public String testPushStore(@PathVariable Integer storeId, @RequestParam(defaultValue = "這是測試推播訊息！") String msg) {
//        messagingTemplate.convertAndSend("/topic/store/" + storeId, msg);
//        return "推播已送出 (storeId=" + storeId + ")";
//    }
//    // 測試推播給會員
//    @GetMapping("/test/push/member/{memberId}")
//    public String testPushMember(@PathVariable Integer memberId, @RequestParam(defaultValue = "這是會員測試推播！") String msg) {
//        messagingTemplate.convertAndSend("/topic/member/" + memberId, msg);
//        return "推播已送出 (memberId=" + memberId + ")";
//    }
    
    @GetMapping("/test/run-scheduler")
    public String runScheduler() {
        scheduler.checkGroupBuyingEnd();
        return "Scheduler executed";
    }
} 