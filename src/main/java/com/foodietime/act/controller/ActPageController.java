package com.foodietime.act.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.foodietime.act.model.ActService;
import com.foodietime.act.model.ActVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;



@Controller 
public class ActPageController {
	@Autowired
	ActService actSvc;
	
	@Autowired
	StoreService storeSvc;
	
	/***進到前台畫面-店家看有什麼活動可以參加***/
	@GetMapping("/act")
    public String index() {
        return "/front/act/act"; // 對應 templates/act.html
    }
	
	/*** 進到前台畫面 - 會員看有店家有優惠 ***/
	@GetMapping("/act/storeActsForMem")
    public String showActPage() {
        return "front/act/actJoinStores"; // 請依你實際 HTML 存放的路徑調整
    }
	
	/*** 進到後台查詢頁 /src/main/resources/templates/admin/act/select_page.html 與 listAllAct.html ***/  
	@GetMapping("/act/select_page")
	public String act_select_page(Model model){//事先塞好getAllActs，直接用 ${actListData} 就能拿到
		model.addAttribute("actVO", new ActVO());
		return "admin/act/select_page"; //select_page.html
	}
	
	/***進到後台ListAllAct畫面**/
	@GetMapping("/act/listAllActs")
	public String listAllAct(Model model) { 
		return "front/act/actJoinStores";  //listAllActs.html
	}
	
	

	
	/***執行同一個Controller方法前，先準備資料，不用每個方法手動加model.addAttribute(...) 直接用 ${actListData} 就能拿到。 ***/  

	@ModelAttribute("actListData") // select_page.html . listAllAct.html
	protected List<ActVO> referenceListData_Act(Model model){
		
		return actSvc.getAllActs();//		List<ActVO> list = actSvc.getAllActs();//		return list;
	}//只是用 <option th:each="empVO : ${empListData}" → 只要回傳 List 就好，不用 new
	
	@ModelAttribute("storeListData")
	protected List<StoreVO> referenceListData_Store(Model model){
		model.addAttribute("storeVO", new StoreVO());
		return storeSvc.getAll();
	}
	//<select th:field="*{storeVO.storId}"> 一定要 new 一個 VO 放進 model
    //<option th:each="storeVO : ${storeListData}" 
	
	
	
	 

	
}

