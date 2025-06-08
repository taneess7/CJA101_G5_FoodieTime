package com.foodietime.reportpost.model;

import java.util.List;

import com.foodietime.reportmessage.model.ReportMessageVO;

public class ReportPostService {
	
	private ReportPostDAO_interface dao;
	
	public ReportPostService() {
		dao = new ReportPostDAOImpl();
	}
	public ReportPostVO addReportPost(Integer postId, Integer memId, java.sql.Timestamp repPostDate, char repPostReason, byte repPostStatus) {
		ReportPostVO reportpostVO = new ReportPostVO();
		reportpostVO.setPostId(postId);
		reportpostVO.setMemId(memId);
		reportpostVO.setRepPostDate(repPostDate);
		reportpostVO.setRepPostReason(repPostReason);
		reportpostVO.setRepPostStatus(repPostStatus);
		dao.insert(reportpostVO);
		return reportpostVO;
	}
	
	public ReportPostVO updateReportPost(Integer repPostId, Integer postId, Integer memId, java.sql.Timestamp repPostDate, char repPostReason, byte repPostStatus) {
		ReportPostVO reportpostVO = new ReportPostVO();
		reportpostVO.setRepPostId(repPostId);
		reportpostVO.setPostId(postId);
		reportpostVO.setMemId(memId);
		reportpostVO.setRepPostDate(repPostDate);
		reportpostVO.setRepPostReason(repPostReason);
		reportpostVO.setRepPostStatus(repPostStatus);
		dao.insert(reportpostVO);
		return reportpostVO;
	}
	
	public void deleteReportpost(Integer repPostId) {
		dao.delete(repPostId);
	}
	
	public ReportPostVO getOneReportPost(Integer repPostId) {
		return dao.findByPK(repPostId);
	}
	
	public List<ReportPostVO> getAll(){
		return dao.getAll();
	}
	

}
