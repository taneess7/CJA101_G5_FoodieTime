package com.foodietime.act.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodietime.act.model.ActCategoryEnum;
import com.foodietime.act.model.ActParticipationService;
import com.foodietime.act.model.ActParticipationVO;
import com.foodietime.act.model.ActService;
import com.foodietime.act.model.ActVO;
import com.foodietime.product.model.ProductService;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateService;
import com.foodietime.storeCate.model.StoreCateVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/act")
public class AddUpdateController {

	@Autowired
	ActService actSvc;

	@Autowired
	StoreService storeSvc;

	@Autowired
	StoreCateService storeCateSvc;

	@Autowired
	ProductService prodSvc;

	@Autowired
	ActParticipationService actPartSvc;



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

	

	// 列出下拉選單-產生「所有店家」的下拉選單
	@ModelAttribute("storeListData")
	protected List<StoreVO> referenceListData() {
		List<StoreVO> list = storeSvc.getAll();
		return list;
	}
	
	// 列出下拉選單-產生「所有活動分類」的下拉選單
	@ModelAttribute("actCateMap")
	protected Map<Integer, String> prepareActCateMap(){
		Map<Integer, String> map = new LinkedHashMap<>();

		//只會回傳類別數字
//		List<ActVO> cateList = actSvc.getAllActs();
//		for (ActVO cate : cateList ) {
//			map.put(cate.getActId(), cate.getActCate());
//		}
		    map.put(1, "優惠活動");
		    map.put(2, "新品上市");
		    map.put(3, "限時優惠");
		    map.put(4, "會員日");
		    map.put(5, "飲品日");
		    map.put(6, "素食推廣");
		    map.put(7, "速食優惠");
		    map.put(8, "義式美食節");
		    map.put(9, "燒烤日");
		    map.put(10,"火鍋季");
		    return map;
		}




	// 列出下拉選單 - 上架/下架
	@ModelAttribute("actStatus")
	protected Map<Integer, String> referenceMapData() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "下架");
		map.put(1, "上架");
		return map;
	}

	// 列出下拉選單 - 百分比/金額
	@ModelAttribute("actDiscount")
	protected Map<Integer, String> referenceDiscountData() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "百分比折扣");
		map.put(1, "固定金額折扣");
		return map;
	}

	// 列出下拉選單 - 全店家/部分店家
	@ModelAttribute("isGlobal")
	protected Map<Integer, String> referenceStore() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "部分店家");// false
		map.put(1, "全店家");// true
		return map;
	}
	
//	//列出下拉選單- 產生「所有店家分類」的下拉選單
	@ModelAttribute("storeCateMap")
	protected Map<Integer, String> prepareStoreCateMap(){
		Map<Integer, String> map = new LinkedHashMap<>();
		
		List<StoreCateVO> cateList = storeCateSvc.getAll();
		for (StoreCateVO cate : cateList) {
			map.put(cate.getStorCateId(), cate.getStorCatName());
		}
		return map;
	}

	// 去除 BindingResult 中某個欄位的 FieldError 紀錄（例如不想驗證圖片欄位）
	// 傳入的參數：actVO：驗證對象 result：原本的驗證結果（包含所有錯誤） removeFieldname：要移除錯誤的欄位名稱（如 "html
	// name=upFile"）
	private BindingResult removeFieldError(ActVO actVO, BindingResult result, String removeFieldname) {

		// 取得所有欄位的錯誤，但「過濾掉 removeFieldname 欄位」的錯誤
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()// 將List轉為資料流
				.filter(error -> !error.getField().equals(removeFieldname))// 對每個FieldError做判斷，取得欄位名稱，如果這個欄位名稱不是
																			// removeFieldname(傳入的是html
																			// name=upFile)，就保留（!equals()）
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(actVO, "actVO");// 建立一個新的 BindingResult，並與原本 actVO 綁定
		for (FieldError fieldError : errorsListToKeep) { // 把「保留下來」的錯誤重新加入新的 BindingResult 中
			result.addError(fieldError); // 回傳處理後的新 BindingResult（已移除指定欄位錯誤）
		}
		return result;
	}




	/*****************活動前台************************************************************/
	// ========================================================
	@Bean(name = "actCategoryEnum")
	public ActCategoryEnum actCategoryEnum() {
		return ActCategoryEnum.MEMBER_DAY; // 隨便傳一個，目的是讓 Thymeleaf 認得 enum 類別
	}

	// ===============//店家點擊活動參加按鈕 GetMapping==========================
	// Controller 收到 actId 與 session中的StoreVO， 呼叫ActService 檢查是否符合活動條件
	// 讀取 enum 折扣策略
	// 有商品折扣後的價格 不等於 原價， 店家符合參加資格

	@GetMapping("/storeParticipateAct")
	public String participate(@RequestParam("actId") Integer actId, HttpSession session,
			RedirectAttributes redirectAttr) {

		StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
		ActVO act = actSvc.findById(actId);

		// 判斷資格(先不做)

//		boolean isEligible = actSvc.isStoreEligibleForAct(store, act);
//		if(!isEligible) {
//			redirectAttr.addFlashAttribute("errorMessage", "商品不符合參加此活動的資格");
//			return "redirect:/act";
//		}
		
		// 取得當前登入店家id（透過 Session）
		Integer storId = store.getStorId();
	
		// 判斷是否重複加入
		boolean alreadyJoined = actSvc.existsByStoreAndAct(storId, actId);
		if (alreadyJoined) {
			 redirectAttr.addFlashAttribute("errorMessage", "⚠️ 您已參加此活動，請勿重複加入");
			 return "redirect:/act";  // 看你要導去哪
		}

		// 尚未參加，進行參加流程
		actPartSvc.joinAct(store, act);

		redirectAttr.addFlashAttribute("successMessage", "已成功報名活動");
		return "redirect:/act";
	}

	// ===========================列出店家參加的活動及商品折扣========================================
	@GetMapping("/myActs")
	public String getMyJoinedActs(HttpSession session, Model model) {

		StoreVO storeInSession = (StoreVO) session.getAttribute("loggedInStore");
		if (storeInSession == null)
			return "redirect:/front/member/login";

		// 改這裡：透過 service 重新查詢含 product 的 store，避免lazy，session關閉無法取得prod
		StoreVO store = storeSvc.getStoreWithProducts(storeInSession.getStorId());

		List<ActParticipationVO> joinedList = actPartSvc.findByStorId(store.getStorId());

		// 資料結構：Map<ActVO, List<ProductVO>>，每個活動對應一組折扣商品
		Map<ActVO, List<ProductVO>> actProdMap = new LinkedHashMap<>();

		for (ActParticipationVO join : joinedList) {
			ActVO act = join.getAct();
			//System.out.println("📌 從資料庫抓到的 actCate = " + act.getActCate());
			ActCategoryEnum cate = ActCategoryEnum.from(act.getActCate()); // 抓出class enum 的提示錯誤
			//System.out.println("📌 解析對應到 enum = " + cate);  // 如果是 null 就代表解析失敗
			// 避免 NullPointerException
			if (cate == null)
				continue;

			// 過濾該店家所有商品，抓出有折扣的
			List<ProductVO> discProds = store.getProduct().stream()
					.filter(p -> cate.calculate(p, act) < p.getProdPrice()).toList();

			actProdMap.put(act, discProds);
		}

		model.addAttribute("actProdMap", actProdMap); // <div th:each="entry : ${actProdMap}">
		return "front/store/storeJoinedActs";
	}
	
	//===============//店家參加活動商品列表XXX沒有用到========================================================================//

		@GetMapping("/actProducts")
		public String showActProds(@RequestParam("actId") Integer actId, HttpSession session, Model model) {

			StoreVO store = (StoreVO) session.getAttribute("loggedInStore"); // 判斷是否登入店家

			// 比對一個全站活動
			ActVO globalAct = actSvc.getOneAct(actId); // 抓取點到的活動

			// 取得 enum 折扣邏輯
			ActCategoryEnum actCategory = ActCategoryEnum.from(globalAct.getActCate());

			// 顯示活動對應商品清單
			List<ProductVO> eligibleProds = store.getProduct().stream()
					.filter(p -> actCategory.calculate(p, globalAct) != p.getProdPrice()).collect(Collectors.toList());

			model.addAttribute("discProds", eligibleProds); // html <tr th:each="entry : ${discProds}">
			model.addAttribute("act", globalAct);
			return "front/store/store_actProducts";
		}
		
	
	/*****************活動後台************************************************************/
	
		
		// 進入新增活動後台畫面
		@GetMapping("/addAct")
		public String addAct(Model model) {
			ActVO actVO = new ActVO();
			model.addAttribute("actVO", actVO);// 將actVO 傳給 HTML畫面使用
			actVO.setActSetTime(new Timestamp(System.currentTimeMillis()));
			return "admin/act/addAct"; // Thymeleaf 會去找 /templates/admin/act/addAct.html
		}
		
		// 進入活動列表後台畫面
		@GetMapping("/listAllAct")
		public String listAllAct(Model model) {
		    List<ActVO> actList = actSvc.getAllActs();
		    model.addAttribute("actListData", actList);
		    return "admin/act/listAllActs"; // 對應 Thymeleaf 頁面路徑 templates/admin/act/listAllAct.html
		}
		


		
		// 新增功能 /Spring Boot 會根據 ActVO 裡面的欄位、型別、自動綁定畫面輸入的欄位，只要有一個欄位轉換失敗或缺少必要欄位，整個請求會拋出 400 錯誤。
		@PostMapping("/insert")
		public String insert(@Valid ActVO actVO, BindingResult result, ModelMap model,
				@RequestParam("storeCateId") Integer storeCateId,
				@RequestParam("actCate") String actCate, // ✅ 接中文字串,
				@RequestParam("upFiles") MultipartFile[] parts, RedirectAttributes redirectAttrs,
				HttpServletRequest request) throws IOException { //<select name="storId">	➜ 你要用request.getParameter("storId")，有用HttpServlet request取值就不用RequestParam()

			// ✅ <<< 這裡加上 debug log
			System.out.println(">>> insert 方法有觸發");
			System.out.println(">> 檢查 result.hasErrors() = " + result.hasErrors());
			System.out.println(">> 圖片是否空 = " + (parts.length == 0 || parts[0].isEmpty()));
			for (FieldError fe : result.getFieldErrors()) {
				System.out.println("欄位錯誤: " + fe.getField() + " -> " + fe.getDefaultMessage());
			}

//			/**使用storeCateId 查相關店家 **/
			List<StoreVO> stores = storeSvc.findByCateId(storeCateId);
			

			/*** 接收請求參數，輸入格式錯誤處理 ***/
			// 去除圖片欄位驗證錯誤
			result = removeFieldError(actVO, result, "upFiles"); // <input type="file" name="upFiles"> removeFieldError
																	// 不想因為「圖片未上傳」就不讓表單送出，排除圖片上傳欄位不被檢查
			// 處理圖片上傳
			if (parts.length == 0 || parts[0].isEmpty()) { // 未選擇圖片，補驗證訊息
				model.addAttribute("errorMessage", "活動照片: 請上傳照片"); // addAct.html th:utext="${errorMessage}"
			} else {
				for (MultipartFile multipartFile : parts) {
					byte[] buf = multipartFile.getBytes(); // 圖片轉byte[]
					actVO.setActPhoto(buf);
				}
			}

			// 表單驗證不通過 → 回原頁
			if (result.hasErrors() || parts[0].isEmpty()) {
				List<FieldError> errors = result.getFieldErrors(); // FieldError 表單欄位單一BindingResult，使用傳給前端看錯誤
				return "admin/act/addAct"; // 回到原頁顯示錯誤訊息

			}

			/********** 表單驗證通過，開始新增資料 ************/
			if (Boolean.TRUE.equals(actVO.getIsGlobal())) {
				actVO.setStorId(-1); // storId設虛擬值，表示全店通用
			} else {
				// 部分店家 → 正常選擇
				String storIdStr = request.getParameter("storId");
				if (storIdStr != null && !storIdStr.isEmpty()) {
					actVO.setStorId(Integer.valueOf(storIdStr));
				}
			}
			
			actSvc.addAct(actVO);

//			//儲存活動及關聯
//					actSvc.addAct(actVO);
//					for(StoreVO store : stores) {
//						actSvc.addAct(actVO.getActId(), store.getStorId());
//					}

			/********** 新增完成，準備轉交 ********/
			List<ActVO> list = actSvc.getAllActs();
			model.addAttribute("actListData", list);
			redirectAttrs.addFlashAttribute("success", "- (新增成功)"); // 用redirectAttrs存成功訊息在listAllAct.html顯示
			return "redirect:/act/listAllAct"; // 新增成功後發出HTTP302 重導，發出新的request 到ActPageController
												// @GetMapping("/act/listAllAct")

		}

		// 點選修改按鈕，進入後台修改畫面listOneAct.html
				@PostMapping("getOne_For_Update")
				public String getOne_For_Update(@RequestParam("actId") String actId, ModelMap model){
					/*** 接收參數，進入進入修改畫面listOneAct.html，查詢actId資料 ***/
					ActVO actVO = actSvc.getOneAct(Integer.valueOf(actId));// 從 DB 取出整筆資料

					/*** 查詢完成，轉交 update_act_input.html ***/
					model.addAttribute("actVO", actVO);//直接丟入 model，裡面已經有 actCate 字串欄位
					return "admin/act/update_act_input"; // 進入修改畫面
				}

		// 點選送出修改按鈕，檢查actVO欄位格式
		@PostMapping("update")
		public String update(@Valid ActVO actVO, BindingResult result, ModelMap model,
				@RequestParam("actCate") String actCate, // ✅ 接中文字串,
				@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

			// ✅ <<< 這裡加上 debug log
			System.out.println(">>> update 方法有觸發");
			System.out.println(">> 檢查 result.hasErrors() = " + result.hasErrors());
			System.out.println(">> 圖片是否空 = " + (parts.length == 0 || parts[0].isEmpty()));
			for (FieldError fe : result.getFieldErrors()) {
				System.out.println("欄位錯誤: " + fe.getField() + " -> " + fe.getDefaultMessage());
			}

			/** 接收參數，驗證格式 **/
			// 去除BindingResult中 upFiles欄位的FieldError紀錄
			/*** 接收請求參數，輸入格式錯誤處理 ***/
			// 去除圖片欄位驗證錯誤
			result = removeFieldError(actVO, result, "upFiles"); // <input type="file" name="upFiles"> removeFieldError
																	// 不想因為「圖片未上傳」就不讓表單送出，排除圖片上傳欄位不被檢查
			// 處理圖片上傳
			if (parts.length == 0 || parts[0].isEmpty()) { // 未選擇圖片，補驗證訊息
				model.addAttribute("errorMessage", "活動照片: 請上傳照片"); // addAct.html th:utext="${errorMessage}"
			} else {
				for (MultipartFile multipartFile : parts) {
					byte[] buf = multipartFile.getBytes(); // 圖片轉byte[]
					actVO.setActPhoto(buf);
				}
			}
			if (result.hasErrors()) { // 如果其他欄位驗證格式錯誤，回到原頁面
				return "admin/act/update_act_input";
			}
			/** 格式驗證無誤，開始修改資料 **/
			actSvc.updateAct(actVO);

			/** 修改成功，回到listOneAct.html <label th:text="${success}"></label> **/
			model.addAttribute("success", "- (修改成功) ");
			actVO = actSvc.getOneAct(Integer.valueOf(actVO.getActId()));
			model.addAttribute("actVO", actVO);
			return "admin/act/listOneAct";
		}

		// 點選刪除按鈕
		@PostMapping("delete")
		public String delete(@RequestParam("actId") String actId, ModelMap model) {
			/*** 接收參數，在listAllAct.html，刪除actId資料 ***/
			actSvc.deleteAct(Integer.valueOf(actId));

			/*** 刪除完成，回到listAllAct.html ***/
			List<ActVO> list = actSvc.getAllActs();
			model.addAttribute("actListData", list);
			model.addAttribute("success", "- (刪除成功)");
			return "admin/act/listAllAct";

		}
		
		// 複合查詢
		@PostMapping("listActs_ByCompositeQuery")
		public String listAllAct(HttpServletRequest req, Model model) {
			Map<String, String[]> map = req.getParameterMap();
			List<ActVO> list = actSvc.getAllMap(map);
			model.addAttribute("actListData", list); // for listAllAct.html
			return "admin/act/listAllAct";
		}
}
