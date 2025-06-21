package com.foodietime.post.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<PostVO, Integer>{
	// 1. 取得正常狀態的貼文
    List<PostVO> findByPostStatus(Integer postStatus);
    
    // 2. 根據會員查詢貼文
    List<PostVO> findByMemberMemId(Integer memId);
    
    // 3. 關鍵字搜尋
//    List<PostVO> findByPostTitleContainingAndPostStatus(String keyword, Integer postStatus);
    
    // 4. 熱門貼文
    List<PostVO> findTop5ByPostStatusOrderByLikeCountDesc(byte postStatus);

    // 按分類查詢
    List<PostVO> findByPostCate_PostCateId(Integer postCateId);

    @Query("SELECT p FROM PostVO p WHERE p.postTitle LIKE %:kw% ORDER BY p.likeCount DESC")
    List<PostVO> getPopularPosts(@Param("kw") String keyword);

    // 按讚數排序
    List<PostVO> findAllByOrderByLikeCountDesc();

    // 瀏覽數排序
    List<PostVO> findAllByOrderByViewsDesc();
    List<PostVO> findAllByOrderByViewsAsc();
    
}
