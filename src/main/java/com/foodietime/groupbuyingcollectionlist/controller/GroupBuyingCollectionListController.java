package com.foodietime.groupbuyingcollectionlist.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListId;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListRepository;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListService;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListVO;
import com.foodietime.member.model.MemberVO;

@Controller
@RequestMapping("/groupbuyingcollectionlist")
public class GroupBuyingCollectionListController {
    
    private final GroupBuyingCollectionListService groupBuyingCollectionListService;
     
    
    @Autowired
    public GroupBuyingCollectionListController(GroupBuyingCollectionListService groupBuyingCollectionListService) {
        this.groupBuyingCollectionListService = groupBuyingCollectionListService;
    }

    // 主頁
    @GetMapping("/select_page")
    public String index() {
        return "groupBuyingCollectionList/select_page";
    }
    
    // 查詢單筆收藏（判斷是否已收藏）
    @PostMapping("/findCollectionByMemAndGb")
    public String findSingleCollection(@RequestParam Integer memId,
                                       @RequestParam Integer gbId,
                                       Model model) {
        GroupBuyingCollectionListVO vo = groupBuyingCollectionListService.findByMemIdAndGbId(gbId, memId);
        model.addAttribute("groupBuyingCollectionListVO", vo);
        return "groupBuyingCollectionList/listOneCollectionList";
    }
    
    // 查詢某會員的所有收藏
    @PostMapping("/findCollectionByMem")
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
//        groupBuyingCollectionListService.removeFromCollection(id);
//        // 返回成功刪除的視圖
//        return "groupBuyingCollectionList/deleted";
//    }
	
}

