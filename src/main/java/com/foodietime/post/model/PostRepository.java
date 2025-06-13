package com.foodietime.post.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodietime.member.model.MemberVO;


@Repository
public interface PostRepository extends JpaRepository<PostVO, Integer>{
	// 關鍵字搜尋（標題內含字）
    List<PostVO> findByPostTitleContainingAndPostStatus(Integer postStatus, String keyword);

    // 我的貼文（根據會員）
    List<PostVO> findByMemId(MemberVO member);

    // 熱門貼文（按讚數排序）
    List<PostVO> findByPostStatusOrderByLikeCountDesc(Integer postStatus);

    // 熱門貼文前五筆（首頁用）
    List<PostVO> findTop5ByPostStatusOrderByLikeCountDesc(Integer postStatus);

    // 根據瀏覽數排序
    List<PostVO> findTop10ByPostStatusOrderByViewsDesc(Integer postStatus);

}
