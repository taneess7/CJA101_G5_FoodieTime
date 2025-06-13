//package com.foodietime.directmessage.controller;
//
//import java.time.LocalDateTime;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.foodietime.directmessage.model.DirectMessageService;
//import com.foodietime.directmessage.model.DirectMessageVO;
//import com.foodietime.directmessage.model.DirectMessageVO.MessageDirection;
//import com.foodietime.smg.model.SmgVO;
//
//import jakarta.servlet.http.HttpSession;
//
//@Controller
//@RequestMapping("/directmesssage")
//public class AdminMessageController {
//
//    @Autowired
//    private DirectMessageService messageService;
//
//    // 顯示回覆表單
//    @GetMapping("/reply")
//    public String showReplyForm(@RequestParam("dmId") Integer dmId, Model model) {
//        DirectMessageVO original = messageService.getById(dmId);
//        model.addAttribute("message", original);
//        return "/directmessage/reply";
//    }
//
//    // 接收回覆送出
//    @PostMapping("/reply")
//    public String replyMessage(@RequestParam("dmId") Integer dmId,
//                                @RequestParam("replyContent") String replyContent,
//                                HttpSession session) {
//        // 假設後台登入後 session 中有管理員
//        SmgVO smgr = (SmgVO) session.getAttribute("loggedInSmgr");
//        if (smgr == null) {
//            return "redirect:/";//回到管理員登入頁面
//        }
//
//        DirectMessageVO reply = new DirectMessageVO();
//        DirectMessageVO original = messageService.getById(dmId);
//
//        reply.setMember(original.getMember());       // 回覆給原本的會員
//        reply.setSmgr(smgr);                      // 設定是哪個管理員回覆
//        reply.setMessContent(replyContent);
//        reply.setMessTime(LocalDateTime.now());
//        reply.setMessDirection(MessageDirection.ADMIN_TO_MEMBER);                  // 1 = 管理員回覆
//
//        messageService.addMessage(reply);
//
//        return "redirect:/directmessage/list"; // 回到後台留言列表（你可自定）
//    }
//}
//
