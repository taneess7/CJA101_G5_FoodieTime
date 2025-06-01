package com.foodietime.storeCate.model;

import java.util.List;



public interface StoreCateDAO_interface {
	public void insert(StoreCateVO storeCateVO);
	public void update(StoreCateVO storeCateVO);
	public void delete(Integer storCateId);
	public StoreCateVO findByPrimaryKey(Integer storCateId);//單一查詢出貨歷史
	public List<StoreCateVO> getAll();
}
