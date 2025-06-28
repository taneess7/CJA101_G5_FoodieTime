package com.foodietime.post.controller;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;
import com.foodietime.postcategory.model.PostCategoryService;
import com.foodietime.postcategory.model.PostCategoryVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/post")
public class PostMController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostCategoryService postCategoryService;

    // 後台貼文列表 (簡單查詢)
    @GetMapping({"", "/"})
    public String listAllPosts(
    		  @RequestParam(value = "status", required = false) Byte status,
              @RequestParam(value = "categoryId", required = false) Integer categoryId,
              @RequestParam(value = "keyword", required = false) String keyword,
            ModelMap model) {
    	
    	

    	// 呼叫 Service 層進行查詢，Service 會將參數傳遞給 Repository 的 @Query 方法
        List<PostVO> postList = postService.findPostsForAdmin(status, categoryId, keyword);
        
        List<PostCategoryVO> category = postCategoryService.getAll();

        model.addAttribute("postList", postList);
        model.addAttribute("category", category);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentCategory", categoryId);
        model.addAttribute("currentKeyword", keyword);
        
        return "back/post/postList";
    }

    // 後台查看單一貼文詳情
    @GetMapping("/detail")
    public String viewPostDetail(@RequestParam("postId") Integer postId, ModelMap model) {
        PostVO postVO = postService.getOnePost(postId);
        if (postVO == null) {
            model.addAttribute("errorMessage", "找不到指定的貼文");
            return "redirect:/admin/post/";
        }
        
        model.addAttribute("postVO", postVO);
        return "back/post/postDetail";
    }

    // 後台編輯貼文表單
    @GetMapping("/edit")
    public String editPostForm(@RequestParam("postId") Integer postId, ModelMap model) {
        PostVO postVO = postService.getOnePost(postId);
        if (postVO == null) {
            model.addAttribute("errorMessage", "找不到指定的貼文");
            return "redirect:/admin/post/";
        }
        
        List<PostCategoryVO> category = postCategoryService.getAll();
        model.addAttribute("category", category);
        model.addAttribute("postVO", postVO);
        return "back/post/postEdit";
    }

    // 後台更新貼文
    @PostMapping("/update")
    public String updatePost(
    		 @Valid @ModelAttribute("postVO") PostVO postVO,
             BindingResult result,
            ModelMap model) {
    	
    	if (result.hasErrors()) {
            // 如果驗證失敗，需要重新提供分類列表並返回編輯頁面
            model.addAttribute("category", postCategoryService.getAll());
            return "back/post/postEdit";
        }
        
        // Retrieve the existing post to get other necessary fields (memId, postDate, likeCount, views)
    	PostVO existingPost = postService.getOnePost(postVO.getPostId());
        if (existingPost == null) {
            model.addAttribute("errorMessage", "找不到指定的貼文進行更新");
            return "redirect:/admin/post/";
        }

     // 將表單未提交的欄位從原始資料中填回
        postVO.setMember(existingPost.getMember());
        postVO.setPostDate(existingPost.getPostDate());
        postVO.setLikeCount(existingPost.getLikeCount());
        postVO.setViews(existingPost.getViews());
        postVO.setEditDate(new Timestamp(System.currentTimeMillis())); // 每次後台編輯都更新時間

        PostVO updatedPost = postService.updatePost(
        
        		 postVO.getPostId(), postVO.getMember().getMemId(), postVO.getPostCate().getPostCateId(),
                 postVO.getPostDate(), postVO.getPostStatus(), postVO.getEditDate(),
                 postVO.getPostTitle(), postVO.getPostContent(), postVO.getLikeCount(),
                 postVO.getViews()
        );
        
        if (updatedPost != null) { // Check if the save operation was successful
            model.addAttribute("success", "貼文更新成功");
        } else {
            model.addAttribute("errorMessage", "貼文更新失敗");
        }
        return "redirect:/admin/post/detail?postId=" + postVO.getPostId();
    }

    // 後台刪除貼文
    @PostMapping("/delete")
    public String deletePost(@RequestParam("postId") Integer postId, ModelMap model) {
        postService.deletePost(postId); // Assuming deletePost returns void or handles success/failure internally
        model.addAttribute("success", "貼文刪除成功"); // Always shows success for now, consider return value from service
        return "redirect:/admin/post/";
    }

    // 後台批量操作
    @PostMapping("/batch")
    public String batchAction(
            @RequestParam("action") String action,
            @RequestParam("postIds") List<Integer> postIds,
            ModelMap model) {
        
        int affectedRows = 0;
        if (postIds == null || postIds.isEmpty()) {
            model.addAttribute("errorMessage", "請選擇要操作的貼文");
            return "redirect:/admin/post/";
        }

        if ("publish".equals(action)) {
            // Assuming a method in PostService like batchUpdateStatus(List<Integer> postIds, byte status)
            affectedRows = postService.batchUpdateStatus(postIds, (byte)0); // 0 for published
        } else if ("unpublish".equals(action)) {
            affectedRows = postService.batchUpdateStatus(postIds, (byte)1); // 1 for unpublished (draft)
        } else if ("ban".equals(action)) {
            affectedRows = postService.batchUpdateStatus(postIds, (byte)2); // 2 for banned
        } else if ("delete".equals(action)) {
            // Assuming a method in PostService like batchDelete(List<Integer> postIds)
            affectedRows = postService.batchDelete(postIds);
        } else {
            model.addAttribute("errorMessage", "無效的批量操作");
            return "redirect:/admin/post/";
        }
        
        if (affectedRows > 0) {
            model.addAttribute("success", "批量操作成功，影響 " + affectedRows + " 筆記錄");
        } else {
            model.addAttribute("errorMessage", "批量操作失敗，沒有記錄受到影響");
        }
        
        return "redirect:/admin/post/";
    }
}