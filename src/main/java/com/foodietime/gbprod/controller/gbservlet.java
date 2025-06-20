package com.foodietime.gbprod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.gbprod.dto.GroupBuyingDisplayDTO;
import com.foodietime.gbprod.model.GbprodService;
import com.foodietime.service.GroupBuyingDisplayService;

@Controller
@RequestMapping("/gb-mem")
public class gbservlet {
	
	
	@Autowired
    private GroupBuyingDisplayService groupBuyingDisplayService;
	
	@GetMapping("/gb_index")
	public String showLoginPage(Model model) {
		List<GroupBuyingDisplayDTO> popularProducts = groupBuyingDisplayService.getPopularProducts();
        model.addAttribute("popularProducts", popularProducts);
        
        // 獲取最新上架商品
        List<GroupBuyingDisplayDTO> latestProducts = groupBuyingDisplayService.getLatestProducts();
        model.addAttribute("latestProducts", latestProducts);
        
	    return "front/gb/gbindex"; // 對應到你的 Thymeleaf 登入頁面
	}
	
		/**
	     * 團購商品列表頁
	     * @param model Spring MVC Model
	     * @return 團購商品列表模板
	     */
	    @GetMapping("/products")
	    public String products(Model model) {
	        List<GroupBuyingDisplayDTO> allProducts = groupBuyingDisplayService.getAllActiveProducts();
	        model.addAttribute("products", allProducts);
	        
	        return "front/gb/products";
	    }
	    /**
	     * 搜尋團購商品
	     * @param keyword 搜尋關鍵字
	     * @param model Spring MVC Model
	     * @return 搜尋結果模板
	     */
	    @GetMapping("/search")
	    public String search(@RequestParam("keyword") String keyword, Model model) {
	        List<GroupBuyingDisplayDTO> searchResults = groupBuyingDisplayService.searchProducts(keyword);
	        model.addAttribute("products", searchResults);
	        model.addAttribute("keyword", keyword);
	        
	        return "front/gb/products";
	    }
	
	
	
	
}
