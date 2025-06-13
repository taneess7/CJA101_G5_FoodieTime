
package com.foodietime.storeCate.model;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.store.model.StoreVO;


@Service("storeCateService")
public class StoreCateService {

	@Autowired
	StoreCateRepository repository;

	public void addStorCate(StoreCateVO cateVO) {
		repository.save(cateVO);
	}

	public void updateStoreCate(StoreCateVO cateVO) {
		repository.save(cateVO);
	}

	public void deleteStoreCate(Integer storCateId) {
		if (repository.existsById(storCateId))
			repository.deleteById(storCateId);
	}


	public StoreCateVO getOneStoreCate(Integer storCateId) {
		Optional<StoreCateVO> optional = repository.findById(storCateId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<StoreCateVO> getAll() {
		return repository.findAll();
	}

	public Set<StoreVO> getStoresByCate(Integer storCateId) {
		return getOneStoreCate(storCateId).getStore();
	}


}


	
	
	


