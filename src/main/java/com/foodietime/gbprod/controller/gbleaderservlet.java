package com.foodietime.gbprod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.gbprod.service.GbleaderService;

@Controller
@RequestMapping("/gb")
public class gbleaderservlet {

	 @Autowired
	 private GbleaderService gbleaderService;
    
	 	/**
	     * 查詢店家的促銷價格
	     * @param storeName 店家名稱
	     * @param model 用來傳遞資料到前端
	     * @return 返回顯示頁面
	     */
	    @GetMapping("/leaderindex")
	    public String getStoresWithPromotionPrices(Model model) {
	        // 呼叫 Service 層獲取指定店家的促銷價格
	        List<GbprodVO> storesWithPromotion = gbleaderService.getStoresWithPromotionPrices();

	        // 把結果添加到模型中
	        model.addAttribute("storesWithPromotion", storesWithPromotion);

	        // 返回要顯示頁面
	        return "front/gb/gbleader/leaderindex";  
	    }
        
}
