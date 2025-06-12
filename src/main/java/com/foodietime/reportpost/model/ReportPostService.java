package com.foodietime.reportpost.model;

import java.util.List;

import com.foodietime.member.model.MemService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostService;
import com.foodietime.post.model.PostVO;



public class ReportPostService {
	/*
	private ReportPostDAO_interface dao;
	private MemService memService;
	private PostService postService;
	
	public ReportPostService() {
		dao = new ReportPostDAOImpl();
	}
	public ReportPostVO addReportPost(Integer postId, Integer memId, java.sql.Timestamp repPostDate, char repPostReason, byte repPostStatus) {
		ReportPostVO reportpostVO = new ReportPostVO();
		
//		MemberVO member = memService.getOneMember(memId);
		PostVO post = postService.getOnePost(postId);
		
		reportpostVO.setPostId(post);
//		reportpostVO.setMemId(member);
		reportpostVO.setRepPostDate(repPostDate);
		reportpostVO.setRepPostReason(repPostReason);
		reportpostVO.setRepPostStatus(repPostStatus);
		dao.insert(reportpostVO);
		return reportpostVO;
	}
	
	public ReportPostVO updateReportPost(Integer repPostId, Integer postId, Integer memId, java.sql.Timestamp repPostDate, char repPostReason, byte repPostStatus) {
		ReportPostVO reportpostVO = new ReportPostVO();
		
//		MemberVO member = memService.getOneMember(memId);
		PostVO post = postService.getOnePost(postId);
		
		reportpostVO.setRepPostId(repPostId);
		reportpostVO.setPostId(post);
//		reportpostVO.setMemId(member);
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
	
*/
}
