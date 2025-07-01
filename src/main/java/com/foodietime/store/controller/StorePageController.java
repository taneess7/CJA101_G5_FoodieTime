package com.foodietime.store.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.InputStream;
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

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.foodietime.act.model.ActParticipationVO;
import com.foodietime.act.model.ActVO;
import com.foodietime.coupon.model.CouponService;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductCategoryService;
import com.foodietime.product.model.ProductCategoryServiceImpl;
import com.foodietime.product.model.ProductCategoryVO;
import com.foodietime.product.model.ProductService;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateService;
import com.foodietime.storeCate.model.StoreCateVO;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	private ProductService prodSvc;
	
	@Autowired
	private ProductCategoryServiceImpl prodCateSvcImpl;
	

	
	
	
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
    	
    	

/*-----------------------------------------------------------編輯店家---------------------------------*/
	
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

	/*-----------------------------------------------------------編輯商品---------------------------------*/
	
/***進入商品列表畫面***/

	
	//撈出session店家的商品
	@GetMapping("/listAllProds")
	public String listAllProds(HttpSession session, Model model) { 
		//1.取得店家session
		StoreVO loggedInStore = (StoreVO) session.getAttribute("loggedInStore");
		if(loggedInStore == null) {
			return "redirect:/front/member/login";
		}
		Integer storId = loggedInStore.getStorId();
		
		//2.取得店家的商品
		List<ProductVO> list = prodSvc.findByStoreId(storId); //用storeId找商品，進入畫面才會有資料
//	    System.out.println("清單筆數：" + list.size());
	    model.addAttribute("prodListData", list); //提供給html
	    return "front/store/prod/listAllProds";
	}
	
	//測試指定店家
//	@GetMapping("/listAllProds")
//	public String listAllProds(Model model) { 
////		//1.取得店家session
////		StoreVO loggedInStore = (StoreVO) session.getAttribute("loggedInStore");
////		if(loggedInStore == null) {
////			return "redirect:/front/member/login";
////		}
//		Integer storId = 5;
//		
//		//2.取得店家的商品
//		List<ProductVO> list = prodSvc.findByStoreId(storId); //用storeId找商品，進入畫面才會有資料
//	    //System.out.println("店家id:"+ storId +"清單筆數：" + list.size());
//	    model.addAttribute("prodListData", list); //提供給html
//	    return "front/store/prod/listAllProds";
//	}
	
	
	
	//列出下拉選單 - 上架/下架
		@ModelAttribute("prodStatus")
		protected Map<Boolean, String> referenceMapData(){
			Map<Boolean, String> map = new LinkedHashMap<>();
			map.put(Boolean.FALSE, "下架");
			map.put(Boolean.TRUE, "上架");
			return map;
		}
		
		// 僅供下拉選單使用
		@ModelAttribute("prodListOptionList")
		protected List<ProductVO> referenceListData_Prod(){
			return prodSvc.getAllProducts();
		}
		
//		<select th:field="*{prodVO.prodId}">
//			<option th:each="p : ${prodListOptionList}" th:value="${p.prodId}" th:text="${p.prodName}"></option>
//		</select>
		
		//所有店家的清單（for 下拉選單）
		@ModelAttribute("storeListData")
		protected List<StoreVO> referenceListData_Store(Model model){
			model.addAttribute("storeVO", new StoreVO());
			return storeSvc.getAll();
		}
		
		
		//所有商品類別的清單（for 下拉選單）
		@ModelAttribute("prodCateListData")
		protected List<ProductCategoryVO> referenceListData_prodCate(Model model){
			model.addAttribute("prodCate", new ProductCategoryVO());
			return prodCateSvcImpl.getAllCategories();
	   }

	
//		<select th:field="*{storeVO.storId}"> 一定要 new 一個 VO 放進 model
//		  <option th:each="storeVO : ${storeListData}" th:value="${storeVO.storId}" th:text="${storeVO.storName}"></option>
//		</select>
		
		
		//所有商品類別的清單（for 下拉選單）
		@ModelAttribute("prodCateListData")
		protected List<ProductCategoryVO> referenceListData_prodCate(Model model){
			model.addAttribute("prodCate", new ProductCategoryVO());
			return prodCateSvcImpl.getAllCategories();
	   }
		
/***進入商品新增畫面***/
		@GetMapping("/addProd")
		public String addProd(Model model) {
			ProductVO prodVO = new ProductVO();
			model.addAttribute("prod", prodVO);//將actVO 傳給 HTML畫面使用
			prodVO.setProdUpdateTime(new Timestamp(System.currentTimeMillis()));
			prodVO.setProdLastUpdate(new Timestamp(System.currentTimeMillis()));
			return "front/store/prod/prod_edit"; //Thymeleaf 會去找 /templates/admin/act/addAct.html
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
		
		
		/***商品新增功能:完成轉交給listAll***/
		@PostMapping("/insert")
		public String insert( 
						@Valid ProductVO prodVO, BindingResult result, ModelMap model,
						@RequestParam("upFiles") MultipartFile[] parts,HttpSession session,
						RedirectAttributes redirectAttrs, HttpServletRequest request) throws IOException{  //顯示新增成功訊息 
			System.out.println(">>> insert方法觸發");

			// 從 session 中取出店家資訊
		    Object obj = session.getAttribute("loggedInStore");

		    if (!(obj instanceof StoreVO)) {
		        System.out.println("⚠️ session 中的 loggedInStore 無效，導回登入頁");
		        return "redirect:/front/member/login";
		    }

		    StoreVO loggedInStore = (StoreVO) obj;
		    Integer storId = loggedInStore.getStorId();

		 // ✅ <<< 這裡加上 debug log
			 System.out.println(">>> insert 方法有觸發");
			    System.out.println(">> 檢查 result.hasErrors() = " + result.hasErrors());
			    System.out.println(">> 圖片是否空 = " + (parts.length == 0 || parts[0].isEmpty()));
			    for (FieldError fe : result.getFieldErrors()) {
			        System.out.println("欄位錯誤: " + fe.getField() + " -> " + fe.getDefaultMessage());
			    }
			    
			   
			//如果驗證錯誤，補上 store 與 商品類別下拉選單
			if (result.hasErrors()) {
				for (FieldError fe : result.getFieldErrors()) {
					System.out.println("欄位錯誤" + fe.getField() + "->" + 
				    fe.getDefaultMessage());
				}
				// 若驗證失敗，要補上 store 進去（否則會是 null）
				
		        if (prodVO.getStore() == null) {
		            StoreVO storeVO = new StoreVO();
		            storeVO.setStorId(storId);
		            prodVO.setStore(storeVO);
		        }
		        
		        //取得店家單一商 prod
		        model.addAttribute("prod", prod); //單一商品資料（填表用）
		        
		        //取得該店家所有prods
				List<ProductVO> prods = prodSvc.findByStoreId(storId);
				model.addAttribute("prods", prods);//所有商品（若前端有顯示商品列表）
				
				//下拉選單自動選中分類
				Integer categoryId = prod.getProductCategory().getProdCateId();
				model.addAttribute("prodCate", categoryId);//// 這可選填，若前端用 th:field 就不需要
				
				//取得該店家所有prodCateList
				List<ProductCategoryVO> prodCateList = prodCateSvcImpl.getAllCategories();
				model.addAttribute("prodCateList", prodCateList);
				
				//顯示驗證錯誤訊息:
				List<String> errorMessages = result.getFieldErrors()
	                    .stream()
	                    .map(FieldError::getDefaultMessage)
	                    .collect(Collectors.toList());

			    model.addAttribute("errorMessages", errorMessages);
				return "front/store/prod_edit";
			}

			
			
			// 新增 or 修改分流（判斷是否有 prodId）
			if (prod.getProdId() == null) {
				System.out.println(">>> 進行新增");
				// 儲存商品
				prodSvc.addProduct(prod, prod.getProductCategory().getProdCateId());
				//service: ProductVO addProduct(ProductVO vo, Integer categoryId);
				//提示新增成功
				redirectAttr.addFlashAttribute("successMessage", "優惠券新增成功！");
				return "redirect:/store/addProd"; // 儲存成功重新導向 GET /store/addProd 方法，，避免重複提交

			} else {
				System.out.println(">>> 進行修改，prodId: " + prod.getProdId());
				//修改商品
				prodSvc.updateProduct(prod.getProdId(), prod, prod.getProductCategory().getProdCateId());
				//service:  ProductVO updateProduct(Integer prodId, ProductVO newData, Integer categoryId);
				redirectAttr.addFlashAttribute("successMessage", "優惠券修改成功！");
				// 修改成功後回到該筆資料的編輯頁面
				System.out.println("更新成功");
	            return "redirect:/store/edit_prod?prodId=" + prod.getProdId();
				

				
			}
			
		   }
/***進入商品修改畫面***/		
		//點選修改按鈕，進入後台修改畫面listOneProd.html
		@PostMapping("/update_prod_input")
		public String getOne_For_Update(@RequestParam("prodId") String prodId, ModelMap model) {
			/***接收參數，進入進入修改畫面listOneProd.html，查詢prodId資料***/
			ProductVO prodVO = prodSvc.getProductById(Integer.valueOf(prodId));
			
			/***查詢完成，轉交 update_prod_input.html***/
			model.addAttribute("prodVO", prodVO);
			return "front/store/prod/update_prod_input";
		}

/***進入單一商品修改畫面，***/			
		//點選送出修改按鈕，檢查prodVO欄位格式
		@PostMapping("update")
		public String update(@Valid ProductVO prodVO, BindingResult result, ModelMap model,
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
			result = removeFieldError(prodVO, result, "upFiles"); //<input type="file" name="upFiles"> removeFieldError 不想因為「圖片未上傳」就不讓表單送出，排除圖片上傳欄位不被檢查
						 // 處理圖片上傳
			if (parts.length == 0 ||parts[0].isEmpty()) { //未選擇圖片，補驗證訊息
				model.addAttribute("errorMessage", "商品照片: 請上傳照片"); //addProd.html th:utext="${errorMessage}" 	
			} else {
						for (MultipartFile multipartFile : parts) {
							byte[] buf = multipartFile.getBytes(); //圖片轉byte[]
							prodVO.setProdPhoto(buf);
						}
					}
			if (result.hasErrors()) { //如果其他欄位驗證格式錯誤，回到原頁面
				return "front/store/prod/update_prod_input";
			}
			/**格式驗證無誤，開始修改資料**/
			Integer prodId = prodVO.getProdId();            // 從 prodVO 取得商品編號
			Integer categoryId = prodVO.getProductCategory().getProdCateId(); // 從 prodVO 的關聯物件取得類別 ID

			prodSvc.updateProduct(prodId, prodVO, categoryId);//servic ProductVO updateProduct(Integer prodId, ProductVO newData, Integer categoryId);

			
			
			/**修改成功，回到listOneProd.html <label th:text="${success}"></label>**/
			model.addAttribute("success", "- (修改成功)");
			prodVO = prodSvc.getProductById(Integer.valueOf(prodVO.getProdId()));
			model.addAttribute("prodVO", prodVO);
			return "front/store/prod/listOneProd";
		}
		
		// 去除 BindingResult 中某個欄位的 FieldError 紀錄（例如不想驗證圖片欄位）
				// 傳入的參數：prodVO：驗證對象   result：原本的驗證結果（包含所有錯誤） removeFieldname：要移除錯誤的欄位名稱（如 "html name=upFile"）
				private BindingResult removeFieldError(ProductVO prodVO, BindingResult result, String removeFieldname) {
					
					// 取得所有欄位的錯誤，但「過濾掉 removeFieldname 欄位」的錯誤  
					List<FieldError> errorsListToKeep = result.getFieldErrors().stream()//將List轉為資料流
							.filter(error -> !error.getField().equals(removeFieldname))//對每個FieldError做判斷，取得欄位名稱，如果這個欄位名稱不是 removeFieldname(傳入的是html name=upFile)，就保留（!equals()）
							.collect(Collectors.toList());
					result = new BeanPropertyBindingResult(prodVO, "prodVO");// 建立一個新的 BindingResult，並與原本 prodVO 綁定
					for (FieldError fieldError : errorsListToKeep) { // 把「保留下來」的錯誤重新加入新的 BindingResult 中
						result.addError(fieldError); // 回傳處理後的新 BindingResult（已移除指定欄位錯誤）
					}
					return result;
				}
				
				
	//============================ListAllData顯示圖片================================================
				@GetMapping("DBGifReader")
				public void dBGifReader(@RequestParam("prodId") String prodId, HttpServletRequest req, HttpServletResponse res)
						                                                                                          throws IOException {
					res.setContentType("image/gif");
					ServletOutputStream out = res.getOutputStream();

					try {
						out.write(prodSvc.getProductById(Integer.valueOf(prodId)).getProdPhoto());
					} catch (Exception e) {
							
						InputStream is = new ClassPathResource("static/images/act/default.png").getInputStream();
						out.write(is.readAllBytes());
					}
				}
				
				//InputStream is = req.getServletContext().getResourceAsStream("/act/default.png");
				//src/main/webapp/act/default.png， 從專案部署的根目錄 /webapp 開始找，找不到 → null
		//============================點選刪除按鈕===========================================
		@PostMapping("delete")
		public String delete(@RequestParam("prodId") String prodId, ModelMap model) {
			/***接收參數，在listAllProd.html，刪除prodId資料***/
			prodSvc.deleteProduct(Integer.valueOf(prodId));
			
			/***刪除完成，回到listAllProd.html***/
			List<ProductVO> list = prodSvc.getAllProducts();
			model.addAttribute("prodListData", list);
			model.addAttribute("success", "- (刪除成功)");
			return "front/store/listAllProds";
			
		}
		
		//===========================進入商品列表，撈出所有店家的商品=======================================
		
//		@GetMapping("/listAllStoreProds")
//		public String listAllProds(Model model) { 
//			List<ProductVO> list = prodSvc.getAllProducts(); //一定要加，進入畫面才會有資料
//		    System.out.println("清單筆數：" + list.size());
//		    model.addAttribute("prodListData", list); //一定要加，進入畫面才會有資料
//		    return "front/store/prod/listAllProds";
//		}
		
		

		
		

}
