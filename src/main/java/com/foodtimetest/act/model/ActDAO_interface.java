package com.foodtimetest.act.model;

import java.util.List;



public interface ActDAO_interface {
	public void insert(ActVO actVO);
	public void update(ActVO actVO);
	public void delete(Integer actId);
	public ActVO findByPrimaryKey(Integer actId);//單一查詢出貨歷史
	public List<ActVO> getAll();
}
