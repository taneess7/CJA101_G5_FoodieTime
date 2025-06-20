package com.foodietime.act.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.foodietime.act.model.ActService;
import com.foodietime.act.model.ActVO;
import com.foodietime.store.model.StoreService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Controller
@Validated
@RequestMapping("/act")
public class SelectController {
	
	


	@Autowired
	ActService actSvc;

	@Autowired
	StoreService storeSvc;
	
	@GetMapping("getOne_For_Display")
	public String selectPage(Model model) {
		model.addAttribute("actVO", new ActVO()); // 一定要加
	    model.addAttribute("actListData", actSvc.getAllActs()); //  select 用到的列表
		return "admin/act/select_page";
	}

	
	//
	// select_page.html - POST - submit
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
	/*** 接收請求參數，驗證輸入格式 ***/
	@NotBlank(message = "活動編號:請勿空白") 
	@Digits(integer = 3, fraction = 0, message = "活動編號: 請填數字-請勿超過{integer}位數") @Min(value = 1, message = "活動編號: 不能小於{value}") @Max(value = 100, message = "活動編號； 不能超過{value}") @RequestParam("actId") String actId,
			ModelMap model)

	/*** 開始查資料 ***/
	{
		ActVO actVO = actSvc.getOneAct(Integer.valueOf(actId)); // elect_page.html name="actId"

		List<ActVO> list = actSvc.getAllActs();
		model.addAttribute("actListData", list); // select_page.html option th:each="actVO : ${actListData}

//model.addAttribute("storeVO", new StoreVO()); //列出所有店家-html還沒建立該選項
//	List<StoreVO> list2 = storeSvc.getAll();
//	model.addAttribute("storeListData", list2);

		/*** 查無資料 前端頁面顯示錯誤 ***/
		if (actVO == null) {
			model.addAttribute("errorMessage", "查無資料"); // select_page.html 錯誤列表:${errorMessage}
			return "admin/act/select_page";
		}
		/*** 查詢完成，有資料，準備轉交 ***/
		model.addAttribute("actVO", actVO); // 左邊:actVO -> <form th:object=${actVO}">, 右邊actVO for Controller的變數
		return "admin/act/select_page";
	}

	// @ExceptionHandler捕捉特定例外類別。驗證規則違反時會被拋出ConstraintViolationException
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ModelAndView handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations(); // 逐條列出每個錯誤
		StringBuilder strBuilder = new StringBuilder();// 建立字串容器
		for (ConstraintViolation<?> violation : violations) {
			strBuilder.append(violation.getMessage() + "<br>"); // html 每條錯誤訊息後加 <br> 換行
		}

		// 驗證錯誤，回填選單資料
		List<ActVO> list = actSvc.getAllActs();
		model.addAttribute("actListData", list);
//		model.addAttribute("storeVO", new StoreVO());
//		List<StoreVO> list2 = storeSvc.getAll();
//		model.addAttribute("actListData", list2);

		String message = strBuilder.toString();// 把全部拼起來變成一個字串

		return new ModelAndView("admin/act/select_page", "errorMessage", "請修正以下錯誤:<br>" + message);

		// ModelAndView(要跳轉的頁面，變數名稱，變數內容)
		// 前端接收: <div th:utext="${errorMessage}"></div>
	}
	
	
	
	//搜尋時間區間
	@GetMapping("/searchActs")
	public String searchActs(@RequestParam("actStartTime") String start,
	                         @RequestParam("actEndTime") String end,
	                         Model model) {
	    List<ActVO> result = actSvc.findActsBetween(start, end);
	    model.addAttribute("acts", result);
	    return "acts_result";
	}

}
