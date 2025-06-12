package com.foodietime.store.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.foodietime.store.model.StoreVO;

@Service
public class StoreService {
	
	@Autowired
	private StoreRepository repository; 
	

	
	public void addStore(StoreVO storeVO) {
		repository.save(storeVO);  //原廠save可以用在新增和修改，有主鍵是update，沒有主鍵是insert
	}
	
	public void updateStore(StoreVO storeVO) {
		repository.save(storeVO);
	}
	
	public void deleteStore(Integer storId) {
		if(repository.existsById(storId))
			repository.deleteByStorId(storId); //StoreRepository 自訂刪除
	}
	
	public StoreVO getOneStore(Integer storId) {
		Optional<StoreVO> optional = repository.findById(storId); //內建單一查詢
		return optional.orElse(null); //如果值存在就回傳其值，否則回傳other的值
	}
	
	public List<StoreVO> getAll(){
		return repository.findAll(); //回傳全部
	}
	
	//模糊查詢
//	public List<StoreVO> getAll(Map<String, String[]> map){ //回傳key-value類型的參數
//		return HibernateUtil_CompositeQuery_g05.getAllC(map.sessionFactory.openSession());
//		 // 呼叫 Hibernate 複合查詢工具類別的靜態方法 getAllC(...)
//	    // map 是從前端來的查詢條件
//	    // sessionFactory.openSession() 是打開一個新的 Hibernate Session，交給工具處理查詢
//	}
	

}
