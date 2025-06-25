package com.foodietime.groupbuyingcases.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesService;

@Controller
@RequestMapping("/gb")
public class GroupBuyingCasesController {

	@Autowired
	private GroupBuyingCasesService groupBuyingCasesService;

	// 查詢某會員參加的所有團購案
//	@GetMapping("/member/{memId}")
//	public String findByMemId(@PathVariable Integer memId, Model model) {
//
//		List<GroupBuyingCasesVO> groupBuyingCases = groupBuyingCasesService.findByMemId(memId);
//
//		if (groupBuyingCases.isEmpty()) {
//			// 如果該會員沒有開設任何團購案，顯示錯誤訊息
//			model.addAttribute("error", "找不到該會員開設的團購案");
//		} else {
//			// 如果找到了團購案，將資料傳遞到視圖
//			model.addAttribute("groupBuyingCases", groupBuyingCases);
//		}
//
//		// 返回顯示團購案列表的視圖
//		return "front/gb/gbleader/leader-groups";
//	}

	// 查詢某會員開設且是團主的團購案
	@GetMapping("/leader-groups")
	public String findByLeader(HttpSession session, Model model) {
	    // 1. 檢查是否已登入
	    MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
	    if (loggedInMember == null) {
	        // 未登入 → 重導到登入頁面
	        return "redirect:/front/member/login";
	    }

	    // 2. 查詢該會員開設且為團主的團購案
	    Integer memId = loggedInMember.getMemId();
	    List<GroupBuyingCasesVO> groupBuyingCases =
	        groupBuyingCasesService.findByMember_MemIdAndLeader(memId, (byte) 0);

	    // 3. 放入 Model
	    if (groupBuyingCases.isEmpty()) {
	        model.addAttribute("error", "找不到該會員開設且為團主的團購案");
	    }
	    model.addAttribute("groupBuyingCases", groupBuyingCases);

	    // 4. 回傳列表頁面
	    return "front/gb/gbleader/leader-groups";
	}

	// 查詢某會員(團主)的某一團購詳細
	 @GetMapping("/leader-gbdetail/{gbId}")
	    public String leaderGbDetail(
	            @PathVariable Integer gbId,
	            HttpSession session,
	            Model model) {

	        // 1. 登入檢查
	        MemberVO me = (MemberVO) session.getAttribute("loggedInMember");
	        if (me == null) {
	            return "redirect:/front/member/login";
	        }

	        // 2. 同時按 gbId, memId, leader=0 (團主) 來查
	        GroupBuyingCasesVO group =
	            groupBuyingCasesService
	                .findByGbIdAndMember_MemIdAndLeader(gbId, me.getMemId(), (byte)0);

	        // 3. 找不到就回列表
	        if (group == null) {
	            return "redirect:/gb/leader-groups";
	        }

	        // 4. 放到 Model，顯示詳情
	        model.addAttribute("group", group);
	        model.addAttribute("participants", group.getParticipants());
	        return "front/gb/gbleader/leader-gbdetail";
	    }
	
	
//	// 新增修改團購案
//	@PostMapping("/save")
//	public String saveGroupBuyingCase(@ModelAttribute GroupBuyingCasesVO groupBuyingCasesVO, Model model) {
//
//		// 創建團購案
//		GroupBuyingCasesVO savedGroupBuyingCase = groupBuyingCasesService.saveGroupBuyingCase(groupBuyingCasesVO);
//
//		// 傳遞保存後的團購案到前端
//		model.addAttribute("groupBuyingCase", savedGroupBuyingCase);
//		return "front/gb/gbleader/leader-groups"; // 完成後跳轉至團購管理頁面
//	}
		
	
	
	// 顯示「編輯團購」表單，只允許自己作為團主編輯
	 @GetMapping("/leader-groups-edit/{gbId}")
	 public String showEditForm(
	         @PathVariable Integer gbId,
	         HttpSession session,
	         Model model) {

	     // 1. 登入檢查
	     MemberVO me = (MemberVO) session.getAttribute("loggedInMember");
	     if (me == null) {
	         return "redirect:/front/member/login";
	     }

	     // 2. 同時按 gbId, memId, leader = 0 (團主) 來撈
	     GroupBuyingCasesVO group = 
	         groupBuyingCasesService
	             .findByGbIdAndMember_MemIdAndLeader(gbId, me.getMemId(), (byte)0);
	     if (group == null) {
	         // 不是該筆團購的團主或該團購不存在 → 導回列表
	         return "redirect:/gb/leader-groups";
	     }

	     // 3. 放到 Model 裡供表單綁定
	     model.addAttribute("groupBuyingCase", group);
	     return "front/gb/gbleader/leader-groups-edit";
	 }

	
//	// 只負責「建立」
//	@PostMapping("/create")
//    public String createGroupBuyingCase(
//            @ModelAttribute GroupBuyingCasesVO vo,
//            HttpSession session) {
//
//        MemberVO me = (MemberVO) session.getAttribute("loggedInMember");
//        if (me == null) {
//            return "redirect:/front/member/login";
//        }
//        // 把目前會員設為團主
//        vo.setMember(me);
//        // 儲存
//        groupBuyingCasesService.saveGroupBuyingCase(vo);
//        return "redirect:/gb/leader-groups";
//    }
//
	 @PostMapping("/update/{gbId}")
	 public String updateGroupBuyingCase(
	         @PathVariable Integer gbId,
	         @ModelAttribute GroupBuyingCasesVO vo,
	         HttpSession session) {

	     // 1. 登入檢查
	     MemberVO me = (MemberVO) session.getAttribute("loggedInMember");
	     if (me == null) {
	         return "redirect:/front/member/login";
	     }

	     // 2. 權限＆存在性檢查：只有自己是團主(leader=0)的這筆團購才取出
	     GroupBuyingCasesVO existing =
	         groupBuyingCasesService
	             .findByGbIdAndMember_MemIdAndLeader(gbId, me.getMemId(), (byte)0);
	     if (existing == null) {
	         // 不存在或你不是團主 → 拒絕並導回列表
	         return "redirect:/gb/leader-groups";
	     }

	     // 3. 覆寫允許被修改的欄位
	     existing.setGbTitle(vo.getGbTitle());
	     existing.setGbDescription(vo.getGbDescription());
	     existing.setGbStartTime(vo.getGbStartTime());
	     existing.setGbEndTime(vo.getGbEndTime());
	     existing.setGbStatus(vo.getGbStatus());
	     existing.setGbMinProductQuantity(vo.getGbMinProductQuantity());
	     // …如果還有其他欄位可編輯，再一一設定…

	     // 4. 存回資料庫
	     groupBuyingCasesService.saveGroupBuyingCase(existing);

	     // 5. 完成後跳回團購管理列表
	     return "redirect:/gb/leader-groups";
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
//	@GetMapping("/leader/{gbId}")
//	public String findById(@PathVariable Integer gbId, Model model) {
//		Optional<GroupBuyingCasesVO> groupBuyingCaseOpt = groupBuyingCasesService.findById(gbId);
//		if (groupBuyingCaseOpt.isPresent()) {
//			model.addAttribute("groupBuyingCase", groupBuyingCaseOpt.get());
//			return "front/gb/gbleader/leader-gbdetail"; // 顯示單個團購案的詳情頁面
//		} else {
//			model.addAttribute("error", "團購案不存在");
//			return "front/gb/gbleader/leaderindex"; // 如果團購案不存在，返回列表頁面並顯示錯誤訊息
//		}
//	}



	


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
