package com.foodietime.gbprod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;


import com.foodietime.gbprod.dto.GroupBuyingDisplayDTO;
import com.foodietime.gbprod.dto.ProductDetailDTO;

import com.foodietime.gbprod.service.GroupBuyingDisplayService;
import com.foodietime.gbprod.service.ProductDetailService;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesService;


@Controller
@RequestMapping("/gb")
public class gbservlet {
	
	
	@Autowired
    private GroupBuyingDisplayService groupBuyingDisplayService;
    
    @Autowired
    private ProductDetailService productDetailService;
    
  
    
	
	@GetMapping("/gbindex")
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
	        model.addAttribute("allProducts", allProducts);
	        
	        return "front/gb/gball";
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
        model.addAttribute("allProducts", searchResults);
        model.addAttribute("keyword", keyword);
        
        return "front/gb/gball";
    }
    
    /**
     * 顯示所有團購案例頁面
     * @param model Spring MVC Model
     * @return 全部團購案例模板
     */
    @GetMapping("/all")
    public String showAllGroupBuyingCases(Model model) {
        // 獲取所有活躍的團購案
        List<GroupBuyingDisplayDTO> allProducts = groupBuyingDisplayService.getAllActiveProducts();
        model.addAttribute("allProducts", allProducts);
        return "front/gb/gball";
    }
    
    /**
     * 顯示團購商品詳情頁面
     * @param gbId 團購案例ID
     * @param model Spring MVC Model
     * @return 商品詳情模板
     */
    @GetMapping("/detail/{gbId}")
    public String showProductDetail(@PathVariable Integer gbId, Model model) {
        
             // 從資料庫獲取商品詳情
             ProductDetailDTO productDetail = productDetailService.getProductDetail(gbId);
            
            if (productDetail == null) {
                model.addAttribute("error", "找不到該團購商品");
                return "front/gb/gbindex"; // 返回首頁並顯示錯誤訊息
            }
            
            // 將商品詳情資料傳遞給前端模板
            model.addAttribute("productDetail", productDetail);
            
            return "front/gb/product-detail";
        
    }
    
    

}