package com.foodietime.act.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.product.model.ProductCategoryRepository;
import com.foodietime.product.model.ProductCategoryVO;
import com.foodietime.product.model.ProductRepository;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;

@Service
public class ActService {

		
		@Autowired
		private ActRepository repository; 

		
		public void addAct(ActVO actVO) {
			repository.save(actVO);  //原廠save可以用在新增和修改，有主鍵是update，沒有主鍵是insert
		}
		
		public void updateAct(ActVO actVO) {
			repository.save(actVO);
		}
		
		public void deleteAct(Integer actId) {
			if(repository.existsById(actId))
				repository.deleteByActId(actId); //StoreRepository 自訂刪除
		}
		
		public ActVO getOneAct(Integer actId) {
			Optional<ActVO> optional = repository.findById(actId); //內建單一查詢
			return optional.orElse(null); //如果值存在就回傳其值，否則回傳other的值
		}
		
		public List<ActVO> getAll(){
			return repository.findAll(); //回傳全部
		}
		
}
