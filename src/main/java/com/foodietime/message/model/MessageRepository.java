package com.foodietime.message.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageVO, Integer>{

	List<MessageVO> findByPost_PostId(Integer postId);
	
	/**
     * 後台管理員專用查詢。
     * 可根據貼文ID (postId) 和/或 留言內容關鍵字 (keyword) 進行篩選。
     * 如果參數為 null，則會忽略該條件。
     */
    @Query("SELECT m FROM MessageVO m WHERE " +
           "(:postId IS NULL OR m.post.postId = :postId) AND " +
           "(:keyword IS NULL OR m.mesContent LIKE %:keyword%) " +
           "ORDER BY m.mesDate DESC")
    List<MessageVO> findForAdmin(@Param("postId") Integer postId, @Param("keyword") String keyword);

    @Modifying
    @Query("DELETE FROM MessageVO m WHERE m.mesId IN :mesIds")
    int deleteByMesIdIn(@Param("mesIds") List<Integer> mesIds);
}
