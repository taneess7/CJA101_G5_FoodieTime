package com.foodietime.postcategory.controller;

import com.foodietime.postcategory.model.PostCategoryService;
import com.foodietime.postcategory.model.PostCategoryVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/PostCategory")
public class PostCategoryMController {

    @Autowired
    private PostCategoryService postCategoryService;

    
    // @GetMapping({"", "/"})
    // public String listPostCategory(Model model) {
    //     // 如果 model 中沒有 postCategoryVO，表示是初次載入，給一個空物件用於新增
    //     if (!model.containsAttribute("postCategoryVO")) {
    //         model.addAttribute("postCategoryVO", new PostCategoryVO());
    //     }
    //     List<PostCategoryVO> categories = postCategoryService.getAll();
    //     model.addAttribute("categories", categories);
    //     return "back/postcategory/PostCategory";
    // }

    // /**
    //  * 處理新增或更新分類的請求
    //  */
    // @PostMapping("/update")
    // public String updatePostCategory(@Valid @ModelAttribute("postCategoryVO") PostCategoryVO postCategoryVO,
    //                            BindingResult result,
    //                            RedirectAttributes redirectAttributes,
    //                            Model model) {

    //     if (result.hasErrors()) {
    //         // 如果驗證失敗，將錯誤訊息和所有分類列表帶回頁面
    //         List<PostCategoryVO> categories = postCategoryService.getAll();
    //         model.addAttribute("categories", categories);
    //         return "back/postcategory/PostCategory";
    //     }

    //     try {
    //         postCategoryService.save(postCategoryVO);
    //         String action = (postCategoryVO.getPostCateId() == null) ? "新增" : "更新";
    //         redirectAttributes.addFlashAttribute("success", "分類" + action + "成功！");
    //         return "redirect:/admin/PostCategory/";
    //     } catch (IllegalStateException e) {
    //         // 捕捉到名稱重複的例外
    //         result.rejectValue("postCate", "error.postCategoryVO", e.getMessage());
    //         model.addAttribute("categories", postCategoryService.getAll());
    //         return "back/postcategory/PostCategory";
    //     }
    // }

    
    // @GetMapping("/edit")
    // public String editPostCategoryForm(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
    //     PostCategoryVO postCategoryVO = postCategoryService.getOnePostCategory(id);
    //     if (postCategoryVO != null) {
    //         // 將要編輯的物件放入 RedirectAttributes，這樣重導後可以在 listCategories 方法中接收到
    //         redirectAttributes.addFlashAttribute("postCategoryVO", postCategoryVO);
    //     } else {
    //         redirectAttributes.addFlashAttribute("errorMessage", "找不到該分類！");
    //     }
    //     return "redirect:/admin/PostCategory/";
    // }

    
    // @PostMapping("/delete")
    // public String deletePostCategory(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
    //     try {
    //         postCategoryService.deletePostCategory(id);
    //         redirectAttributes.addFlashAttribute("success", "分類刪除成功！");
    //     } catch (IllegalStateException e) {
    //         // 從 Service 層捕捉到無法刪除的例外
    //         redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("errorMessage", "刪除失敗，發生未知錯誤。");
    //     }
    //     return "redirect:/admin/PostCategory/";
    // }
}
