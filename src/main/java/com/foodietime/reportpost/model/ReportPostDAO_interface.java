package com.foodietime.reportpost.model;

import java.util.List;

public interface ReportPostDAO_interface {
	
	
	int insert(ReportPostVO reportpostVO);
	int update(ReportPostVO reportpostVO);
	int delete(Integer repPostId);
	
	public ReportPostVO findByPK(Integer repPostId);
	public List<ReportPostVO> getAll();

}
