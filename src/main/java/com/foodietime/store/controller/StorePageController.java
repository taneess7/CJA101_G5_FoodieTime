package com.foodietime.store.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodietime.act.model.ActVO;
import com.foodietime.cart.model.CartVO;
import com.foodietime.coupon.model.CouponService;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductService;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateService;
import com.foodietime.storeCate.model.StoreCateVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RequestMapping("/store")
@Controller
public class StorePageController {

	@Autowired
	private StoreService storeSvc;

	@Autowired
	private StoreCateService storeCateSvc;

	@Autowired
	private CouponService couponSvc;

	@Autowired
	private ProductService productSvc;
	
	
/*-----------------------------------------------------------整合頁面-------------------------------------------------------*/
    
    //店家整合編輯
   @GetMapping("/sc")
   public String editStoreCoupon(HttpSession session,Model model){
              // 取出登入會員
            MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
            if (member == null) {
                return "redirect:/front/member/login";
            }
            // 根據會員 Email 查出店家（你也可以用 memberId）
            StoreVO store = storeSvc.findByStorEmail(member.getMemEmail());  // ⚠️ 你要有這個方法
            if (store == null) {
                model.addAttribute("error", "查無對應店家");
                return "front/member/member_center";
            }
            model.addAttribute("storeVO", store);
            return "front/store/sc";
            
        }
    	
    	

/*------------------------------------------------------------------------------------------------------*/
	
    // 主要頁面 store_edit2 - 店家頁面 
	@GetMapping("/store_edit2")
	public String goStoreEdit(HttpSession session, Model model) { // th:field="*{storName}" 一定要給model storeVO
		 // 從 session 取得已登入店家
		StoreVO loggedInStore  = (StoreVO) session.getAttribute("loggedInStore");
		if (loggedInStore == null) {
			return "redirect:/front/member/login";
		}
		
		 // 用 storeId 從資料庫撈完整資料
//		StoreVO storeVO = new StoreVO(); // 從資料庫撈資料
//		Integer currentStorId = 5;
//		storeVO = storeSvc.findById(currentStorId); // 取出店家編號 ---- 這行控制進入網頁帶出的店家
		
		
		// 用 storeId 從資料庫撈完整資料
		Integer storId = loggedInStore.getStorId();
	    StoreVO storeVO = storeSvc.findById(storId);
	    
	    // 放進 model 以供 Thymeleaf 表單綁定使用
		model.addAttribute("storeVO", storeVO); // 這一行必不可少！
		model.addAttribute("storCatNameList", storeCateSvc.getAll());
		// 顯示預覽圖//
		if (storeVO.getStorPhoto() != null) {
			String base64Image = Base64.getEncoder().encodeToString(storeVO.getStorPhoto());
			model.addAttribute("base64Image", base64Image);
		} else {
			model.addAttribute("base64Image", "https://placehold.co/300x200/ffcc00/333?"); // 或放預設圖片連結
		}

		// 撈取資料後回到原頁面//
		return "front/store/store_edit2"; // 找 templates/store_edit2 測試thymleaf 、圖片預覽、地址轉經緯度功能
	}

	// 更新store全部欄位
	@PostMapping("/updateAll")
	public String updateStore(@Valid @ModelAttribute("storeVO") StoreVO storeVO, BindingResult result, Model model,
			@RequestParam("upFiles") MultipartFile[] parts, @RequestParam("storWeeklyOffDay") String[] offDays // 公休日欄位：多選
	) throws IOException {

		System.out.println("[後端進入 updateAll]");
		System.out.println("接收到 ID：" + storeVO.getStorId());

		// 1. 若有驗證錯誤（來自 @NotN @NotBlank, @Email 等），就返回原頁
		if (result.hasErrors()) {
			if (storeVO.getStorOffDay() == null) {
				storeVO.setStorOffDay(""); }// 轉空字串以避免 Thymeleaf contains 錯誤
			model.addAttribute("storeVO", storeVO);
			model.addAttribute("error", "請檢查欄位輸入");
			model.addAttribute("storCatNameList", storeCateSvc.getAll());
			System.out.println("hasErrors1");
			return "front/store/store_edit2"; 
		}

		// 2. 處理圖片欄位（若未上傳新圖片，保留舊圖）
		if (parts[0].isEmpty()) {
			byte[] originalPhoto = storeSvc.findById(storeVO.getStorId()).getStorPhoto();
			storeVO.setStorPhoto(originalPhoto);
			System.out.println("hasErrors2");
		} else {
			byte[] photo = parts[0].getBytes();
			storeVO.setStorPhoto(photo);
			System.out.println("圖片設置成功");
		}

		// 2.1 處理欄位（若為 null 則保留原本資料）
		// 先抓出原本的 Store 資料
		StoreVO originalStore = storeSvc.findById(storeVO.getStorId());
		// 保留經緯度
		if (storeVO.getStorLat() == null) {
			storeVO.setStorLat(originalStore.getStorLat());
		}
		if (storeVO.getStorLon() == null) {
			storeVO.setStorLon(originalStore.getStorLon());
		}

		// 保留檢舉數
		if (storeVO.getStorReportCount() == null) {
			storeVO.setStorReportCount(originalStore.getStorReportCount());
		}

		// 保留總星星數
		if (storeVO.getStarNum() == null) {
			storeVO.setStarNum(originalStore.getStarNum());
		}

		// 保留總評論數
		if (storeVO.getReviews() == null) {
			storeVO.setReviews(originalStore.getReviews());
		}
	

		// 放入所有類別
		List<StoreCateVO> storCatNameList = storeCateSvc.getAll();
		model.addAttribute("storCatNameList", storCatNameList);

		// 3. 公休日多選 → 字串儲存（如 "1,6"）
		String joinedOffDays = String.join(",", offDays);
		storeVO.setStorOffDay(joinedOffDays);

		// 4. 更新
		storeSvc.updateStore(storeVO);
		System.out.println("更新成功");
		return "redirect:/store/success";
	}
	
	 @GetMapping("/success")
	    public String home() {
		 System.out.println("進入成功頁");
	        return "front/store/success"; 
	    }
	 
	 
//============================優惠券編輯===============================//
	 //見CouponController
//============================甜點飲料餐廳===============================//

	@GetMapping("/desert2")
	public String showSwStore2(Model model) {

		List<StoreVO> swStores = storeSvc.getSwRestaurants();
		model.addAttribute("swStores", swStores);// 存入多筆店家資料(一群VO)，傳給Thymleaf
		System.out.println("✅ 撈到店家筆數：" + swStores.size());
		String[] weekdays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

		// ➤ 把店家圖片轉 base64 存成 Map<店家ID, 圖片Base64字串>
		Map<Integer, String> storeImageMap = new HashMap<>();
		for (StoreVO store : swStores) {
			if (store.getStorPhoto() != null) {
				String base64Image = Base64.getEncoder().encodeToString(store.getStorPhoto());
				storeImageMap.put(store.getStorId(), base64Image);
			} else {
				storeImageMap.put(store.getStorId(), "https://placehold.co/300x200/ffcc00/333?"); // 可改成預設圖連結
			}
		}
		model.addAttribute("storeImageMap", storeImageMap); // 傳給前端 HTML 使用

		// ➤ 把商品圖片轉 base64 存成 Map<店家ID, 圖片Base64字串>

		Map<Integer, List<ProductVO>> storeProductMap = new HashMap<>();
		Map<Integer, Map<Integer, String>> storeProdImageMap = new HashMap<>();

		for (StoreVO store : swStores) {
			Integer storId = store.getStorId();

			List<ProductVO> swProds = storeSvc.getProdsByStoreId(storId);
			storeProductMap.put(storId, swProds); // 商品清單

			Map<Integer, String> prodImageMap = new HashMap<>();
			for (ProductVO prod : swProds) {
				if (prod.getProdPhoto() != null) {
					String base64Image = Base64.getEncoder().encodeToString(prod.getProdPhoto());
					prodImageMap.put(prod.getProdId(), base64Image);
				} else {
					prodImageMap.put(prod.getProdId(), ""); // 預設圖
				}
			}
			storeProdImageMap.put(storId, prodImageMap); // 商品圖片 base64 map
		}
		// 傳給前端 HTML 使用
		model.addAttribute("storeProductMap", storeProductMap);
		model.addAttribute("storeProdImageMap", storeProdImageMap);

		// 優惠券Map<店家ID, List<CouponVO>>
		Map<Integer, List<CouponVO>> storeCouponMap = new HashMap<>();
		for (StoreVO store : swStores) {
			List<CouponVO> coupons = couponSvc.getCouponsByStorId(store.getStorId());
			storeCouponMap.put(store.getStorId(), coupons);
			System.out.println("➡️ 優惠券數量：" + coupons.size());
		}

		model.addAttribute("storeCouponMap", storeCouponMap); // 傳給前端 HTML 使用

//  //商品Map<店家ID, List<ProductVO>>
//    Map<Integer, List<ProductVO>> storeProductsMap = new HashMap<>();
//    for (StoreVO store : swStores) {
//   	 List<ProductVO> prods = productSvc.getProductsByStorId(store.getStorId());
//   	 storeProductsMap.put(store.getStorId(), prods);
//   	 System.out.println("➡️ 商品數量：" + prods.size());
//    }
//    
//    model.addAttribute("storeProductMap", storeProductsMap); // 傳給前端 HTML 使用

		return "front/restaurant/2/dessert-drinks2";
	}

	//============================編輯商品===============================//
	
/***進入商品編輯列表畫面***/
	@GetMapping("/listAllProds")
	public String listAllProds(Model model) { 
		return "front/store/prod/listAllProds";  //listAllActs.html
	}
	
/***進入商品新增畫面***/
		@GetMapping("/addAct")
		public String addAct(Model model) {
			ActVO actVO = new ActVO();
			model.addAttribute("actVO", actVO);//將actVO 傳給 HTML畫面使用
			actVO.setActSetTime(new Timestamp(System.currentTimeMillis()));
			return "admin/act/addAct"; //Thymeleaf 會去找 /templates/admin/act/addAct.html
		}
		
		//不讓 Spring 綁定TimeStamp欄位
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
		
		
/***商品新增功能，完成轉交給listAll***/
		@PostMapping("/insert")
		public String insert(
				@Valid ActVO actVO, BindingResult result, ModelMap model,
//				@RequestParam("storeCateId") Integer storeCateId,
				@RequestParam("upFiles") MultipartFile[] parts,RedirectAttributes redirectAttrs, HttpServletRequest request) throws IOException{  
			
			
			// ✅ <<< 這裡加上 debug log
			 System.out.println(">>> insert 方法有觸發");
			    System.out.println(">> 檢查 result.hasErrors() = " + result.hasErrors());
			    System.out.println(">> 圖片是否空 = " + (parts.length == 0 || parts[0].isEmpty()));
			    for (FieldError fe : result.getFieldErrors()) {
			        System.out.println("欄位錯誤: " + fe.getField() + " -> " + fe.getDefaultMessage());
			    }
			    

			/***接收請求參數，輸入格式錯誤處理***/
			 // 去除圖片欄位驗證錯誤
			result = removeFieldError(actVO, result, "upFiles"); //<input type="file" name="upFiles"> removeFieldError 不想因為「圖片未上傳」就不讓表單送出，排除圖片上傳欄位不被檢查
			 // 處理圖片上傳
			if (parts.length == 0 ||parts[0].isEmpty()) { //未選擇圖片，補驗證訊息
				model.addAttribute("errorMessage", "活動照片: 請上傳照片"); //addAct.html th:utext="${errorMessage}" 	
			} else {
				for (MultipartFile multipartFile : parts) {
					byte[] buf = multipartFile.getBytes(); //圖片轉byte[]
					actVO.setActPhoto(buf);
				}
			}

			
			 // 表單驗證不通過 → 回原頁
			if(result.hasErrors() || parts[0].isEmpty()) {
				List<FieldError> errors = result.getFieldErrors(); //FieldError 表單欄位單一BindingResult，使用傳給前端看錯誤
				return "admin/act/addAct"; //回到原頁顯示錯誤訊息
				
			}
		
			/**********表單驗證通過，開始新增資料************/
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
					
			/**********新增完成，準備轉交********/
			List<ActVO> list = actSvc.getAllActs();
			model.addAttribute("actListData", list);
			redirectAttrs.addFlashAttribute("success", "- (新增成功)"); //用redirectAttrs存成功訊息在listAllAct.html顯示
			return "redirect:/act/listAllAct"; //新增成功後發出HTTP302 重導，發出新的request 到ActPageController @GetMapping("/act/listAllAct")
			
		   }
/***進入商品修改畫面***/		
		//點選修改按鈕，進入後台修改畫面listOneAct.html
		@PostMapping("getOne_For_Update")
		public String getOne_For_Update(@RequestParam("actId") String actId, ModelMap model) {
			/***接收參數，進入進入修改畫面listOneAct.html，查詢actId資料***/
			ActVO actVO = actSvc.getOneAct(Integer.valueOf(actId));
			
			/***查詢完成，轉交 update_act_input.html***/
			model.addAttribute("actVO", actVO);
			return "admin/act/update_act_input";
		}

/***進入單一商品修改畫面，***/			
		//點選送出修改按鈕，檢查actVO欄位格式
		@PostMapping("update")
		public String update(@Valid ActVO actVO, BindingResult result, ModelMap model,
							 @RequestParam("upFiles") MultipartFile[] parts) throws IOException{
			
			// ✅ <<< 這裡加上 debug log
					 System.out.println(">>> update 方法有觸發");
					    System.out.println(">> 檢查 result.hasErrors() = " + result.hasErrors());
					    System.out.println(">> 圖片是否空 = " + (parts.length == 0 || parts[0].isEmpty()));
					    for (FieldError fe : result.getFieldErrors()) {
					        System.out.println("欄位錯誤: " + fe.getField() + " -> " + fe.getDefaultMessage());
					    }
					    
					    
			/**接收參數，驗證格式**/
			//去除BindingResult中 upFiles欄位的FieldError紀錄
					    /***接收請求參數，輸入格式錯誤處理***/
						 // 去除圖片欄位驗證錯誤
			result = removeFieldError(actVO, result, "upFiles"); //<input type="file" name="upFiles"> removeFieldError 不想因為「圖片未上傳」就不讓表單送出，排除圖片上傳欄位不被檢查
						 // 處理圖片上傳
			if (parts.length == 0 ||parts[0].isEmpty()) { //未選擇圖片，補驗證訊息
				model.addAttribute("errorMessage", "活動照片: 請上傳照片"); //addAct.html th:utext="${errorMessage}" 	
			} else {
						for (MultipartFile multipartFile : parts) {
							byte[] buf = multipartFile.getBytes(); //圖片轉byte[]
							actVO.setActPhoto(buf);
						}
					}
			if (result.hasErrors()) { //如果其他欄位驗證格式錯誤，回到原頁面
				return "admin/act/update_act_input";
			}
			/**格式驗證無誤，開始修改資料**/
			actSvc.updateAct(actVO);
			
			/**修改成功，回到listOneAct.html <label th:text="${success}"></label>**/
			model.addAttribute("success", "- (修改成功)");
			actVO = actSvc.getOneAct(Integer.valueOf(actVO.getActId()));
			model.addAttribute("actVO", actVO);
			return "admin/act/listOneAct";
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
			return "admin/act/listAllAct";
			
		}

}
