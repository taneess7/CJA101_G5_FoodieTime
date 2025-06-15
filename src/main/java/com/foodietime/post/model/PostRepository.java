//package com.foodietime.post.model;
//
//import java.util.List;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//
//@Repository
//public interface PostRepository extends JpaRepository<PostVO, Integer>{
//	// 1. 取得正常狀態的貼文（最重要）
//    List<PostVO> findByPostStatus(Integer postStatus);
//    
//    // 2. 根據會員查詢貼文（個人頁面用）
//    List<PostVO> findByMemberMemId(Integer memId);
//    
//    // 3. 關鍵字搜尋（搜尋功能用）
//    List<PostVO> findByPostTitleContainingAndPostStatus(String keyword, Integer postStatus);
//    
//    // 4. 熱門貼文（首頁推薦用）
//    List<PostVO> findTop5ByPostStatusOrderByLikeCountDesc(Integer postStatus);
//
//}
