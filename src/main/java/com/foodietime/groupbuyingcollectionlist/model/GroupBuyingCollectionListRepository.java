package com.foodietime.groupbuyingcollectionlist.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.member.model.MemberVO;

import java.util.List;
import java.util.Optional;
@Repository
public interface GroupBuyingCollectionListRepository extends JpaRepository<GroupBuyingCollectionListVO, GroupBuyingCollectionListId> {


	// 查詢單筆收藏
    Optional<GroupBuyingCollectionListVO> findByIdMemIdAndIdGbId(Integer memId, Integer gbId);
    
    // 查詢某會員的所有收藏
    List<GroupBuyingCollectionListVO> findByIdMemId(Integer memId);
    
    // 查詢某團購的所有收藏
    List<GroupBuyingCollectionListVO> findByIdGbId(Integer gbId);
    
    // 檢查是否已收藏
    boolean existsByIdMemIdAndIdGbId(Integer memId, Integer gbId);
    
    // 計算收藏數量
//    long countByIdMemId(Integer memId);
//    long countByIdGbId(Integer gbId);
    
    // ===== 使用 @Query 的方法（推薦，更明確） =====
    
    @Query("SELECT g FROM GroupBuyingCollectionListVO g WHERE g.id.memId = :memId AND g.id.gbId = :gbId")
    Optional<GroupBuyingCollectionListVO> findByMemIdAndGbId(@Param("memId") Integer memId, @Param("gbId") Integer gbId);
    
    @Query("SELECT g FROM GroupBuyingCollectionListVO g WHERE g.id.memId = :memId ORDER BY g.createAt DESC")
    List<GroupBuyingCollectionListVO> findByMemId(@Param("memId") Integer memId);
    
    @Query("SELECT g FROM GroupBuyingCollectionListVO g WHERE g.id.gbId = :gbId ORDER BY g.createAt DESC")
    List<GroupBuyingCollectionListVO> findByGbId(@Param("gbId") Integer gbId);
    
    @Query("SELECT COUNT(g) > 0 FROM GroupBuyingCollectionListVO g WHERE g.id.memId = :memId AND g.id.gbId = :gbId")
    boolean existsByMemIdAndGbId(@Param("memId") Integer memId, @Param("gbId") Integer gbId);
    
    @Query("SELECT COUNT(g) FROM GroupBuyingCollectionListVO g WHERE g.id.memId = :memId")
    long countByMemId(@Param("memId") Integer memId);
    
    @Query("SELECT COUNT(g) FROM GroupBuyingCollectionListVO g WHERE g.id.gbId = :gbId")
    long countByGbId(@Param("gbId") Integer gbId);
    
    // 查詢會員收藏並包含相關資料
    @Query("SELECT g FROM GroupBuyingCollectionListVO g " +
           "LEFT JOIN FETCH g.member m " +
           "LEFT JOIN FETCH g.groupBuyingCase gb " +
           "WHERE g.id.memId = :memId " +
           "ORDER BY g.createAt DESC")
    List<GroupBuyingCollectionListVO> findByMemIdWithDetails(@Param("memId") Integer memId);
    
    // 刪除方法
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupBuyingCollectionListVO g WHERE g.id.memId = :memId AND g.id.gbId = :gbId")
    void deleteByMemIdAndGbId(@Param("memId") Integer memId, @Param("gbId") Integer gbId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM GroupBuyingCollectionListVO g WHERE g.id.memId = :memId")
    void deleteAllByMemId(@Param("memId") Integer memId);
}
