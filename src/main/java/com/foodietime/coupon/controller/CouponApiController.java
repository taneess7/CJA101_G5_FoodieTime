package com.foodietime.coupon.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodietime.coupon.model.CouponService;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.store.model.StoreVO;

import jakarta.servlet.http.HttpSession;

public class CouponApiController {
	
	private CouponService couSvc;
	
	@GetMapping("/api/coupons")
	@ResponseBody
	public List<CouponVO> getStoreCouponsJson(HttpSession session) {
	    StoreVO storeVO = (StoreVO) session.getAttribute("loggedInStore");
	    if (storeVO == null) {
	        return Collections.emptyList(); // 或拋例外
	    }

	    Integer storId = storeVO.getStorId();
	    return couSvc.getCouponsByStorId(storId);
	}

}
