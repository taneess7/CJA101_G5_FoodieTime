package com.foodietime.groupbuyingcollectionlist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodietime.groupbuyingcollectionlist.model.GroupBuyingCollectionListService;

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
	public String 
	
	
	
	
}
