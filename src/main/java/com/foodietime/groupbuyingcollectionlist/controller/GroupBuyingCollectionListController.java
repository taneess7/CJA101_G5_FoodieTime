package com.foodietime.groupbuyingcollectionlist.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListId;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListRepository;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListService;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListVO;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/gblist")
public class GroupBuyingCollectionListController {
    
         
    
    @Autowired
    private GroupBuyingCollectionListService groupBuyingCollectionListService;


    
    // 查詢單筆收藏（判斷是否已收藏）
    @PostMapping("/findByMemAndGb")
    public String findSingleCollection(@RequestParam Integer memId,
                                       @RequestParam Integer gbId,
                                       Model model) {
        GroupBuyingCollectionListVO vo = groupBuyingCollectionListService.findByMemIdAndGbId(gbId, memId);
        model.addAttribute("groupBuyingCollectionListVO", vo);
        return "groupBuyingCollectionList/listOneCollectionList";
    }
    
    // 查詢某會員的所有收藏
    @PostMapping("/findByMem")
    public String findCollectionByMem(@RequestParam Integer memId, Model model) {
		List<GroupBuyingCollectionListVO> collectionList = groupBuyingCollectionListService.findByMemId(memId);
		if(collectionList.isEmpty()) {
			model.addAttribute("errorMsgs", Arrays.asList("查無會員收藏資料"));
            return "groupBuyingCollectionList/select_page";
		}
		model.addAttribute("collectionList",collectionList);
		return "groupBuyingCollectionList/listAllCollectionList";
	}
			
    
    // 顯示新增表單
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("groupBuyingCollectionListVO", new GroupBuyingCollectionListVO());
        return "groupBuyingCollectionList/addGroupBuyingCollection";
    }

    // 新增收藏
    @PostMapping("/add")
    public String addGroupBuyingCollection(@RequestParam Integer memId,
                                         @RequestParam Integer gbId,
                                         Model model) {
        try {
            GroupBuyingCollectionListVO result = groupBuyingCollectionListService.addToCollection(memId, gbId);
            model.addAttribute("groupBuyingCollectionListVO", result);
            return "groupBuyingCollectionList/listOneGroupBuyingCollection";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }


    // 查詢所有團購收藏
    @GetMapping("/listAll")
    public String getAllGroupBuyingCollectionsList(Model model) {
        List<GroupBuyingCollectionListVO> list = groupBuyingCollectionListService.findAll();
        model.addAttribute("groupBuyingCollectionList", list);
        return "groupBuyingCollectionList/listAll";  // 返回對應的視圖頁面
    }

    // 刪除單筆團購收藏（根據 memId 和 gbId）
//    @PostMapping("/delete")
//    public String deleteGroupBuyingCollectionList(@RequestParam Integer memId,
//                                                  @RequestParam Integer gbId,
//                                                  Model model) {
//    	 // 創建 GroupBuyingCollectionListId 物件
//        GroupBuyingCollectionListId id = new GroupBuyingCollectionListId(memId, gbId);
//        // 呼叫 service 層的 deleteById 方法
//        groupBuyingCollectionListService.deleteByMemIdAndGbId(id);
//        // 返回成功刪除的視圖
//        return "groupBuyingCollectionList/deleted";
//    }
	
// // 新增收藏團購（AJAX）
//    @PostMapping("/addFavoriteGb")
//    @ResponseBody
//    public Object addFavoriteGb(@RequestParam Integer memId, @RequestParam Integer gbId) {
//        HashMap<String, Object> result = new HashMap<>();
//        try {
//            groupBuyingCollectionListService.addToCollection(memId, gbId);
//            result.put("success", true);
//        } catch (Exception e) {
//            result.put("success", false);
//            result.put("error", e.getMessage());
//        }
//        return result;
//    }
//
//    // 取消收藏團購（AJAX）
//    @PostMapping("/removeFavoriteGb")
//    @ResponseBody
//    public Object removeFavoriteGb(@RequestParam Integer memId, @RequestParam Integer gbId) {
//        HashMap<String, Object> result = new HashMap<>();
//        boolean removed = groupBuyingCollectionListService.removeFromCollection(memId, gbId);
//        result.put("success", removed);
//        if (!removed) result.put("error", "未找到收藏紀錄");
//        return result;
//    }
//
//    // 查詢會員所有收藏團購（for 收藏團購頁面）
//    @GetMapping("/myFavoriteGb")
//    @ResponseBody
//    public Object myFavoriteGb(@RequestParam Integer memId) {
//        List<GroupBuyingCollectionListVO> list = groupBuyingCollectionListService.findByMemIdWithDetails(memId);
//        return list;
//    }

 // 收藏團購頁面（資料庫查詢渲染）
    @GetMapping("/myFavoriteGbPage")
    public String myFavoriteGbPage(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/login"; // 或導向登入頁
        }
        Integer memId = member.getMemId();
        List<GroupBuyingCollectionListVO> list = groupBuyingCollectionListService.findByMemIdWithDetails(memId);
        model.addAttribute("collectionList", list);
        return "front/favoritelist/gb-favoritelist";
    }

    
    @PostMapping("/remove")
    public String removeGroupBuyingCollection(@RequestParam Integer memId,
                                              @RequestParam Integer gbId,
                                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        boolean success = groupBuyingCollectionListService.removeFromCollection(memId, gbId);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "移除失敗或不存在");
        }
        return "redirect:/gb/gbfavoritelist";
    }

}

