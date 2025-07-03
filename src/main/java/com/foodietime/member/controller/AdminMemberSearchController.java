package com.foodietime.member.controller;

import com.foodietime.member.model.MemberVO;
import com.foodietime.member.model.MemberDTO;
import com.foodietime.directmessage.model.BatchMessageRequestDTO;
import com.foodietime.directmessage.model.DirectMessageService;
import com.foodietime.member.model.MemService;
import com.foodietime.orders.model.OrdersVO;
import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/smg/admin-members-search")
public class AdminMemberSearchController {

    @Autowired
    private MemService memService;
    
    @Autowired
    private DirectMessageService dmService;

    @GetMapping
    public String searchMembers(
    		@RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "regDateFrom", required = false) String regDateFrom,
            @RequestParam(value = "regDateTo", required = false) String regDateTo,
            @RequestParam(value = "orderCountMin", required = false) Integer orderCountMin,
            @RequestParam(value = "totalSpentMin", required = false) Integer totalSpentMin,
            Model model) {
    	
    	
        List<MemberVO> allMembers = memService.getAll();
    	
    	
    	  // ✅ 總會員數
        int totalMembers = memService.getAll().size();
        model.addAttribute("totalMembers", totalMembers);
        
        // ✅ 活躍會員數
        long activeMembers = memService.getAll().stream()
                .filter(m -> m.getMemStatus() == MemberVO.MemberStatus.ACTIVE)
                .count();
        model.addAttribute("activeMembers", activeMembers);
        
        // ✅ 新增會員（7 天內）
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);
        long newMembers = memService.getAll().stream()
                .filter(m -> {
                    LocalDate regDate = m.getMemTime().toLocalDateTime().toLocalDate();
                    return (regDate.isEqual(sevenDaysAgo) || regDate.isAfter(sevenDaysAgo));
                })
                .count();
        model.addAttribute("newMembers", newMembers);
        
        // ✅ 異常會員（假設 memStatus == INACTIVE）
        long abnormalMembers = memService.getAll().stream()
                .filter(m -> m.getMemStatus() == MemberVO.MemberStatus.INACTIVE)
                .count();
        model.addAttribute("abnormalMembers", abnormalMembers);

        List<MemberVO> members = memService.getAll();

        // 先做篩選
        members = members.stream()
                .filter(member -> {
                    boolean match = true;

                    // 關鍵字
                    if (keyword != null && !keyword.isBlank()) {
                        match = match && (
                                member.getMemName().contains(keyword) ||
                                member.getMemEmail().contains(keyword)
                        );
                    }

                    // 狀態
                    if (status != null && !status.isBlank()) {
                        int targetStatus = switch (status) {
                            case "active" -> 1;
                            case "inactive" -> 0;
                            case "disabled" -> 2;
                            default -> -1;
                        };
                        match = match && (member.getMemStatus().ordinal() == targetStatus);
                    }

                    // 性別
                    if (gender != null && !gender.isBlank()) {
                        MemberVO.Gender g = member.getMemGender();
                        match = match && (
                                (gender.equals("male") && g == MemberVO.Gender.MALE) ||
                                (gender.equals("female") && g == MemberVO.Gender.FEMALE) ||
                                (gender.equals("other") && g == MemberVO.Gender.NOT_REVEAL)
                        );
                    }

                    // 註冊日期 from
                    if (regDateFrom != null && !regDateFrom.isBlank()) {
                        LocalDate fromDate = LocalDate.parse(regDateFrom);
                        LocalDate regDate = member.getMemTime().toLocalDateTime().toLocalDate();
                        match = match && (regDate.isEqual(fromDate) || regDate.isAfter(fromDate));
                    }

                    // 註冊日期 to
                    if (regDateTo != null && !regDateTo.isBlank()) {
                        LocalDate toDate = LocalDate.parse(regDateTo);
                        LocalDate regDate = member.getMemTime().toLocalDateTime().toLocalDate();
                        match = match && (regDate.isEqual(toDate) || regDate.isBefore(toDate));
                    }

                    // 訂單數量
                    if (orderCountMin != null) {
                        int orderCount = member.getOrders() != null ? member.getOrders().size() : 0;
                        match = match && (orderCount >= orderCountMin);
                    }

                    // 總消費金額
                    if (totalSpentMin != null) {
                        int totalSpent = 0;
                        if (member.getOrders() != null) {
                            totalSpent = member.getOrders().stream()
                                    .filter(o -> o.getActualPayment() != null)
                                    .mapToInt(OrdersVO::getActualPayment)
                                    .sum();
                        }
                        match = match && (totalSpent >= totalSpentMin);
                    }

                    return match;
                })
                .collect(Collectors.toList());

        // 轉 DTO
        List<MemberDTO> dtoList = members.stream().map(member -> {
            MemberDTO dto = new MemberDTO();
            dto.setMemId(member.getMemId());
            dto.setMemName(member.getMemName());
            dto.setMemEmail(member.getMemEmail());
            dto.setMemPhone(member.getMemPhone());
            dto.setMemStatus(member.getMemStatus().ordinal());
            dto.setMemGender(member.getMemGender().ordinal());

            if (member.getMemTime() != null) {
                dto.setMemTime(member.getMemTime().toLocalDateTime().toLocalDate().toString());
            }
            
            if (member.getMemAvatar() != null) {
                String base64 = java.util.Base64.getEncoder().encodeToString(member.getMemAvatar());
                dto.setAvatarBase64(base64);
            }

            // 訂單數
            int orderCount = member.getOrders() != null ? member.getOrders().size() : 0;
            dto.setOrderCount(orderCount);

            // 消費金額
            int totalSpent = 0;
            if (member.getOrders() != null) {
                totalSpent = member.getOrders().stream()
                        .filter(o -> o.getActualPayment() != null)
                        .mapToInt(OrdersVO::getActualPayment)
                        .sum();
            }
            dto.setTotalSpent(totalSpent);

            return dto;
        }).collect(Collectors.toList());
        
     // ✨ 分頁邏輯針對已篩選的 dtoList
        int pageSize = 10;
        int totalItems = dtoList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        int fromIndex = Math.min((page - 1) * pageSize, totalItems);
        int toIndex = Math.min(page * pageSize, totalItems);
        List<MemberDTO> pageMembers = dtoList.subList(fromIndex, toIndex);

        // 傳到畫面
        model.addAttribute("members", pageMembers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // 傳篩選值 (保持畫面有原值)
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) allMembers.size() / pageSize));
        model.addAttribute("status", status);
        model.addAttribute("gender", gender);
        model.addAttribute("regDateFrom", regDateFrom);
        model.addAttribute("regDateTo", regDateTo);
        model.addAttribute("orderCountMin", orderCountMin);
        model.addAttribute("totalSpentMin", totalSpentMin);
        model.addAttribute("currentPath", "/smg/admin-members-search");


        return "admin/smg/admin-members-search";
    }
    
    @PostMapping("/batch-send-message")
    @ResponseBody
    public Map<String, Object> batchSendMessage(@RequestBody BatchMessageRequestDTO request, HttpSession session) {
        List<Integer> memIds = request.getMemIds();
        String content = request.getMessageContent();

        // 取得目前登入管理員
        SmgVO smgr = (SmgVO) session.getAttribute("loggedInSmg");

        for (Integer memId : memIds) {
            MemberVO member = memService.getById(memId);
            dmService.addMessage(member, smgr, content);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("sentCount", memIds.size());
        return result;
    }


}
