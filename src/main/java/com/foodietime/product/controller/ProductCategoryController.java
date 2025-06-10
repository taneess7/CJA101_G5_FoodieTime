package com.foodietime.product.controller;

import com.foodietime.product.model.ProductCategoryService;
import com.foodietime.product.model.ProductCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
public class ProductCategoryController {

	@Autowired
    private ProductCategoryService categoryService;

    // 查全部
    @GetMapping("/list")
    public String listAll(Model model) {
        List<ProductCategoryVO> list = categoryService.getAllCategories();
        model.addAttribute("categoryList", list);
        return "category/listAllCategory";
    }

    // 顯示新增頁面
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("categoryVO", new ProductCategoryVO());
        return "category/addCategory";
    }

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

    // 顯示修改表單
    @GetMapping("/edit")
    public String showEditForm(@RequestParam Integer prodCateId, Model model) {
        ProductCategoryVO vo = categoryService.getCategoryById(prodCateId);
        model.addAttribute("categoryVO", vo);
        return "category/editCategory";
    }

    // 修改分類
    @PostMapping("/edit")
    public String update(@RequestParam Integer prodCateId,
                         @RequestParam String prodCate,
                         Model model) {
        try {
            ProductCategoryVO vo = categoryService.updateCategory(prodCateId, prodCate);
            model.addAttribute("categoryVO", vo);
            return "category/listOneCategory";
        } catch (RuntimeException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "category/editCategory";
        }
    }

    // 刪除分類
    @PostMapping("/delete")
    public String delete(@RequestParam Integer prodCateId, Model model) {
        categoryService.deleteCategory(prodCateId);
        return "redirect:/category/list";
    }

}
