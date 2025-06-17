package com.foodietime.act.model;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.foodietime.product.model.ProductCategoryRepository;
import com.foodietime.product.model.ProductCategoryVO;
import com.foodietime.product.model.ProductRepository;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;

import jakarta.transaction.Transactional;

@Service
public class ActService {

		
	@Autowired
	private ActRepository repository;

	public void addAct(ActVO actVO) {
		repository.save(actVO);
	}

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
		
}
