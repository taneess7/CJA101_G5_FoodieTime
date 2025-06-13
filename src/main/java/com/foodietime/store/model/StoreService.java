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
		return optional.orElse(null); //如果值存在就回傳其值，否則回傳null
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
	
	
	public StoreVO findByStorEmail(String email) {
		return repository.findByStorEmail(email);
	}
	
	//檢舉次數
	public void reportStore(Integer storId) {
		StoreVO store = repository.findById(storId)
				.orElseThrow(() -> new RuntimeException("找不到該店家"));
				
		// 檢舉次數預設為 0
	    byte oldCount = store.getStorReportCount() == null ? 0 : store.getStorReportCount();
	    byte newCount = (byte) (oldCount + 1);

	    store.setStorReportCount(newCount);

	    // 根據檢舉次數決定上下架狀態
	    store.setStorStatus(newCount >= 3 ? (byte) 0 : (byte) 1);

		
		repository.save(store); //存回資料庫
	}
	

}
