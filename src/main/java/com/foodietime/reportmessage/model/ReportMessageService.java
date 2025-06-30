package com.foodietime.reportmessage.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.foodietime.member.model.MemberVO;
import com.foodietime.message.model.MessageVO;


@Service("ReportMessageService")
public class ReportMessageService {
	
	@Autowired
	private ReportMessageRepository repository;

	
	public ReportMessageVO save(ReportMessageVO vo) {
	    return repository.save(vo);
	}
	public ReportMessageVO updateReportMessage(Integer repMesId, Integer mesId, Integer memId, java.sql.Timestamp repMesDate, String repMesReason, byte repMesStatus) {
		ReportMessageVO reportmessageVO = new ReportMessageVO();

		MemberVO member = new MemberVO();
		member.setMemId(memId);
		

		MessageVO mes = new MessageVO();
		mes.setMesId(mesId);

		reportmessageVO.setRepMesId(repMesId);
		reportmessageVO.setMes(mes);
		reportmessageVO.setMember(member);
		reportmessageVO.setRepMesDate(repMesDate);
		reportmessageVO.setRepMesReason(repMesReason);
		reportmessageVO.setRepMesStatus(repMesStatus);
		return reportmessageVO;
	}

	public void deleteReportmessage(Integer repMesId) {
		repository.deleteById(repMesId);
	}

	public ReportMessageVO getOneReportPost(Integer repMesId) {
		return repository.findById(repMesId).orElse(null);
	}

	public List<ReportMessageVO> getAll(){
		return repository.findAll();
	}
	
	//後臺使用的方法
	public List<ReportMessageVO> findForAdmin(Byte status, Integer postId, String keyword, LocalDate startDate, LocalDate endDate) {
		return repository.findForAdmin(status, postId, keyword, startDate, endDate);
    }

}
