package com.foodietime.store.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateService;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

//@Controller
@RequestMapping("/store")
public class StoreController {

	@Autowired
	StoreService storeSvc;

	@Autowired
	StoreCateService storeCateSvc;

	// 只要這裡判斷 session 沒有 storeEmail，就導回登入頁
	@GetMapping("store/store_edit.html")
	public String editStorePage(HttpSession session, ModelMap model) {

		String email = (String) session.getAttribute("storeEmail");
		if (email == null) {
			return "/store/storeLogin";
		}

		// 查資料並塞進 model
		StoreVO storeVO = storeSvc.findByStorEmail(email);
		model.addAttribute("storeVO", storeVO);
		return "/store/store_edit";
	}

	// 檢舉上下架
	@PostMapping("store/report") // 表單或 URL 的參數抓取名為 storId 的值，轉成 Integer 並賦值給 storId 變數
	public String reportStore(@RequestParam("storId") Integer storId, ModelMap model) {
		storeSvc.reportStore(storId);
		model.addAttribute("reportSuccess", true); // 傳true回前端
		return "store/listAllStore"; // 回原頁面

	}

	@GetMapping("addStore") // 顯示新增表單畫面 → GET
	public String addStore(ModelMap model) {
		StoreVO storeVO = new StoreVO();
		model.addAttribute("storeVO", storeVO);
		return "/store/store_edit";

	}

	@PostMapping("insert") // 接收表單送出的新增請求 → POST
	public String insert(@Valid StoreVO storeVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(storeVO, result, "upFiles"); // 新增完成後導向列表

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "商家照片: 請上傳照片");
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				storeVO.setStorPhoto(buf); // VO呼叫上傳照片的方法
			}
		}
		if (result.hasErrors() || parts[0].isEmpty()) {
			return "/store/store_edit"; // 輸入錯誤回到商家
		}

		/*************************** 2.開始新增資料 *****************************************/
		storeSvc.addStore(storeVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<StoreVO> list = storeSvc.getAll();
		model.addAttribute("storeListData", list);// for listAllStore.html 第85行用
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/store/listAllStores"; // //
												// 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/emp/listAllEmp")
	}

	/*
	 * 修改店家資料 1.先查詢 查詢某一筆資料以供更新
	 */// 表示從 HTTP 請求的 參數中取得名為 storId 的值，並將這個值轉成 String 型別，放到名為 storId 的變數中使用
	// 訪問 http://localhost:8080/getOne_For_Update?empno=7001→ 就會自動把 "7001" 傳給這個
	// empno 參數。
	@PostMapping("getOne_For_Update") // ModelMap Spring 提供的物件，讓你把資料傳到 JSP 頁面
	public String getOne_For_Update(@RequestParam("storId") String storId, ModelMap model) {// 你可以用
																							// model.addAttribute("key",
																							// value) 把資料塞進去，JSP 就可以用
																							// ${key} 取得
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		StoreVO storeVO = storeSvc.getOneStore(Integer.valueOf(storId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("storeVO", storeVO);
		return "/store/store_edit"; // 查詢完成後轉交update_store_input.html
	}

	/* 修改店家資料 2.修改圖片 */
	@PostMapping("update")
	public String update(@Valid StoreVO storeVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(storeVO, result, "upFiles"); // 移除upFiles的驗證錯誤

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片，使用原圖存入
			byte[] upFiles = storeSvc.getOneStore(storeVO.getStorId()).getStorPhoto();
			storeVO.setStorPhoto(upFiles);
		} else {
			for (MultipartFile multipartFile : parts) { // 使用者有選擇新圖片，使用新圖存入
				byte[] upFiles = multipartFile.getBytes();
				storeVO.setStorPhoto(upFiles);
			}
		}
		if (result.hasErrors()) {
			return "store/store_edit";
		}
		/*************************** 2.開始修改資料 *****************************************/

		storeSvc.updateStore(storeVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		storeVO = storeSvc.getOneStore(Integer.valueOf(storeVO.getStorId()));
		model.addAttribute("StoreVO", storeVO);
		return "store/store_edit";

	}

	@ModelAttribute("storeListData")
	protected List<StoreVO> referenceListData() {

		List<StoreVO> list = storeSvc.getAll();
		return list;
	}

	public BindingResult removeFieldError(StoreVO storeVO, BindingResult result, String removedFieldname) {

		// 1. 把所有錯誤中，排除掉指定欄位的錯誤
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldError -> !fieldError.getField().equals(removedFieldname)).collect(Collectors.toList());

		// 2. 建立一個新的 BindingResult，只保留其他欄位錯誤
		result = new BeanPropertyBindingResult(storeVO, "storeVO");

		// 3. 把保留的錯誤加進新的 BindingResult
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}

		return result;
	}

}
