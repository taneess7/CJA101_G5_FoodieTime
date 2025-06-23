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
@RequestMapping("/gb")
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
	@GetMapping("/leader-groups/{memId}")
	public String findByLeader(@PathVariable Integer memId, Model model) {
		// 1 = 是團主 (對應資料表 participants.IS_LEADER = 1)
		List<GroupBuyingCasesVO> groupBuyingCases = groupBuyingCasesService.findByMember_MemIdAndLeader(memId,
				(byte) 1);

		if (groupBuyingCases.isEmpty()) {
			model.addAttribute("error", "找不到該會員開設且為團主的團購案");
		} else {
			model.addAttribute("groupBuyingCases", groupBuyingCases);
		}
		return "front/gb/gbleader/leader-groups";
	}

//	// 查詢某店家開設的所有團購案
//    @GetMapping("/store/{storId}")
//    public String findByStoreId(@PathVariable Integer storId, Model model) {
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
//        return "front/gb/gbleader/leader-searchresult";
//    }
//
//	// 根據商品編號或商品名稱查詢團購案
//	@GetMapping("/product/{gbProdId}")
//	public String findByProdIdOrName(@PathVariable Integer gbProdId, @RequestParam(required = false) String gbProdName,
//			Model model) {
//		// 查詢該商品對應的所有團購案
//		List<GroupBuyingCasesVO> groupBuyingCases = groupBuyingCasesService.findByProdIdOrName(gbProdId, gbProdName);
//
//		if (groupBuyingCases.isEmpty()) {
//			// 如果該商品沒有對應的團購案，顯示錯誤訊息
//			model.addAttribute("error", "找不到該商品對應的團購案");
//			return "front/gb/gbleader/leaderindex"; // 返回團購案列表頁面並顯示錯誤訊息
//		}
//
//		// 如果找到了團購案，將資料傳遞到視圖
//		model.addAttribute("groupBuyingCases", groupBuyingCases);
//		return "front/gb/gbleader/leader-searchresult"; // 顯示商品對應的團購案列表視圖
//	}

	// 根據團購編號查詢團購案
	@GetMapping("/leader/{gbId}")
	public String findById(@PathVariable Integer gbId, Model model) {
		Optional<GroupBuyingCasesVO> groupBuyingCaseOpt = groupBuyingCasesService.findById(gbId);
		if (groupBuyingCaseOpt.isPresent()) {
			model.addAttribute("groupBuyingCase", groupBuyingCaseOpt.get());
			return "front/gb/gbleader/leader-gbdetail"; // 顯示單個團購案的詳情頁面
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
		return "front/gb/gbleader/leader-groups"; // 完成後跳轉至團購管理頁面
	}

	// 更新團購案狀態
//	@PostMapping("/{gbId}/status")
//	public String updateGroupBuyingCaseStatus(@PathVariable Integer gbId, @RequestParam Byte newStatus, Model model) {
//		GroupBuyingCasesVO updatedGroupBuyingCase = groupBuyingCasesService.updateGroupBuyingCaseStatus(gbId,
//				newStatus);
//		if (updatedGroupBuyingCase != null) {
//			model.addAttribute("groupBuyingCase", updatedGroupBuyingCase);
//			return "front/gb/gbleader/leader-groups"; // 顯示更新後的團購案詳情頁面
//		} else {
//			model.addAttribute("error", "團購案更新失敗");
//			return "front/gb/gbleader/leader-groups"; // 返回列表頁面並顯示錯誤訊息
//		}
//	}
	
//	@GetMapping("/search")
//    public String searchCases(
//            @RequestParam(required = false) Integer gbId,
//            @RequestParam(required = false) Integer gbProdId,
//            @RequestParam(required = false) String gbProdName,
//            Model model) {
//
//        // 1. 查單筆詳情
//        if (gbId != null) {
//            Optional<GroupBuyingCasesVO> voOpt = groupBuyingCasesService.findById(gbId);
//            if (voOpt.isPresent()) {
//                model.addAttribute("groupBuyingCase", voOpt.get());
//                return "front/gb/gbleader/leader-gbdetail";
//            } else {
//                model.addAttribute("error", "團購案不存在");
//                return "front/gb/gbleader/leaderindex";
//            }
//        }
//
//        // 2. 查多筆列表（商品編號 或 名稱）
//        List<GroupBuyingCasesVO> list =
//            groupBuyingCasesService.findByProdIdOrName(gbProdId, gbProdName);
//
//        if (list.isEmpty()) {
//            model.addAttribute("error", "找不到符合條件的團購案");
//            // 你可以回原列表頁或搜尋結果頁，看你要怎麼顯示
//            return "front/gb/gbleader/leaderindex";
//        }
//
//        model.addAttribute("groupBuyingCases", list);
//        return "front/gb/gbleader/leader-searchresult";
//    }
}
