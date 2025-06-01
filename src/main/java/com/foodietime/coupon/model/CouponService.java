package com.foodietime.coupon.model;

import java.sql.Timestamp;
import java.util.List;

public class CouponService {

	private CouponDAO_interface dao;

	public CouponService() {
		dao = new CouponDAO();
	}

	public CouponVO addCoupon(Integer storId, String couDes, String couType, Integer couMinOrd, Timestamp couDate) {

		CouponVO couponVO = new CouponVO();

		couponVO.setStorId(storId);
		couponVO.setCouDes(couDes);
		couponVO.setCouType(couType);
		couponVO.setCouMinOrd(couMinOrd);
		couponVO.setCouDate(couDate);
		dao.insert(couponVO);

		return couponVO;
	}

	public CouponVO updateCoupon(Integer couId, Integer storId, String couDes, String couType, Integer couMinOrd,
			Timestamp couDate) {

		CouponVO couponVO = new CouponVO();

		couponVO.setCouId(couId);
		couponVO.setStorId(storId);
		couponVO.setCouDes(couDes);
		couponVO.setCouType(couType);
		couponVO.setCouMinOrd(couMinOrd);
		couponVO.setCouDate(couDate);
		dao.update(couponVO);
		
		return couponVO;
	}

	public void deleteCoupon(Integer couId) {
		dao.delete(couId);
	}

	public CouponVO getOneCoupon(Integer couId) {
		return dao.findByPrimaryKey(couId);
	}

	public List<CouponVO> getAll() {
		return dao.getAll();
	}
	
	public List<CouponVO> getStorCoupon(Integer storId) {
		return dao.findByStorId(storId);
	}
	
	public List<CouponVO> getDistinctStorId(){
		return dao.findDistinctStorId();
	}

}
