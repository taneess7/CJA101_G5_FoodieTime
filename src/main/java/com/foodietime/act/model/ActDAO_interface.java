package com.foodietime.act.model;

import java.util.List;

public interface ActDAO_interface {
	// 此介面定義對資料庫的相關存取抽象方法
	int add(ActVO act);
	int update(ActVO act);
	int delete(Integer actId);
	ActVO findByPk(Integer actId);
	List<ActVO> getAll();
}
