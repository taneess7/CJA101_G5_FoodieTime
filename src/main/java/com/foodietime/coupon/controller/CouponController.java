package com.foodietime.coupon.controller;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodietime.act.model.ActVO;
import com.foodietime.coupon.model.CouponService;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/store")
public class CouponController {

	@Autowired
	private CouponService couSvc;

	@Autowired
	private StoreService storeSvc;

	// 空表單，初始化給預設值(not null)
	@ModelAttribute("addCoupon")
	public CouponVO prepareCoupon() {
		CouponVO coupon = new CouponVO();
		coupon.setCouName("買100折20");
		coupon.setCouType("外送折扣");
		coupon.setCouDesc("適用草莓果昔");
		coupon.setCouMinOrd(100);
		coupon.setCouDiscount(BigDecimal.valueOf(0.0));
		coupon.setCouStartDate(Timestamp.from(Instant.now()));
		coupon.setCouEndDate(Timestamp.valueOf("2025-12-31 23:59:59"));

		return coupon;
	}

	// 不讓 Spring 綁定TimeStamp欄位
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				if (text != null && !text.isEmpty()) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
					setValue(Timestamp.valueOf(LocalDateTime.parse(text, formatter)));
				}
			}
		});
	}

	@GetMapping("/editCoupon")
	public String showEditPage(@RequestParam(required = false) Integer couId, Model model) {
//		public String showEditPage(@RequestParam(required = false) Integer couId, HttpSession session, Model model) {	
		// 從 session 取得登入商家 ID
//		StoreVO storeVO = (StoreVO) session.getAttribute("loggedInStore");
//		if(storeVO == null) {
//			return "redirect:/login"; //未登入，回到登入頁
//		}
//		Integer storId = storeVO.getStorId();
//		session.setAttribute("loggedInStore", storId);

		StoreVO storeVO = new StoreVO(); // 從資料庫撈資料
		Integer currentStorId = 5;

		// 只撈該商家擁有的優惠券作為下拉選單
		List<CouponVO> coupons = couSvc.getCouponsByStorId(currentStorId);
		model.addAttribute("coupons", coupons); // 下拉選單 th:each="cou : ${coupons}" 用

		// 有選coupon,就撈該couId做表單
		CouponVO couponVO = null;
		if (couId != null) {
			couponVO = null; // 初次進入頁面，不顯示表單
		} else if (couId == 0) {
			// 新增空白表單
			couponVO = new CouponVO();
			storeVO.setStorId(currentStorId); // 一定要設定 store 裡的 id
			couponVO.setStore(storeVO); // 把 store 放進 couponVO 裡
		} else {
			couponVO = couSvc.findById(couId); // 修改表單
		}

		model.addAttribute("coupon", couponVO); // 表單 th:object="${coupon}"用
		return "front/store/coupon_edit"; // Thymleleaf頁面
	}
}
