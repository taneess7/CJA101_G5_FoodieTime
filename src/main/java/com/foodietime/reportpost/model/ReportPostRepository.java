package com.foodietime.reportpost.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPostVO, Integer>{

    // 依照會員查詢檢舉
    List<ReportPostVO> findByMember_MemId(Integer memId);

    // 依照貼文查詢檢舉
    List<ReportPostVO> findByPost_PostId(Integer postId);

    // 依照狀態查詢
    List<ReportPostVO> findByRepPostStatus(byte repPostStatus);
}

