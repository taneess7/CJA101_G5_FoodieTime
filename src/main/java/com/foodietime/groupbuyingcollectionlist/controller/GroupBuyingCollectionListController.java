
package com.foodietime.groupbuyingcollectionlist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListService;
import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListVO;

@Controller
@RequestMapping("/groupbuyingcollectionlist")
public class GroupBuyingCollectionListController {
	
	private final GroupBuyingCollectionListService groupBuyingCollectionListService;
	
	@Autowired
	public GroupBuyingCollectionListController(GroupBuyingCollectionListService groupBuyingCollectionListService) {
		this.groupBuyingCollectionListService = groupBuyingCollectionListService;
	}

	//主頁
	@GetMapping("/select_page")
	public String index() {
		return "groupBuyingCollectionList/select_page";
	}
	
	//查詢單筆收藏（判斷是否已收藏）
	@PostMapping("/serchByMemGb")
	public String searchCollectionList(@RequestParam Integer memId,
			                           @RequestParam Integer gbId,
			                           Model model) {
		GroupBuyingCollectionListVO vo = groupBuyingCollectionListService.getOneCollection(memId, gbId);
		model.addAttribute("groupBuyingCollectionListVO",vo);
		return "groupBuyingCollectionList/listOneCollectionList";
	}
	
	
	
	
}

