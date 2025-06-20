package com.foodietime.groupbuyingcases.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesService;

@Controller
@RequestMapping("/gbcase")
public class GroupBuyingCasesController {

    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;
    
    // 查詢某會員參加的所有團購案
    @GetMapping("/member/{memId}")
    public String findByMemId(@PathVariable Integer memId, Model model) {
        
        List<GroupBuyingCasesVO> groupBuyingCases = groupBuyingCasesService.findByMemId(memId);

        if (groupBuyingCases.isEmpty()) {
            // 如果該會員沒有開設任何團購案，顯示錯誤訊息
            model.addAttribute("error", "找不到該會員開設的團購案");
        } else {
            // 如果找到了團購案，將資料傳遞到視圖
            model.addAttribute("groupBuyingCases", groupBuyingCases);
        }

        // 返回顯示團購案列表的視圖
        return "front/gb/gbleader/leader-groups";
    }
    
    // 查詢某會員開設且是團主的團購案
    @GetMapping("/leader/{memId}")
    public String findByLeader(@PathVariable Integer memId, @RequestParam(required = false) Boolean leader, Model model) {
       
        List<GroupBuyingCasesVO> groupBuyingCases = groupBuyingCasesService.findByMember_MemIdAndLeader(memId, leader);

        if (groupBuyingCases.isEmpty()) {
            model.addAttribute("error", "找不到該會員開設且為團主的團購案");
        } else {
            model.addAttribute("groupBuyingCases", groupBuyingCases);
        }
        return "front/gb/gbleader/leader-groups"; // 返回顯示團主所開設團購案的頁面
    }


//    @GetMapping("/store/{storId}")
//    public String findByStoreId(@PathVariable Integer storId, Model model) {
//        // 查詢該店家開設的所有團購案
//        List<GroupBuyingCasesVO> groupBuyingCases = groupBuyingCasesService.findByStoreId(storId);
//
//        if (groupBuyingCases.isEmpty()) {
//            // 如果該店家沒有開設任何團購案，顯示錯誤訊息
//            model.addAttribute("error", "找不到該店家開設的團購案");
//        } else {
//            // 如果找到了團購案，將資料傳遞到視圖
//            model.addAttribute("groupBuyingCases", groupBuyingCases);
//        }
//
//        // 返回顯示店家開設的團購案列表視圖
//        return "group-buying-cases/group-buying-cases-list";
//    }

    
    // 根據商品編號或商品名稱查詢團購案
    @GetMapping("/product/{gbProdId}")
    public String findByProdIdOrName(@PathVariable Integer gbProdId, @RequestParam(required = false) String gbProdName, Model model) {
        // 查詢該商品對應的所有團購案
        List<GroupBuyingCasesVO> groupBuyingCases = groupBuyingCasesService.findByProdIdOrName(gbProdId, gbProdName);

        if (groupBuyingCases.isEmpty()) {
            // 如果該商品沒有對應的團購案，顯示錯誤訊息
            model.addAttribute("error", "找不到該商品對應的團購案");
            return "front/gb/gbleader/leaderindex"; // 返回團購案列表頁面並顯示錯誤訊息
        }

        // 如果找到了團購案，將資料傳遞到視圖
        model.addAttribute("groupBuyingCases", groupBuyingCases);
        return "group-buying-cases/group-buying-cases-list"; // 顯示商品對應的團購案列表視圖
    }

    
    // 根據團購編號查詢團購案
    @GetMapping("/{gbId}")
    public String findById(@PathVariable Integer gbId, Model model) {
        Optional<GroupBuyingCasesVO> groupBuyingCaseOpt = groupBuyingCasesService.findById(gbId);
        if (groupBuyingCaseOpt.isPresent()) {
            model.addAttribute("groupBuyingCase", groupBuyingCaseOpt.get());
            return "group-buying-cases/group-buying-case-detail"; // 顯示單個團購案的詳情頁面
        } else {
            model.addAttribute("error", "團購案不存在");
            return "front/gb/gbleader/leaderindex"; // 如果團購案不存在，返回列表頁面並顯示錯誤訊息
        }
    }

    // 新增修改團購案
    @PostMapping("/save")
    public String saveGroupBuyingCase(@ModelAttribute GroupBuyingCasesVO groupBuyingCasesVO, Model model) {
        
        // 創建團購案
        GroupBuyingCasesVO savedGroupBuyingCase = groupBuyingCasesService.saveGroupBuyingCase(groupBuyingCasesVO);
        
        // 傳遞保存後的團購案到前端
        model.addAttribute("groupBuyingCase", savedGroupBuyingCase);
        return "redirect:/admin/group-buying-cases"; // 完成後跳轉至團購管理頁面
    }
    


    // 刪除某個團購案  (更改狀態為取消)
//    @GetMapping("/delete/{gbId}")
//    public String deleteGroupBuyingCase(@PathVariable Integer gbId, Model model) {
//        // 查找團購案是否存在
//        Optional<GroupBuyingCasesVO> groupBuyingCaseOpt = groupBuyingCasesService.findById(gbId);
//        
//        if (groupBuyingCaseOpt.isPresent()) {
//            // 如果團購案存在，刪除它
//            groupBuyingCasesService.deleteGroupBuyingCase(gbId);
//            model.addAttribute("message", "團購案已刪除");
//        } else {
//            // 如果團購案不存在，顯示錯誤消息
//            model.addAttribute("error", "找不到指定的團購案");
//        }
//
//        // 重定向到團購案列表頁面
//        return "redirect:/front/gb/gbleader/leader-groups";
//    }


    // 更新團購案狀態
    @PostMapping("/{gbId}/status")
    public String updateGroupBuyingCaseStatus(@PathVariable Integer gbId, @RequestParam Byte newStatus, Model model) {
        GroupBuyingCasesVO updatedGroupBuyingCase = groupBuyingCasesService.updateGroupBuyingCaseStatus(gbId, newStatus);
        if (updatedGroupBuyingCase != null) {
            model.addAttribute("groupBuyingCase", updatedGroupBuyingCase);
            return "front/gb/gbleader/leader-groups"; // 顯示更新後的團購案詳情頁面
        } else {
            model.addAttribute("error", "團購案更新失敗");
            return "front/gb/gbleader/leader-groups"; // 返回列表頁面並顯示錯誤訊息
        }
    }
}
