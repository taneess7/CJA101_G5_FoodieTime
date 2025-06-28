//package com.foodietime.message.controller;
//
//import com.foodietime.message.model.MessageService;
//import com.foodietime.message.model.MessageVO;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/admin/message")
//public class MessageMController {
//
//    @Autowired
//    private MessageService messageService;
//
//    /**
//     * 後台留言列表，可依貼文ID或關鍵字篩選
//     */
//    @GetMapping({"", "/"})
//    public String listAllMessages(
//            @RequestParam(value = "postId", required = false) Integer postId,
//            @RequestParam(value = "keyword", required = false) String keyword,
//            ModelMap model) {
//
//        // 這裡假設 MessageService 中有一個 findMessagesForAdmin 方法來處理篩選邏輯
//        // 如果沒有，可以先用 messageService.getAll()，再逐步加入篩選功能
//        List<MessageVO> messageList = messageService.findMessagesForAdmin(postId, keyword);
//
//        model.addAttribute("messageList", messageList);
//        model.addAttribute("currentPostId", postId);
//        model.addAttribute("currentKeyword", keyword);
//
//        return "back/message/messageList"; // 需要建立對應的 Thymeleaf 視圖
//    }
//
//    /**
//     * 顯示留言編輯表單
//     */
//    @GetMapping("/edit")
//    public String editMessageForm(@RequestParam("mesId") Integer mesId, ModelMap model, RedirectAttributes redirectAttributes) {
//        // 假設 MessageService 中有 getOneMessage 方法
//        MessageVO messageVO = messageService.getOneMessage(mesId);
//        if (messageVO == null) {
//            redirectAttributes.addFlashAttribute("errorMessage", "找不到指定的留言");
//            return "redirect:/admin/message/";
//        }
//        model.addAttribute("messageVO", messageVO);
//        return "back/message/messageEdit"; // 需要建立對應的 Thymeleaf 視圖
//    }
//
//    /**
//     * 更新留言內容 (用於審核)
//     */
//    @PostMapping("/update")
//    public String updateMessage(
//            @Valid @ModelAttribute("messageVO") MessageVO messageVO,
//            BindingResult result,
//            RedirectAttributes redirectAttributes) {
//
//        if (result.hasErrors()) {
//            // 如果驗證失敗，返回編輯頁面並顯示錯誤
//            // 注意：這裡直接返回視圖名稱，前端需要處理錯誤顯示
//            return "back/message/messageEdit";
//        }
//
//        MessageVO existingMessage = messageService.getOneMessage(messageVO.getMesId());
//        if (existingMessage == null) {
//            redirectAttributes.addFlashAttribute("errorMessage", "找不到要更新的留言");
//            return "redirect:/admin/message/";
//        }
//
//        // 管理員只更新留言內容
//        existingMessage.setMesContent(messageVO.getMesContent());
//
//        messageService.save(existingMessage); // 使用 save 方法更新
//
//        redirectAttributes.addFlashAttribute("success", "留言更新成功");
//        return "redirect:/admin/message/";
//    }
//
//    /**
//     * 刪除單一留言
//     */
//    @PostMapping("/delete")
//    public String deleteMessage(@RequestParam("mesId") Integer mesId, RedirectAttributes redirectAttributes) {
//        try {
//            messageService.deleteMessage(mesId); // 假設 MessageService 中有 deleteMessage 方法
//            redirectAttributes.addFlashAttribute("success", "留言刪除成功");
//        } catch (Exception e) {
//            // 可以記錄錯誤日誌
//            redirectAttributes.addFlashAttribute("errorMessage", "刪除失敗，請稍後再試");
//        }
//        return "redirect:/admin/message/";
//    }
//
//    /**
//     * 批次刪除留言
//     */
//    @PostMapping("/batch")
//    public String batchAction(
//            @RequestParam("action") String action,
//            @RequestParam("mesIds") List<Integer> mesIds,
//            RedirectAttributes redirectAttributes) {
//
//        if (mesIds == null || mesIds.isEmpty()) {
//            redirectAttributes.addFlashAttribute("errorMessage", "請選擇要操作的留言");
//            return "redirect:/admin/message/";
//        }
//
//        if ("delete".equals(action)) {
//            int affectedRows = messageService.batchDelete(mesIds); // 假設 MessageService 中有 batchDelete 方法
//            redirectAttributes.addFlashAttribute("success", "批次刪除成功，共影響 " + affectedRows + " 筆記錄");
//        } else {
//            redirectAttributes.addFlashAttribute("errorMessage", "無效的批量操作");
//        }
//
//        return "redirect:/admin/message/";
//    }
//}
//
