package com.foodietime.coupon.controller;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.foodietime.coupon.model.CouponService;
import com.foodietime.coupon.model.CouponVO;

import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/store")
public class CouponController {

	@Autowired
	private CouponService couSvc;

	@Autowired
	private StoreService storeSvc;

	

	//店家優惠券編輯頁面
	@GetMapping("/editCoupon")
	public String showEditPage(@RequestParam(required = false) Integer couId, HttpSession session, Model model) {

		// 1. 從 session 取得登入商家 ID
		StoreVO loggedInStore  = (StoreVO) session.getAttribute("loggedInMember");
		if (loggedInStore == null) {
			return "redirect:/front/member/login";
		}
		Integer storId = loggedInStore.getStorId();


		// 只撈該商家擁有的優惠券作為下拉選單
		List<CouponVO> coupons = couSvc.getCouponsByStorId(storId);
		model.addAttribute("coupons", coupons); // 下拉選單 th:each="cou : ${coupons}" 用

		// 有選coupon,就撈該couId做表單
		CouponVO couponVO = null;

		if (couId == null || couId == 0) {

			// 新增空白表單
			couponVO = new CouponVO();
			// 填入預設值
			couponVO.setCouMinOrd(0); 
			couponVO.setCouDiscount(BigDecimal.valueOf(0.0));
			couponVO.setCouStartDate(Timestamp.from(Instant.now()));
			
			
			StoreVO storeVO = new StoreVO(); // 從資料庫店家撈資料
			storeVO.setStorId(storId); // 設定 store 裡的 id
			couponVO.setStore(storeVO); // 設定關聯商家
		} else {
			couponVO = couSvc.findById(couId); // 修改表單
		}

		model.addAttribute("coupon", couponVO); // 表單 th:object="${coupon}"用
		return "front/store/coupon_edit"; // Thymleleaf頁面
	}

	// 新增
	@PostMapping("/coupon/save")
	public String insert( // , HttpServletRequest request
			@Valid 
			@ModelAttribute("coupon") CouponVO couponVO, BindingResult result, Model model,//@ModelAttribute("coupon") 給 th:object="${coupon}"用
			RedirectAttributes redirectAttr) { //顯示新增成功訊息

		System.out.println(">>> insert方法觸發");

		// 如果驗證錯誤，補上 store 與優惠券下拉選單
		if (result.hasErrors()) {
			for (FieldError fe : result.getFieldErrors()) {
				System.out.println("欄位錯誤" + fe.getField() + "->" + fe.getDefaultMessage());
			}
			// 若驗證失敗，要補上 store 進去（否則會是 null）
			Integer currentStorId = 5;
	        if (couponVO.getStore() == null) {
	            StoreVO storeVO = new StoreVO();
	            storeVO.setStorId(currentStorId);
	            couponVO.setStore(storeVO);
	        }
	        //取得該店家所有coupons
			List<CouponVO> coupons = couSvc.getCouponsByStorId(currentStorId);
			model.addAttribute("coupons", coupons);
			
			//顯示驗證錯誤訊息:
			List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

		    model.addAttribute("errorMessages", errorMessages);
			return "front/store/coupon_edit";
		}

		// 新增 or 修改分流（判斷是否有 couId）
		if (couponVO.getCouId() == null) {
			System.out.println(">>> 進行新增");
			// 儲存優惠券
			couSvc.addCoupon(couponVO);
			//提示新增成功
			redirectAttr.addFlashAttribute("successMessage", "優惠券新增成功！");
			return "redirect:/store/editCoupon"; // 儲存成功重新導向 GET /store/editCoupon 方法，，避免重複提交

		} else {
			System.out.println(">>> 進行修改，couId: " + couponVO.getCouId());
			couSvc.updateCoupon(couponVO); 
			redirectAttr.addFlashAttribute("successMessage", "優惠券修改成功！");
			// 修改成功後回到該筆資料的編輯頁面
			return "redirect:/store/editCoupon?couId=" + couponVO.getCouId();
		}
		
	}
	
	//========================================================================================//
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
}
