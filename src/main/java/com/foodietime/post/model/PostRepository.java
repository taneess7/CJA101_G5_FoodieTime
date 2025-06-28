package com.foodietime.post.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostVO, Integer> {
	// 1. 查詢所有公開的貼文，並按編輯日期排序 (用來取代不安全的 findAll)
	List<PostVO> findAllByPostStatusOrderByEditDateDesc(byte postStatus);

	// 2. 根據會員查詢貼文
	List<PostVO> findByMemberMemId(Integer memId);

	// 3. 關鍵字搜尋
	List<PostVO> findByPostTitleContainingAndPostStatus(String keyword, byte postStatus);

	// 4. 熱門貼文
	List<PostVO> findTop5ByPostStatusOrderByLikeCountDesc(byte postStatus);

	// 按分類查詢
	@Query("SELECT p FROM PostVO p WHERE p.postCate.postCateId = :categoryId AND p.postStatus = 0 ORDER BY "
			+ "CASE WHEN :sort = 'like_Count' THEN p.likeCount END DESC, "
			+ "CASE WHEN :sort = 'views' THEN p.views END DESC, " + "p.editDate DESC")
	List<PostVO> findByCategoryAndSort(@Param("categoryId") Integer categoryId, @Param("sort") String sort);

	@Query("SELECT p FROM PostVO p WHERE p.postTitle LIKE %:kw% ORDER BY p.likeCount DESC")
	List<PostVO> getPopularPosts(@Param("kw") String keyword);

	// 按讚數排序
	List<PostVO> findAllByOrderByLikeCountDesc();

	// 瀏覽數排序
	List<PostVO> findAllByOrderByViewsDesc();

	List<PostVO> findAllByOrderByViewsAsc();
	
	// 我的貼文
	List<PostVO> findByMemberMemIdOrderByEditDateDesc(Integer memId);

	// ==================== 新增方法以支援 PostMController ====================

	/**
	 * 根據狀態、分類ID和關鍵字動態查詢後台貼文列表。 JPQL中的 `(:param IS NULL OR ...)`
	 * 語法允許參數為可選的。這是完成後台篩選功能的核心。
	 */

	// For combining all three parameters in a single query (more robust for
	// findPostsForAdmin)
	@Query("SELECT p FROM PostVO p WHERE " + "(:status IS NULL OR p.postStatus = :status) AND "
			+ "(:categoryId IS NULL OR p.postCate.postCateId = :categoryId) AND "
			+ "(:keyword IS NULL OR p.postTitle LIKE %:keyword%) " + "ORDER BY p.editDate DESC")
	List<PostVO> findFilteredPostsForAdmin(@Param("status") Byte status, @Param("categoryId") Integer categoryId,
			@Param("keyword") String keyword);

}
