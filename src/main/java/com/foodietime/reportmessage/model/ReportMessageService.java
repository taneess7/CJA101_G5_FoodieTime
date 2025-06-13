//package com.foodietime.reportmessage.model;
//
//import java.util.List;
//
//import com.foodietime.member.model.MemService;
//import com.foodietime.member.model.MemberVO;
//import com.foodietime.message.model.MessageService;
//import com.foodietime.message.model.MessageVO;
//import com.foodietime.post.model.PostVO;
//import com.foodietime.reportmessage.model.ReportMessageVO;
//
//public class ReportMessageService {
//	/*
//	private ReportMessageDAO_interface dao;
//	private MemService memService;
//	private MessageService messageService;
//
//	public ReportMessageService() {
//		dao = new ReportMessageDAOImpl();
//	}
//	public ReportMessageVO addrepMessage(Integer mesId, Integer memId, java.sql.Timestamp repMesDate, char repMesReason, byte repMesStatus) {
//		ReportMessageVO reportmessageVO = new ReportMessageVO();
//
////		MemberVO member = memService.getOneMember(memId);
//		MessageVO message = messageService.getOneMessage(mesId);
//
//		reportmessageVO.setMesId(message);
////		reportmessageVO.setMemId(member);
//		reportmessageVO.setRepMesDate(repMesDate);
//		reportmessageVO.setRepMesReason(repMesReason);
//		reportmessageVO.setRepMesStatus(repMesStatus);
//		dao.insert(reportmessageVO);
//		return reportmessageVO;
//	}
//	public ReportMessageVO updateReportMessage(Integer repMesId, Integer mesId, Integer memId, java.sql.Timestamp repMesDate, char repMesReason, byte repMesStatus) {
//		ReportMessageVO reportmessageVO = new ReportMessageVO();
//
////		MemberVO member = memService.getOneMember(memId);
//		MessageVO message = messageService.getOneMessage(mesId);
//
//		reportmessageVO.setRepMesId(repMesId);
//		reportmessageVO.setMesId(message);
////		reportmessageVO.setMemId(member);
//		reportmessageVO.setRepMesDate(repMesDate);
//		reportmessageVO.setRepMesReason(repMesReason);
//		reportmessageVO.setRepMesStatus(repMesStatus);
//		dao.insert(reportmessageVO);
//		return reportmessageVO;
//	}
//
//	public void deleteReportmessage(Integer repMesId) {
//		dao.delete(repMesId);
//	}
//
//	public ReportMessageVO getOneReportPost(Integer repMesId) {
//		return dao.findByPK(repMesId);
//	}
//
//	public List<ReportMessageVO> getAll(){
//		return dao.getAll();
//	}
//*/
//}
