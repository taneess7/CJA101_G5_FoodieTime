package com.foodtimetest.store.model;

import java.util.List;


public interface StoreDAO_interface {
	public void insert(StoreVO storeVO);
	public void update(StoreVO storeVO);
	public void delete(Integer storId);
	public StoreVO findByPrimaryKey(Integer storId);//單一查詢出貨歷史
	public List<StoreVO> getAll();
}
