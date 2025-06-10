package com.foodietime.reportmessage.model;

import java.util.List;

import com.foodietime.reportmessage.model.ReportMessageVO;

public class ReportMessageService {
	
	private ReportMessageDAO_interface dao;
	
	public ReportMessageService() {
		dao = new ReportMessageDAOImpl();
	}
	public ReportMessageVO addrepMessage(Integer mesId, Integer memId, java.sql.Timestamp repMesDate, char repMesReason, byte repMesStatus) {
		ReportMessageVO reportmessageVO = new ReportMessageVO();
		reportmessageVO.setMesId(mesId);
//		reportmessageVO.setMemId(memId);
		reportmessageVO.setRepMesDate(repMesDate);
		reportmessageVO.setRepMesReason(repMesReason);
		reportmessageVO.setRepMesStatus(repMesStatus);
		dao.insert(reportmessageVO);
		return reportmessageVO;
	}
	public ReportMessageVO updateReportMessage(Integer repMesId, Integer mesId, Integer memId, java.sql.Timestamp repMesDate, char repMesReason, byte repMesStatus) {
		ReportMessageVO reportmessageVO = new ReportMessageVO();
		reportmessageVO.setRepMesId(repMesId);
		reportmessageVO.setMesId(mesId);
//		reportmessageVO.setMemId(memId);
		reportmessageVO.setRepMesDate(repMesDate);
		reportmessageVO.setRepMesReason(repMesReason);
		reportmessageVO.setRepMesStatus(repMesStatus);
		dao.insert(reportmessageVO);
		return reportmessageVO;
	}
	
	public void deleteReportmessage(Integer repMesId) {
		dao.delete(repMesId);
	}
	
	public ReportMessageVO getOneReportPost(Integer repMesId) {
		return dao.findByPK(repMesId);
	}
	
	public List<ReportMessageVO> getAll(){
		return dao.getAll();
	}

}
