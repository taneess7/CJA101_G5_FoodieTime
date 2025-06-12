package com.foodietime.coupon.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CouponService {

		
		@Autowired
		private CouponRepository repository; 

		
		public void addCoupon(CouponVO couVO) {
			repository.save(couVO);  //原廠save可以用在新增和修改，有主鍵是update，沒有主鍵是insert
		}
		
		public void updateCoupon(CouponVO couVO) {
			repository.save(couVO);
		}
		
		public void deleteCoupon(Integer couId) {
			if(repository.existsById(couId))
				repository.deleteByCouId(couId); //StoreRepository 自訂刪除
		}
		
		public CouponVO getOneStore(Integer couId) {
			Optional<CouponVO> optional = repository.findById(couId); //內建單一查詢
			return optional.orElse(null); //如果值存在就回傳其值，否則回傳other的值
		}
		
		public List<CouponVO> getAll(){
			return repository.findAll(); //回傳全部
		}
		
}
