package com.foodietime.reportmessage.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodietime.favoritepost.model.FavoritePostVO;

@Repository
public interface ReportMessageRepository extends JpaRepository<ReportMessageVO, Integer> {
	/**
	 * 後台管理員專用查詢。
	 */
	@Query("SELECT rm FROM ReportMessageVO rm " 
			+ "LEFT JOIN FETCH rm.mes m " 
			+ "LEFT JOIN FETCH m.post p " // 抓取留言所屬的貼文 (孫子物件)
			+ "LEFT JOIN FETCH rm.member mem WHERE "
			+ "(:status IS NULL OR rm.repMesStatus = :status) AND "
			+ "(:postId IS NULL OR m.post.postId = :postId) AND "
			+ "(:keyword IS NULL OR rm.repMesReason LIKE %:keyword% OR m.mesContent LIKE %:keyword%) AND "
			+ "(:startDate IS NULL OR FUNCTION('DATE', rm.repMesDate) >= :startDate) AND "
			+ "(:endDate IS NULL OR FUNCTION('DATE', rm.repMesDate) <= :endDate) " + "ORDER BY rm.repMesDate DESC")
	List<ReportMessageVO> findForAdmin(@Param("status") Byte status, @Param("postId") Integer postId,
			@Param("keyword") String keyword, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
	
	/**
	 * 查詢特定會員的留言檢舉
	 * @param memId 會員ID
	 * @return 該會員的留言檢舉列表
	 */
	List<ReportMessageVO> findByMember_MemId(Integer memId);
}
