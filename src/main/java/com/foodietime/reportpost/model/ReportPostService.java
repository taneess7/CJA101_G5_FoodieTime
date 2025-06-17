package com.foodietime.reportpost.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

@Service("ReportPostService")
public class ReportPostService {

	@Autowired
	private ReportPostRepository repository;

	public ReportPostVO addReportPost(Integer postId, Integer memId, java.sql.Timestamp repPostDate, char repPostReason,
			byte repPostStatus) {
		ReportPostVO reportpostVO = new ReportPostVO();

		MemberVO member = new MemberVO();
		member.setMemId(memId);

		PostVO post = new PostVO();
		post.setPostId(postId);

		reportpostVO.setPost(post);
		reportpostVO.setMember(member);
		reportpostVO.setRepPostDate(repPostDate);
		reportpostVO.setRepPostReason(repPostReason);
		reportpostVO.setRepPostStatus(repPostStatus);

		return repository.save(reportpostVO);
	}

	public ReportPostVO updateReportPost(Integer repPostId, Integer postId, Integer memId,
			java.sql.Timestamp repPostDate, char repPostReason, byte repPostStatus) {
		ReportPostVO reportpostVO = new ReportPostVO();

		MemberVO member = new MemberVO();
		member.setMemId(memId);

		PostVO post = new PostVO();
		post.setPostId(postId);

		reportpostVO.setRepPostId(repPostId);
		reportpostVO.setPost(post);
		reportpostVO.setMember(member);
		reportpostVO.setRepPostDate(repPostDate);
		reportpostVO.setRepPostReason(repPostReason);
		reportpostVO.setRepPostStatus(repPostStatus);

		return repository.save(reportpostVO);
	}

	public void deleteReportPost(Integer repPostId) {
		repository.deleteById(repPostId);
	}

	public ReportPostVO getOneReportPost(Integer repPostId) {
		return repository.findById(repPostId).orElse(null);
	}

	public List<ReportPostVO> getAll() {
		return repository.findAll();
	}

}
