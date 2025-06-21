package com.foodietime.product.controller;

import com.foodietime.product.model.ProductCategoryService;
import com.foodietime.product.model.ProductCategoryServiceImpl;
import com.foodietime.product.model.ProductCategoryVO;
import com.foodietime.product.model.ProductService;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/category")
public class ProductCategoryController {

	@Autowired
    private ProductCategoryService categoryService;
	@Autowired
	private ProductService productService;
	
    // 查全部
    @GetMapping("/food-categories")
    public String listAll() {      
        return "/front/restaurant/food-categories";
    }
    
    //跳回首頁
    @GetMapping("/index")
    public String listindex() {
    	return "/index";
    }
    
    //跳到會員登入頁面
    @GetMapping("/login")
    public String listlogin() {
    	return "/front/member/login";
    }
    
    @GetMapping("/{cateId}")
    public String showCategoryPage(@PathVariable Integer cateId, Model model) {
        // 1. 找出分類名稱
        ProductCategoryVO categoryVO = categoryService.findById(cateId);
        model.addAttribute("categoryName", categoryVO.getProdCate());

        // 2. 找出該分類的店家
        List<StoreVO> storeList = categoryService.getStoresByCategoryId(cateId);
        model.addAttribute("storeList", storeList);
        model.addAttribute("categoryId", cateId);

        // 3. 根據店家撈商品
        List<ProductVO> allProducts = new ArrayList<>();
        for (StoreVO store : storeList) {
            List<ProductVO> products = productService.findByStoreId(store.getStorId());
            allProducts.addAll(products);
        }
        model.addAttribute("productList", allProducts);

        return "front/restaurant/category"; // 共用模板
    }
    
    //中式料理
    @GetMapping("/chinese-cuisine")
    public String listChinese() {
        
        return "/front/restaurant/chinese-cuisine";
    }
    //日式料理
    @GetMapping("/japanese-cuisine")
    public String listJapanese() {
        
        return "/front/restaurant/japanese-cuisine";
    }
    //韓式料理
    @GetMapping("/korean-cuisine")
    public String listKorean() {
        
        return "/front/restaurant/korean-cuisine";
    }
    //泰式料理
    @GetMapping("/thai-cuisine")
    public String listThai() {
        
        return "/front/restaurant/thai-cuisine";
    }
    //義式料理
    @GetMapping("/italian-cuisine")
    public String listItalian() {
        
        return "/front/restaurant/italian-cuisine";
    }
    //美式餐廳 (可動態帶入資料庫店家)
//    @GetMapping("/american-cuisine")
//    public String showAmeStore(Model model) {
//        List<StoreVO> ameStores = categoryService.getAmericanRestaurants(); // 呼叫 StoreService
//        model.addAttribute("AmeStore", ameStores); // 傳到 Thymeleaf 頁面
//        return "front/restaurant/american-cuisine"; // 返回 american-cuisine.html
//    }


    //甜點飲料
    @GetMapping("/dessert-drinks")
    public String listDessert() {
        
        return "/front/restaurant/dessert-drinks";
    }
    //素食料理
    @GetMapping("/vegetarian-cuisine")
    public String listVegetarian() {
        
        return "/front/restaurant/vegetarian-cuisine";
    }
//    // 顯示新增頁面
//    @GetMapping("/add")
//    public String showAddForm(Model model) {
//        model.addAttribute("categoryVO", new ProductCategoryVO());
//        return "category/addCategory";
//    }

//    // 新增分類
//    @PostMapping("/add")
//    public String add(@RequestParam String prodCate, Model model) {
//        try {
//            ProductCategoryVO vo = categoryService.addCategory(prodCate);
//            model.addAttribute("categoryVO", vo);
//            return "category/listOneCategory";
//        } catch (RuntimeException e) {
//            model.addAttribute("errorMsg", e.getMessage());
//            return "category/addCategory";
//        }
//    }
//
//    // 顯示修改表單
//    @GetMapping("/edit")
//    public String showEditForm(@RequestParam Integer prodCateId, Model model) {
//        ProductCategoryVO vo = categoryService.getCategoryById(prodCateId);
//        model.addAttribute("categoryVO", vo);
//        return "category/editCategory";
//    }

    // 修改分類
//    @PostMapping("/edit")
//    public String update(@RequestParam Integer prodCateId,
//                         @RequestParam String prodCate,
//                         Model model) {
//        try {
//            ProductCategoryVO vo = categoryService.updateCategory(prodCateId, prodCate);
//            model.addAttribute("categoryVO", vo);
//            return "category/listOneCategory";
//        } catch (RuntimeException e) {
//            model.addAttribute("errorMsg", e.getMessage());
//            return "category/editCategory";
//        }
//    }
//
//    // 刪除分類
//    @PostMapping("/delete")
//    public String delete(@RequestParam Integer prodCateId, Model model) {
//        categoryService.deleteCategory(prodCateId);
//        return "redirect:/category/list";
//    }

}
