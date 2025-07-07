	package com.foodietime.member.controller;
	
	import java.time.LocalDate;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.stream.Collectors;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.http.ResponseEntity;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.Model;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.member.model.BatchUpdateStatusRequestDTO;
import com.foodietime.member.model.MemService;
	import com.foodietime.member.model.MemberDTO;
	import com.foodietime.member.model.MemberVO;
import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
	
	@Controller
	@RequestMapping("/smg/admin-members-permissions")
	public class AdminMemberController {
	
	    @Autowired
	    private MemService memService;
	    
	    

	
	    @GetMapping
	    public String showMemberPermissions(
	            @RequestParam(value = "keyword", required = false) String keyword,
	            @RequestParam(value = "status", required = false) String status,
	            @RequestParam(value = "lastModified", required = false) String lastModified,
	            @RequestParam(value = "page", defaultValue = "1") int page,
	            HttpSession session,
	            Model model) {
	    	
	    	SmgVO smg = (SmgVO) session.getAttribute("loggedInSmg");
	    	model.addAttribute("loggedInSmgId", smg != null ? smg.getSmgrId() : -1);

	        int pageSize = 10;
	        List<MemberVO> allMembers = memService.getAll();

	        // 篩選條件
	        List<MemberVO> filtered = allMembers.stream()
	            .filter(member -> {
	                boolean match = true;

	                if (keyword != null && !keyword.isBlank()) {
	                    match = match && (
	                        member.getMemName().contains(keyword) ||
	                        member.getMemEmail().contains(keyword)
	                    );
	                }

	                if (status != null && !status.isBlank()) {
	                    int targetStatus = switch (status) {
	                        case "active" -> 1;
	                        case "inactive" -> 0;
	                        case "disabled" -> 2;
	                        default -> -1;
	                    };
	                    match = match && (member.getMemStatus().ordinal() == targetStatus);
	                }


	                if (lastModified != null && !lastModified.isBlank()) {
	                    LocalDate now = LocalDate.now();
	                    LocalDate compareDate = null;

	                    switch (lastModified) {
	                        case "today" -> compareDate = now;
	                        case "week" -> compareDate = now.minusDays(7);
	                        case "month" -> compareDate = now.minusMonths(1);
	                        case "quarter" -> compareDate = now.minusMonths(3);
	                    }

	                    LocalDate memberDate = now; // 未來你可替換成 member.getLastModifiedDate()
	                    match = match && (memberDate.isAfter(compareDate) || memberDate.isEqual(compareDate));
	                }

	                return match;
	            })
	            .collect(Collectors.toList());

	        // 分頁
	        int totalItems = filtered.size();
	        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
	        page = Math.max(1, Math.min(page, totalPages)); // 防止頁碼越界
	        int start = (page - 1) * pageSize;
	        int end = Math.min(start + pageSize, totalItems);

	        List<MemberVO> pageData = filtered.subList(start, end);

	        List<MemberDTO> dtoList = pageData.stream().map(member -> {
	            MemberDTO dto = new MemberDTO();
	            dto.setMemId(member.getMemId());
	            dto.setMemName(member.getMemName());
	            dto.setMemEmail(member.getMemEmail());
	            dto.setMemPhone(member.getMemPhone());
	            dto.setMemStatus(member.getMemStatus().ordinal());
	            dto.setMemNoSpeak(member.getMemNoSpeak().ordinal());
	            dto.setMemNoPost(member.getMemNoPost().ordinal());
	            dto.setMemNoGroup(member.getMemNoGroup().ordinal());
	            dto.setMemNoJoinGroup(member.getMemNoJoingroup().ordinal());

	            if (member.getMemTime() != null) {
	                dto.setMemTime(member.getMemTime().toLocalDateTime().toLocalDate().toString());
	            }

	            if (member.getMemAvatar() != null && member.getMemAvatar().length > 0) {
	                String base64 = java.util.Base64.getEncoder().encodeToString(member.getMemAvatar());
	                dto.setAvatarBase64(base64);
	            } else {
	                dto.setAvatarBase64("");
	            }

	            dto.setLastModifiedDate("-"); // 或 null，看你前端怎麼處理
	            dto.setLastModifiedBy("-");

	            return dto;
	        }).collect(Collectors.toList());

	        model.addAttribute("dbVersion", "20250705");
	        model.addAttribute("members", dtoList);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", totalPages);
	        model.addAttribute("param", Map.of(
	                "keyword", keyword == null ? "" : keyword,
	                "status", status == null ? "" : status,
	                "lastModified", lastModified == null ? "" : lastModified
	        ));
	        model.addAttribute("currentPath", "/smg/admin-members-permissions");

	        return "admin/smg/admin-members-permissions";
	    }

	
	    // 提交表單更新
	    @PostMapping("/update-status")
	    @ResponseBody
	    public ResponseEntity<Map<String, Object>> updateMember(
	            @RequestParam("memId") Integer memId,
	            @RequestParam("memStatus") Integer memStatus,
	            @RequestParam("memNoSpeak") Integer memNoSpeak,
	            @RequestParam("memNoPost") Integer memNoPost,
	            @RequestParam("memNoGroup") Integer memNoGroup,
	            @RequestParam("memNoJoingroup") Integer memNoJoingroup,
	            @RequestParam("permissionReason") String permissionReason,
	            HttpServletRequest request
	    ) {
	    	System.out.println("✅ 有進入 /update-status controller");
	        // 先建立回傳物件
	        Map<String, Object> result = new HashMap<>();
	        MemberVO original = memService.getById(memId);
	        
	        if (original == null) {
	            result.put("success", false);
	            result.put("message", "會員不存在");
	            return ResponseEntity.badRequest().body(result);
	        }

	        // 先取舊值
	        int oldStatus = original.getMemStatus().ordinal();
	        int oldNoSpeak = original.getMemNoSpeak().ordinal();
	        int oldNoPost = original.getMemNoPost().ordinal();
	        int oldNoGroup = original.getMemNoGroup().ordinal();
	        int oldNoJoingroup = original.getMemNoJoingroup().ordinal();

	        // 更新欄位
	        original.setMemStatus(MemberVO.MemberStatus.values()[memStatus]);
	        original.setMemNoSpeak(MemberVO.NoSpeakStatus.values()[memNoSpeak]);
	        original.setMemNoPost(MemberVO.NoPostStatus.values()[memNoPost]);
	        original.setMemNoGroup(MemberVO.NoGroupStatus.values()[memNoGroup]);
	        original.setMemNoJoingroup(MemberVO.NoJoingroupStatus.values()[memNoJoingroup]);

	        memService.save(original);

	        // 若該會員為目前登入者，且狀態變為停權 → 踢出
	        HttpSession session = request.getSession(false);
	        if (session != null) {
	            MemberVO loggedIn = (MemberVO) session.getAttribute("loggedInMember");
	            // 只踢出會員端登入者（不要踢管理員）
	            if (loggedIn != null && loggedIn.getMemId().equals(memId)) {
	                if (memStatus == 2) {
	                    session.setAttribute("kickoutReason", "您的帳號已被停權，請聯絡客服人員！");
	                    result.put("kickedOut", true);
	                } else {
	                    // ✅ 若是登入者，但非停權 → 同步更新 session 中的資料
	                    MemberVO refreshed = memService.getById(memId);
	                    session.setAttribute("loggedInMember", refreshed);
	                }
	            }
	        }

	        // 填入回傳資料
	        result.put("success", true);
	        result.put("memId", memId);
	        result.put("memStatus", memStatus);
	        result.put("memNoSpeak", memNoSpeak);
	        result.put("memNoPost", memNoPost);
	        result.put("memNoGroup", memNoGroup);
	        result.put("memNoJoingroup", memNoJoingroup);
	        result.put("oldStatus", oldStatus);
	        result.put("oldNoSpeak", oldNoSpeak);
	        result.put("oldNoPost", oldNoPost);
	        result.put("oldNoGroup", oldNoGroup);
	        result.put("oldNoJoingroup", oldNoJoingroup);
	        result.put("lastModifiedDate", LocalDate.now().toString());
	        result.put("lastModifiedBy", "系統管理員");

	        return ResponseEntity.ok(result);
	    }


	    
	    @GetMapping("/get-member/{memId}")
	    @ResponseBody
	    public MemberDTO getMember(@PathVariable("memId") Integer memId) {
	        MemberVO member = memService.getById(memId);
	
	        MemberDTO dto = new MemberDTO();
	        dto.setMemId(member.getMemId());
	        dto.setMemName(member.getMemName());
	        dto.setMemEmail(member.getMemEmail());
	        dto.setMemPhone(member.getMemPhone());
	        dto.setMemStatus(member.getMemStatus().ordinal());
	        dto.setMemNoSpeak(member.getMemNoSpeak().ordinal());
	        dto.setMemNoPost(member.getMemNoPost().ordinal());
	        dto.setMemNoGroup(member.getMemNoGroup().ordinal());
	        dto.setMemNoJoinGroup(member.getMemNoJoingroup().ordinal());
	        dto.setLastModifiedDate(LocalDate.now().toString());
	        dto.setLastModifiedBy("系統管理員"); // 先寫死
	
	        
	        if (member.getMemTime() != null) {
	            dto.setMemTime(member.getMemTime().toLocalDateTime().toLocalDate().toString());
	        }
	        
	        if (member.getMemAvatar() != null) {
	            String base64 = java.util.Base64.getEncoder().encodeToString(member.getMemAvatar());
	            dto.setAvatarBase64(base64);
	        }
	
	        return dto;
	    }
	    
	    @GetMapping("/member-avatar/{memId}")
	    @ResponseBody
	    public ResponseEntity<byte[]> getMemberAvatar(@PathVariable("memId") Integer memId) {
	        MemberVO member = memService.getById(memId);
	        byte[] avatar = member.getMemAvatar();
	
	        if (avatar == null || avatar.length == 0) {
	            // 沒圖 → 回傳預設圖 (用 classpath 讀)
	            try (var is = getClass().getResourceAsStream("/static/images/icons/logo.png")) {
	                if (is != null) {
	                    byte[] defaultAvatar = is.readAllBytes();
	                    return ResponseEntity.ok()
	                            .header("Content-Type", "image/png")
	                            .body(defaultAvatar);
	                } else {
	                    return ResponseEntity.notFound().build();
	                }
	            } catch (Exception e) {
	                return ResponseEntity.notFound().build();
	            }
	        } else {
	            // 有圖 → 回傳會員上傳的
	            return ResponseEntity.ok()
	                    .header("Content-Type", "image/jpeg")  // 如果你有存 mimeType，這裡可以自動判斷
	                    .body(avatar);
	        }
	    }
	    
	    @PostMapping("/batch-update-status")
	    @ResponseBody
	    public Map<String, Object> batchUpdateStatus(@RequestBody BatchUpdateStatusRequestDTO request) {
	        List<Integer> memIds = request.getMemIds();
	        Integer targetStatus = request.getTargetStatus();
	        String reason = request.getReason();
	     // TODO: 將 reason 存入 log 或 history 表

	        for (Integer memId : memIds) {
	            MemberVO member = memService.getById(memId);
	            member.setMemStatus(MemberVO.MemberStatus.values()[targetStatus]);
	            memService.save(member);
	        }

	        Map<String, Object> result = new HashMap<>();
	        result.put("success", true);
	        result.put("updatedCount", memIds.size());
	        result.put("memIds", memIds);
	        return result;
	    }
	}
