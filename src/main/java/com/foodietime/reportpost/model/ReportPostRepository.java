package com.foodietime.reportpost.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPostVO, Integer> {

	// 依照會員查詢檢舉
	List<ReportPostVO> findByMember_MemId(Integer memId);

	// 依照貼文查詢檢舉
	List<ReportPostVO> findByPost_PostId(Integer postId);

	// 依照狀態查詢
	List<ReportPostVO> findByRepPostStatus(byte repPostStatus);

	/**
	 * 後台管理員專用查詢。 可根據檢舉狀態 (status)、貼文ID (postId) 和/或 檢舉理由關鍵字 (keyword) 進行篩選。 如果參數為
	 * null，則會忽略該條件。
	 */
	@Query("SELECT r FROM ReportPostVO r " + "LEFT JOIN FETCH r.post p " + "LEFT JOIN FETCH r.member m WHERE "
			+ "(:status IS NULL OR r.repPostStatus = :status) AND "
			+ "(:postId IS NULL OR r.post.postId = :postId) AND "
			+ "(:keyword IS NULL OR r.repPostReason LIKE %:keyword%) AND "
			+ "(:startDate IS NULL OR FUNCTION('DATE', r.repPostDate) >= :startDate) AND "
			+ "(:endDate IS NULL OR FUNCTION('DATE', r.repPostDate) <= :endDate) " + "ORDER BY r.repPostDate DESC")
	List<ReportPostVO> findForAdmin(@Param("status") Byte status, @Param("postId") Integer postId,
			@Param("keyword") String keyword, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Modifying
	@Query("UPDATE ReportPostVO r SET r.repPostStatus = :newStatus WHERE r.repPostId IN :repPostIds")
	int batchUpdateStatus(@Param("repPostIds") List<Integer> repPostIds, @Param("newStatus") byte newStatus);

	/**
	 * 計算指定會員的貼文被檢舉次數（只計算已處理的檢舉，狀態為1或2）
	 * @param memberId 會員ID
	 * @param statuses 檢舉狀態列表
	 * @return 被檢舉次數
	 */
	long countByPost_Member_MemIdAndRepPostStatusIn(Integer memberId, List<Byte> statuses);

	List<ReportPostVO> findByPost_Member_MemId(Integer memId);
}
