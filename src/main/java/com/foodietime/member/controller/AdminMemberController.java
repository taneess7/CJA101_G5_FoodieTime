package com.foodietime.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;

@Controller
@RequestMapping("/smg/admin-member")
public class AdminMemberController {

    @Autowired
    private MemService memService;

    // 編輯畫面
    @GetMapping("/edit/{id}")
    public String editMember(@PathVariable("id") Integer id, Model model) {
        MemberVO member = memService.getById(id);
        model.addAttribute("member", member);
        model.addAttribute("statuses", MemberVO.MemberStatus.values());
        model.addAttribute("noSpeakOptions", MemberVO.NoSpeakStatus.values());
        model.addAttribute("noPostOptions", MemberVO.NoPostStatus.values());
        model.addAttribute("noGroupOptions", MemberVO.NoGroupStatus.values());
        model.addAttribute("noJoinGroupOptions", MemberVO.NoJoingroupStatus.values());
        return "admin/member_edit";
    }

    // 提交表單更新
    @PostMapping("/update-status")
    public String updateMember(@ModelAttribute MemberVO formMember) {
        MemberVO original = memService.getById(formMember.getMemId());

        // 只更新權限相關欄位
        original.setMemStatus(formMember.getMemStatus());
        original.setMemNoSpeak(formMember.getMemNoSpeak());
        original.setMemNoPost(formMember.getMemNoPost());
        original.setMemNoGroup(formMember.getMemNoGroup());
        original.setMemNoJoingroup(formMember.getMemNoJoingroup());

        memService.save(original); // 呼叫你現有的 save() 即可
        return "redirect:/admin/member/edit/" + formMember.getMemId(); // 回原畫面
    }
    
    @GetMapping("/admin-members-permissions")
    public String showMemberPermissions(Model model) {
        List<MemberVO> members = memService.getAll();
        model.addAttribute("members", members);
        return "admin/smg/admin-member-permissions"; // ❗️完整路徑：資料夾 + 檔名（無 .html）
    }
}
