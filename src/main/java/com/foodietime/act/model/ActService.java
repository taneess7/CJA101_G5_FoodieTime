package com.foodietime.act.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.act.hibernate.HibernateUtil_CompositeQuery_Act;

import jakarta.transaction.Transactional;

@Service
public class ActService {

	@Autowired
	private ActRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	//id 為 null 或該筆不存在，會變成新增。
	public void addAct(ActVO actVO) {
		repository.save(actVO);
	}

	//d 有值且對應資料庫有該筆資料，則是更新。(若有null的欄位會蓋掉原本資料)
	public void updateAct(ActVO actVO) {
		repository.save(actVO);
	}

	public void deleteAct(Integer actId) {
		if (repository.existsById(actId))
			repository.deleteByactId(actId);
	}

	public ActVO getOneAct(Integer actId) {
		Optional<ActVO> optional = repository.findById(actId);
		return optional.orElse(null);
	}

	public List<ActVO> getAllActs() { // 查所有資料
		return repository.findAll();
	}

	public ActVO findById(Integer id) { // 查單筆資料
		return repository.findById(id).orElse(null);
	}
	
	public List<ActVO> findActsBetween(String start, String end) { //查區間資料
		return repository.findActsBetween(start, end);
	}

	
	
	//測試1- 更新活動名稱 -查資料後再改欄位
	
	  public String updateStoreAct(Integer id, ActVO newActData) {
	        ActVO act = repository.findById(id).orElse(null);
	        if (act != null) return "找不到活動";
	        	
	            act.setActCate(newActData.getActCate());
	            act.setActName(newActData.getActName());
	            act.setActContent(newActData.getActContent());
	            act.setActLastUpdate(new Timestamp(System.currentTimeMillis()));//活動建立時間
	            act.setActStartTime(newActData.getActStartTime());
	            act.setActEndTime(newActData.getActEndTime());
	            act.setActStatus(newActData.getActStatus());
	            act.setActDiscount(newActData.getActDiscount());
	            act.setActDiscValue(newActData.getActDiscValue());
	            act.setActPhoto(newActData.getActPhoto());
	            act.setActLastUpdate(new Timestamp(System.currentTimeMillis()));//最後更新時間
	            act.setIsGlobal(newActData.getIsGlobal());
	            
	            repository.save(act); // 更新
	            return "活動已更新（ID: " + id + "，是否全店適用: " + newActData.getIsGlobal() + "）";
	        }
	    
	  
	  
	// 更新所有圖片
	@Transactional
	public void updateAllActPhotos(MultipartFile[] files) throws IOException {
		List<ActVO> actList = repository.findAll(); // 撈出所有活動
		int count = Math.min(files.length, actList.size());

		for (int i = 0; i < count; i++) {
			MultipartFile file = files[i];
			if (!file.isEmpty()) {
				actList.get(i).setActPhoto(file.getBytes());
			}

			repository.saveAll(actList); // 批次更新資料庫
		}

	}

	public List<ActVO> getAllMap(Map<String, String[]> map) {
		
		return HibernateUtil_CompositeQuery_Act.getAllC(map,sessionFactory.openSession()); 
	}

}
