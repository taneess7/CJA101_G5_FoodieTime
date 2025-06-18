package com.foodietime.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberDTO;
import com.foodietime.member.model.MemberVO;

@Controller
@RequestMapping("/smg/admin-members-permissions")
public class AdminMemberController {

    @Autowired
    private MemService memService;

    @GetMapping
    public String showMemberPermissions(Model model) {
        List<MemberVO> members = memService.getAll();
        model.addAttribute("members", members);
        return "admin/smg/admin-members-permissions";
    }

    // 提交表單更新
    @PostMapping("/update-status")
    public String updateMember(    
    	      @RequestParam("memId") Integer memId,
              @RequestParam("memStatus") Integer memStatus,
              @RequestParam("memNoSpeak") Integer memNoSpeak,
              @RequestParam("memNoPost") Integer memNoPost,
              @RequestParam("memNoGroup") Integer memNoGroup,
              @RequestParam("memNoJoingroup") Integer memNoJoingroup) {
    	MemberVO original = memService.getById(memId);

        // 只更新權限相關欄位
    	 original.setMemStatus(MemberVO.MemberStatus.values()[memStatus]);
         original.setMemNoSpeak(MemberVO.NoSpeakStatus.values()[memNoSpeak]);
         original.setMemNoPost(MemberVO.NoPostStatus.values()[memNoPost]);
         original.setMemNoGroup(MemberVO.NoGroupStatus.values()[memNoGroup]);
         original.setMemNoJoingroup(MemberVO.NoJoingroupStatus.values()[memNoJoingroup]);

        memService.save(original); // 呼叫你現有的 save() 即可
        // 回權限管理頁
        return "redirect:/smg/admin-members-permissions";
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
        dto.setMemLevel("銅牌會員"); // 先寫死
        dto.setPermissionLevel("VIP會員"); // 先寫死
        dto.setLastModifiedDate("2024-06-01"); // 先寫死
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



    
}
