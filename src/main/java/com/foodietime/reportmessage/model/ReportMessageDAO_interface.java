package com.foodietime.reportmessage.model;

import java.util.List;

public interface ReportMessageDAO_interface {
	
	int insert(ReportMessageVO reportMessageVO);
	int update(ReportMessageVO reportMessageVO);
	int delete(Integer repPostId);
	
	public ReportMessageVO findByPK(Integer repPostId);
	public List<ReportMessageVO> getAll();

}
