package com.foodietime.store.model;

import java.util.List;

import com.foodietime.storeCate.model.StoreCateVO;


public interface StoreDAO_interface {
	int add(StoreVO store);
	int update(StoreVO store);
	int delete(Integer storId);
	StoreVO findByPk(Integer storId);//單一查詢出貨歷史
	List<StoreVO> getAll();
	int add(StoreVO store, StoreCateVO cate);
}
