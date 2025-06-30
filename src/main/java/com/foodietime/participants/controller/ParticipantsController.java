package com.foodietime.participants.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodietime.member.model.MemberVO;
import com.foodietime.participants.model.ParticipantsService;
import com.foodietime.participants.model.ParticipantsVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/participants")
public class ParticipantsController {
	@Autowired
	private ParticipantsService participantsService;

	// 查詢所有參與者
	@GetMapping("/all")
	public String getAllParticipants(Model model) {
		List<ParticipantsVO> participants = participantsService.getAll();
		model.addAttribute("participants", participants);
		return "participants/participants-list"; // 返回顯示所有參與者的視圖
	}

	// 查詢某個團購案的所有參與者
	@GetMapping("/by-group-buying-case/{gbId}")
	public String getParticipantsByGroupBuyingCaseId(@PathVariable Integer gbId, Model model) {
		List<ParticipantsVO> participants = participantsService.getParticipantsByGroupBuyingCaseId(gbId);
		model.addAttribute("participants", participants);
		return "participants/participants-by-group-buying-case"; // 顯示某個團購案所有參與者的視圖
	}

	// 查詢某個參與者
	@GetMapping("/{parId}")
	public String getParticipantById(@PathVariable Integer parId, Model model) {
		ParticipantsVO participant = participantsService.getParticipantById(parId);
		if (participant != null) {
			model.addAttribute("participant", participant);
			return "participants/participant-detail"; // 顯示單個參與者的視圖
		} else {
			model.addAttribute("error", "參與者不存在");
			return "participants/participants-list"; // 返回到參與者列表頁面，並顯示錯誤訊息
		}
	}

	// 新增或修改參與者
	@PostMapping("/save")
	public String saveParticipant(@ModelAttribute ParticipantsVO participant, Model model) {
		// 調用服務層的保存方法
		ParticipantsVO savedParticipant = participantsService.save(participant);
		model.addAttribute("participant", savedParticipant);
		return "participants/participant-detail"; // 顯示新增或更新後的參與者詳情
	}
	
	// 新增地址
    @PostMapping("/add-address")
    public String addAddress(@ModelAttribute ParticipantsVO address, HttpSession session) {
        MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
        if (loggedInMember != null) {
            participantsService.addAddress(address, loggedInMember.getMemId());
        }
        return "redirect:/gb/leader-address";
    }
    
	// 編輯表單頁
    @GetMapping("/edit/{parId}")
    public String editAddressForm(@PathVariable Integer parId, Model model, HttpSession session) {
        ParticipantsVO addr = participantsService.getParticipantById(parId);
        model.addAttribute("address", addr);
        return "front/gb/gbleader/leader-address-edit";
    }

    // 編輯送出
    @PostMapping("/edit/{parId}")
    public String updateAddress(@PathVariable Integer parId, @ModelAttribute ParticipantsVO form, HttpSession session) {
        ParticipantsVO addr = participantsService.getParticipantById(parId);
        if (addr != null) {
            addr.setParName(form.getParName());
            addr.setParPhone(form.getParPhone());
            addr.setParAddress(form.getParAddress());
            participantsService.save(addr);
        }
        return "redirect:/gb/leader-address";
    }

    // 清空地址
    @PostMapping("/delete/{parId}")
    public String clearAddress(@PathVariable Integer parId) {
        ParticipantsVO addr = participantsService.getParticipantById(parId);
        if (addr != null) {
            addr.setParAddress(""); // 清空地址
            participantsService.save(addr);
        }
        return "redirect:/gb/leader-address";
    }

}
