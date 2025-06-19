package com.foodietime.act.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodietime.act.model.ActService;
import com.foodietime.act.model.ActVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateService;
import com.foodietime.storeCate.model.StoreCateVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

//@Controller
//@RequestMapping("/act")
public class AddUpdateController {
	
	@Autowired
	ActService actSvc;
	
	@Autowired
	StoreService storeSvc;
	
	@Autowired
	StoreCateService storeCateSvc;
	
	
	//新增
//	@GetMapping("/addAct")
//	public String addAct(Model model) {
//		ActVO actVO = new ActVO();
//		model.addAttribute("actVO", actVO);
//		return "back-end/act/addAct";
//	}
	
	//新增
	@PostMapping("/insert")
	public String insert(
			@Valid ActVO actVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts,RedirectAttributes redirectAttrs) throws IOException{  
		/***接收請求參數，輸入格式錯誤處理***/
		// 去除 BindingResult 中某個欄位的 FieldError 紀錄（例如不想驗證圖片欄位）
		result = removeFieldError(actVO, result, "upFiles"); //<input type="file" name="upFiles"> removeFieldError 不想因為「圖片未上傳」就不讓表單送出，排除圖片上傳欄位不被檢查
		
		if (parts[0].isEmpty()) { //未選擇圖片，補驗證訊息
			model.addAttribute("errorMessage", "活動照片: 請上傳照片"); //addAct.html th:utext="${errorMessage}" 	
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes(); //圖片轉byte[]
				actVO.setActPhoto(buf);
			}
		}
		if(result.hasErrors() || parts[0].isEmpty()) {
			List<FieldError> errors = result.getFieldErrors(); //FieldError 表單欄位單一BindingResult，使用傳給前端看錯誤
			for (FieldError error : errors) {
				System.out.println("欄位" + error.getField());
				System.out.println("錯誤訊息:" + error.getDefaultMessage());
			}
			return "back-end/act/addAct"; //回到原頁顯示錯誤訊息
			
		}
	
		/**********開始新增資料************/
		actSvc.addAct(actVO);
		/**********新增完成，準備轉交********/
		List<ActVO> list = actSvc.getAllActs();
		model.addAttribute("actListData", list);
		redirectAttrs.addFlashAttribute("success", "- (新增成功)"); //用redirectAttrs存成功訊息在listAllAct.html顯示
		return "redirect:/act/listAllAct"; //新增成功後發出HTTP302 重導，發出新的request 到ActPageController @GetMapping("/act/listAllAct")
		
	}

	
	//點選修改按鈕，進入修改畫面listOneAct.html
	@PostMapping("getOne_For_Updat")
	public String getOne_For_Update(@RequestParam("actId") String actId, ModelMap model) {
		/***接收參數，進入進入修改畫面listOneAct.html，查詢actId資料***/
		ActVO actVO = actSvc.getOneAct(Integer.valueOf(actId));
		
		/***查詢完成，轉交 update_act_input.html***/
		model.addAttribute("actVO", actVO);
		return "back-end/act/update_act_input";
	}
	
	//填完資料，點選確認修改按鈕，檢查actVO欄位格式
	@PostMapping("update")
	public String update(@Valid ActVO actVO, BindingResult result, ModelMap model,
						 @RequestParam("upFiles") MultipartFile[] parts) throws IOException{
		/**接收參數，驗證格式**/
		//去除BindingResult中 upFiles欄位的FieldError紀錄
		result = removeFieldError(actVO, result, "upFiles");
		
		if (parts[0].isEmpty()) {//使用者沒有上傳新圖
			byte[] upFiles = actSvc.getOneAct(actVO.getActId()).getActPhoto(); //使用原本資料庫的圖片
			actVO.setActPhoto(upFiles);
		
		}else {//使用者有上傳新圖
			for (MultipartFile multipartFile: parts) {
				byte[] upFiles = multipartFile.getBytes();
			}
		}
		if (result.hasErrors()) { //如果其他欄位驗證格式錯誤，回到原頁面
			return "back-end/act/update_act_input";
		}
		/**格式驗證無誤，開始修改資料**/
		actSvc.updateAct(actVO);
		
		/**修改成功，回到listOneAct.html <label th:text="${success}"></label>**/
		model.addAttribute("success", "- (修改成功)");
		actVO = actSvc.getOneAct(Integer.valueOf(actVO.getActId()));
		model.addAttribute("actVO", actVO);
		return "back-end/act/listOneAct";
	}
	
	//點選刪除按鈕
	@PostMapping("delete")
	public String delete(@RequestParam("actId") String actId, ModelMap model) {
		/***接收參數，在listAllAct.html，刪除actId資料***/
		actSvc.deleteAct(Integer.valueOf(actId));
		
		/***刪除完成，回到listAllAct.html***/
		List<ActVO> list = actSvc.getAllActs();
		model.addAttribute("actListData", list);
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/act/listAllAct";
		
	}
	
	
//	//複合查詢
//	@PostMapping("listActs_ByCompositeQuery")
//	public String listAllAct(HttpServletRequest req, Model model) {
//		Map<String, String[]> map = req.getParameterMap();
//		List<ActVO> list = actSvc.getAllMap(map);
//		model.addAttribute("actListData", list); //for listAllAct.html
//		return "back-end/act/listAllAct";
//	}
//	
	
	//列出下拉選單-產生「所有店家」的下拉選單
	@ModelAttribute("storeListData")
	protected List<StoreVO> referenceListData(){
		List<StoreVO> list = storeSvc.getAll();
		return list;
	}
	
	//列出下拉選單- 產生「所有店家分類」的下拉選單
	@ModelAttribute("storeCateMap")
	protected Map<Integer, String> prepareStoreCateMap(){
		Map<Integer, String> map = new LinkedHashMap<>();
		
		List<StoreCateVO> cateList = storeCateSvc.getAll();
		for (StoreCateVO cate : cateList) {
			map.put(cate.getStorCateId(), cate.getStorCatName());
		}
		return map;
	}
	
	//列出下拉選單 - 上架/下架
	@ModelAttribute("actStatus")
	protected Map<Integer, String> referenceMapData(){
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(1, "下架");
		map.put(2, "上架");
		return map;
	}
	

	
	// 去除 BindingResult 中某個欄位的 FieldError 紀錄（例如不想驗證圖片欄位）
	// 傳入的參數：actVO：驗證對象   result：原本的驗證結果（包含所有錯誤） removeFieldname：要移除錯誤的欄位名稱（如 "html name=upFile"）
	private BindingResult removeFieldError(ActVO actVO, BindingResult result, String removeFieldname) {
		
		// 取得所有欄位的錯誤，但「過濾掉 removeFieldname 欄位」的錯誤  
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()//將List轉為資料流
				.filter(error -> !error.getField().equals(removeFieldname))//對每個FieldError做判斷，取得欄位名稱，如果這個欄位名稱不是 removeFieldname(傳入的是html name=upFile)，就保留（!equals()）
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(actVO, "actVO");// 建立一個新的 BindingResult，並與原本 actVO 綁定
		for (FieldError fieldError : errorsListToKeep) { // 把「保留下來」的錯誤重新加入新的 BindingResult 中
			result.addError(fieldError); // 回傳處理後的新 BindingResult（已移除指定欄位錯誤）
		}
		return result;
	}
	
}
