package com.foodietime.store.controller;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayInputStream;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.PathVariable;
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
		
		
//		// Base64顯示預覽圖太慢，改用專門回傳圖片的Controller//
//		if (storeVO.getStorPhoto() != null) {
//			String base64Image = Base64.getEncoder().encodeToString(storeVO.getStorPhoto());
//			model.addAttribute("base64Image", base64Image);
//		} else {
//			model.addAttribute("base64Image", "https://placehold.co/300x200/ffcc00/333?"); // 或放預設圖片連結
//		}

		// 撈取資料後回到原頁面//
		return "front/store/store_edit2"; // 找 templates/store_edit2 測試thymleaf 、圖片預覽、地址轉經緯度功能
	}
	
	

	//====================更新store全部欄位=========================//
	@PostMapping("/updateAll")
	public String updateStore(@Valid @ModelAttribute("storeVO") StoreVO storeVO, BindingResult result, Model model,
			@RequestParam("upFiles") MultipartFile[] parts, @RequestParam(value = "storWeeklyOffDay", required = false) String[] offDays // 公休日欄位：多選
	) throws IOException {

		System.out.println("[後端進入 updateAll]");
		System.out.println("接收到 ID：" + storeVO.getStorId());

		// 1. 若有驗證錯誤BindingResult.hasErrors() == true（來自 @NotN @NotBlank, @Email 等）， 執行 hasErrors1 區塊，回傳原畫面
		if (result.hasErrors()) {
			//修正: 驗證失敗也要處理公休日轉換
			if(offDays != null) {
				storeVO.setStorOffDay(String.join(",", offDays));
			}else {
				storeVO.setStorOffDay(""); //若沒選擇，仍要避免null
			}
			if (storeVO.getStorOffDay() == null) {
				storeVO.setStorOffDay(""); }// 轉空字串以避免 Thymeleaf contains 錯誤
			model.addAttribute("storeVO", storeVO);
			model.addAttribute("error", "請檢查欄位輸入");
			model.addAttribute("storCatNameList", storeCateSvc.getAll());
			System.out.println("hasErrors1"); // 驗證錯誤印這個
			return "front/store/store_edit2"; 
		}
        
		//表單資料正確 → hasErrors() == false 不會再進入 hasErrors1 區塊，直接往下執行
		// 2. 處理圖片欄位（若未上傳新圖片，保留舊圖）
		if (parts[0].isEmpty()) {
			byte[] originalPhoto = storeSvc.findById(storeVO.getStorId()).getStorPhoto();
			storeVO.setStorPhoto(originalPhoto);
			System.out.println("noError"); // 成功進入更新流程印這個
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

		// 3.驗證成功，公休日多選 → 字串儲存（如 "1,6"）
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
//============================甜點飲料餐廳 x沒有用到===============================//

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
					prodImageMap.put(prod.getProdId(), "https://placehold.co/200x200/eeeeee/999"); // 預設圖
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
			//System.out.println("➡️ 優惠券數量：" + coupons.size());
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
	//============================一般商品列表===============================//	
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
//		@ModelAttribute("prodListOptionList")
		protected List<ProductVO> referenceListData_Prod(){
			return prodSvc.getAllProducts();
		}
		
//		<select th:field="*{prodVO.prodId}">
//			<option th:each="p : ${prodListOptionList}" th:value="${p.prodId}" th:text="${p.prodName}"></option>
//		</select>
		
		//所有店家的清單（for 下拉選單）
//		@ModelAttribute("storeListData")
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
		
		//============================一般商品新增===============================//			
/***進入商品新增或編輯畫面***/
		 @GetMapping("/prod/success")
		    public String prodAddSuccess() {
			 System.out.println("進入成功頁");
		        return "front/store/prod/success"; 
		    }
		 
		@GetMapping("/prod/prodEdit")
		public String showEditPage(@RequestParam(required = false) Integer prodId, 
				HttpSession session, Model model) {
			
			//1.取得session 中的 loggedInStore，並檢查是否為StoreVO
			Object obj = session.getAttribute("loggedInStore");
			
			if(!(obj instanceof StoreVO)) {
			  System.out.println("⚠️ session 中的 loggedInStore 不是 StoreVO，而是：" + (obj == null ? "null" : obj.getClass().getSimpleName()));
			  return "redirect:/front/member/login"; //導回登入頁
			}
			
			StoreVO loggedInStore = (StoreVO) obj;
			Integer storId = loggedInStore.getStorId();
			
			//新增空白表單
			ProductVO prodVO;
			if(prodId != null) {
				prodVO = prodSvc.getProductById(prodId);//有商品id做修改功能，取得prodVO資料
			}else {
				prodVO = new ProductVO();//沒有商品id，新增商品
				//手動填入欄位預設值(vo.mysql沒有設定自動設定時間)
				prodVO.setProdPrice(0);
				prodVO.setProdUpdateTime(new Timestamp(System.currentTimeMillis()));
				prodVO.setProdLastUpdate(new Timestamp(System.currentTimeMillis()));
				
				StoreVO storeVO = new StoreVO();
				storeVO.setStorId(storId);//取得店家資料
				prodVO.setStore(storeVO);
			}
			
			//表單資料呈現，將prodVO 傳給 HTML畫面th:object=${prod}綁定vo			
			model.addAttribute("prod", prodVO);
			
			//填入商品分類清單
			model.addAttribute("prodCateList", prodCateSvcImpl.getAllCategories());

			// 3. 載入所有商品分類的列表
			model.addAttribute("prodCateList", prodCateSvcImpl.getAllCategories());
			//顯示預覽圖轉base64
			if (prodVO.getProdPhoto() != null) {
			    String base64 = Base64.getEncoder().encodeToString(prodVO.getProdPhoto());
			    model.addAttribute("base64Image", "data:image/jpeg;base64," + base64); // ✅這裡就加好
			} else {
			    model.addAttribute("base64Image", "https://placehold.co/300x200/ffcc00/333"); // ✅直接是URL
			}
			
			return "front/store/prod/prodEdit"; //回到prodEdit.html頁面
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
		
		/***查看商品列表選修改功能，按送出會進到這***/
		@PostMapping("/prod/save")
		public String saveProduct(
		    @Valid @ModelAttribute("prod") ProductVO prod, BindingResult result,
		    @RequestParam("categoryId") Integer categoryId,
		    @RequestParam("upFiles") MultipartFile[] parts,
		    HttpSession session,
		    Model model,
		    RedirectAttributes redirectAttr
		) throws IOException {
			System.out.println("圖片數量 = " + parts.length);
			System.out.println("圖片是否空 = " + (parts[0].isEmpty()));
			System.out.println("分類 id = " + categoryId);
			System.out.println("驗證錯誤？" + result.hasErrors());
			result.getAllErrors().forEach(e -> System.out.println("❗錯誤：" + e.getDefaultMessage()));
		    // 1️ 確認登入店家身份
		    StoreVO loggedInStore = (StoreVO) session.getAttribute("loggedInStore");
		    if (loggedInStore == null) {
		        return "redirect:/front/member/login";
		    }
		    Integer storId = loggedInStore.getStorId();

		    // 2️ 預處理分類與圖片欄位
		    setCategory(prod, categoryId);
		    setStore(prod, storId);
		 // !要在錯誤驗證前就設定圖片（呼叫你的方法），避免prod.getProdPhoto() 還是空的，導致圖片消失
	        handleImage(prod, parts, model);
	    
		    
		    // 3️ 驗證錯誤：回表單
		    if (result.hasErrors()) {
		        logValidationErrors(result);
		        model.addAttribute("prod", prod);
		        model.addAttribute("prodCateList", prodCateSvcImpl.getAllCategories());
		        model.addAttribute("errorMessages", extractErrorMessages(result));
		        
		        //設定圖片
		        if (parts != null && parts.length > 0 && !parts[0].isEmpty()) {
		            prod.setProdPhoto(parts[0].getBytes()); // 用新圖取代
		            model.addAttribute("base64Image", Base64.getEncoder().encodeToString(prod.getProdPhoto()));
		        } else if (prod.getProdPhoto() != null) {
		            // 否則顯示原圖
		            model.addAttribute("base64Image", Base64.getEncoder().encodeToString(prod.getProdPhoto()));
		        } else {
		            // 若沒有圖片，給預設
		            model.addAttribute("base64Image", "https://placehold.co/300x200/ffcc00/333?text=No+Image");
		        }
		        return "front/store/prod/prodEdit";
		    }

		    // 4️ 分流：新增 or 修改
		    if (prod.getProdId() == null) {
		        // ➕ 新增商品
		        prod.setProdLastUpdate(new Timestamp(System.currentTimeMillis()));
		        prod.setProdUpdateTime(new Timestamp(System.currentTimeMillis()));
		        prodSvc.addProduct(prod, categoryId);
		        redirectAttr.addFlashAttribute("successMessage", "商品新增成功！");
		        return "redirect:/store/prod/success";  //需重導其他名子的GetMapping，避免重複新增出現空白頁
		    } else {
		        // ✏️ 修改商品
		        result = removeFieldError(prod, result, "upFiles");
		        if (result.hasErrors()) {
		        	
		        	 //設定圖片
			        if (parts != null && parts.length > 0 && !parts[0].isEmpty()) {
			        	
			            prod.setProdPhoto(parts[0].getBytes()); // 用新圖取代
			            model.addAttribute("base64Image", Base64.getEncoder().encodeToString(prod.getProdPhoto()));
			        } else if (prod.getProdPhoto() != null) {
			            // 否則顯示原圖
			            model.addAttribute("base64Image", Base64.getEncoder().encodeToString(prod.getProdPhoto()));
			        } else {
			            // 若沒有圖片，給預設
			            model.addAttribute("base64Image", "https://placehold.co/300x200/ffcc00/333?text=No+Image");
			        }
		        	   
		        	 System.out.println("使用上傳圖片");
		        	
		        	
		        	model.addAttribute("prod", prod);
		        	model.addAttribute("prodCateList", prodCateSvcImpl.getAllCategories());
		        	model.addAttribute("errorMessages", extractErrorMessages(result));

		        	
		        	return "front/store/prod/prodEdit"; 
		            //return "redirect:/store/prod/prodEdit?prodId=" + prod.getProdId();
		        }
		        
		     // 處理商品圖片欄位（如果沒上傳新圖片，就保留原圖）
				if (parts[0].isEmpty()) {
				    byte[] originalPhoto = prodSvc.getProductById(prod.getProdId()).getProdPhoto(); // ← 取得原圖
				    prod.setProdPhoto(originalPhoto);  // ← 設定為原圖
				    System.out.println("圖片未更新，保留原圖");
				} else {
				    byte[] photo = parts[0].getBytes(); // ← 新圖轉 byte[]
				    prod.setProdPhoto(photo);           // ← 設定新圖
				    System.out.println("圖片更新成功");
				}
		        prod.setProdLastUpdate(new Timestamp(System.currentTimeMillis()));//必填
		        prod.setProdUpdateTime(new Timestamp(System.currentTimeMillis()));//必填
		        prodSvc.updateProduct(prod.getProdId(), prod, categoryId);
		        redirectAttr.addFlashAttribute("successMessage", "商品修改成功！");
		        return "redirect:/store/listAllProds";	 // 修改成功後回到該筆資料的listAll 
		    }
		}
	
		//把2的方法寫在這，方便維護
		private void setCategory(ProductVO prod, Integer categoryId) {
		    if (prod.getProductCategory() == null) {
		        ProductCategoryVO category = new ProductCategoryVO();
		        category.setProdCateId(categoryId);
		        prod.setProductCategory(category);
		    }
		}

		private void setStore(ProductVO prod, Integer storId) {
		    if (prod.getStore() == null) {
		        StoreVO storeVO = new StoreVO();
		        storeVO.setStorId(storId);
		        prod.setStore(storeVO);
		    }
		}

		private void handleImage(ProductVO prod, MultipartFile[] parts, Model model) throws IOException {
		    try {
		        if (parts != null && parts.length > 0 && !parts[0].isEmpty()) {
		            prod.setProdPhoto(parts[0].getBytes());  // ✅ 使用上傳新圖
		            System.out.println("使用上傳圖片");
		        } else if (prod.getProdPhoto() == null && prod.getProdId() != null) {
		            // 若沒有上傳新圖，且prod內也沒有圖→ 撈原圖
		            byte[] originalPhoto = prodSvc.getProductById(prod.getProdId()).getProdPhoto();
		            prod.setProdPhoto(originalPhoto);
		            System.out.println("沒上傳圖片，使用原圖");
		        }
		    } catch (IOException e) {
		        model.addAttribute("imageError", "圖片上傳失敗：" + e.getMessage());
		    }

		    if (prod.getProdPhoto() != null) {
		        model.addAttribute("base64Image", Base64.getEncoder().encodeToString(prod.getProdPhoto()));
		    } else {
		        model.addAttribute("base64Image", "https://placehold.co/300x200/ffcc00/333?text=No+Image");
		    }
		}

		private void logValidationErrors(BindingResult result) {
		    for (FieldError fe : result.getFieldErrors()) {
		        System.out.println("欄位錯誤：" + fe.getField() + " → " + fe.getDefaultMessage());
		    }
		}

		private List<String> extractErrorMessages(BindingResult result) {
		    return result.getFieldErrors().stream()
		            .map(FieldError::getDefaultMessage)
		            .collect(Collectors.toList());
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
		//====================專門回傳圖片的 Controller=========================//
		@GetMapping("/photo/{storId}") //店家照片
		public ResponseEntity<byte[]> getStorePhoto(@PathVariable Integer storId) {
			
		    byte[] photo = storeSvc.findById(storId).getStorPhoto();
		    if (photo == null) {
		        return ResponseEntity.notFound().build();
		    }

		    HttpHeaders headers = new HttpHeaders();
		    
		    //自動判斷圖片格式
		    String format = "jpeg"; //預設
		    
		    try {
		    	ByteArrayInputStream bais = new ByteArrayInputStream(photo);
		    	Iterator<ImageReader> readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(bais));
		    	if (readers.hasNext()) {
		    		format = readers.next().getFormatName().toLowerCase();//可能為 jpeg, png, gif...
		    	}
		    } catch(IOException e) {
		    	e.printStackTrace();
		    }
		    //根據格式設定 Content-Type
		    switch(format) {
		    case "png":
		    	headers.setContentType(MediaType.IMAGE_PNG);
		    	break;
		    case "gif":
		    	headers.setContentType(MediaType.IMAGE_GIF);
		    	break;
		    default:
		    	headers.setContentType(MediaType.IMAGE_JPEG);
		    	break;
		    }
		    
		   
		    return new ResponseEntity<>(photo, headers, HttpStatus.OK);
		}
		
		
		@GetMapping("/prod/photo/{prodId}")  //商品圖片
		public ResponseEntity<byte[]> getProdPhoto(@PathVariable Integer prodId) {
			ProductVO prod = prodSvc.getProductById(prodId);
			 if (prod == null || prod.getProdPhoto() == null) {
			        return ResponseEntity.notFound().build();
			    }

		    byte[] photo = prod.getProdPhoto();

		    HttpHeaders headers = new HttpHeaders();
		    
		    //自動判斷圖片格式
		    String format = "jpeg"; //預設
		    
		    try {
		    	ByteArrayInputStream bais = new ByteArrayInputStream(photo);
		    	Iterator<ImageReader> readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(bais));
		    	if (readers.hasNext()) {
		    		format = readers.next().getFormatName().toLowerCase();//可能為 jpeg, png, gif...
		    	}
		    } catch(IOException e) {
		    	e.printStackTrace();
		    }
		    //根據格式設定 Content-Type
		    switch(format) {
		    case "png":
		    	headers.setContentType(MediaType.IMAGE_PNG);
		    	break;
		    case "gif":
		    	headers.setContentType(MediaType.IMAGE_GIF);
		    	break;
		    default:
		    	headers.setContentType(MediaType.IMAGE_JPEG);
		    	break;
		    }
		    
		   
		    return new ResponseEntity<>(photo, headers, HttpStatus.OK);
		}
		//===========================進入商品列表，撈出所有店家的商品=======================================
		
//		@GetMapping("/listAllStoreProds")
//		public String listAllProds(Model model) { 
//			List<ProductVO> list = prodSvc.getAllProducts(); //一定要加，進入畫面才會有資料
//		    System.out.println("清單筆數：" + list.size());
//		    model.addAttribute("prodListData", list); //一定要加，進入畫面才會有資料
//		    return "front/store/prod/listAllProds";
//		}
		
		
		/***查看商品列表選修改功能，按送出會進到這，(測試時，商品按下新增有點問題，先不要用)***/
//		@PostMapping("/prod/save")
//		public String insert( 
//						@Valid
//						@ModelAttribute("prod") ProductVO prod,BindingResult result, ModelMap model,//給th"object=${prod}用
//						@RequestParam("categoryId") Integer categoryId,//商品類別清單用
//						@RequestParam("upFiles") MultipartFile[] parts,//圖片用 + throws IOException
//						HttpSession session,
//						RedirectAttributes redirectAttr) throws IOException{  //顯示新增成功訊息 
//			
//			System.out.println(">>> save方法觸發");
//			//System.out.println(">> 檢查 result.hasErrors() = " + result.hasErrors());
//		    //System.out.println(">> 圖片是否空 = " + (parts.length == 0 || parts[0].isEmpty()));
//
//			// 從 session 中取出店家資訊
//		    Object obj = session.getAttribute("loggedInStore");
//
//		    if (!(obj instanceof StoreVO)) {
//		        System.out.println("⚠️ session 中的 loggedInStore 無效，導回登入頁");
//		        return "redirect:/front/member/login";
//		    }
//
//		    StoreVO loggedInStore = (StoreVO) obj;
//		    Integer storId = loggedInStore.getStorId();
//		    
//		    //表單綁住分類，避免點修改ProductVO.getProductCategory()" is null
//		    if(prod.getProductCategory() == null) {
//		    	ProductCategoryVO category = new ProductCategoryVO();
//		    	category.setProdCateId(categoryId);
//		    	prod.setProductCategory(category);
//		    }
//
//		    //圖片處理: 有上傳圖片就存入(新增或修改)
//			if(parts!=null && parts.length > 0 && !parts[0].isEmpty()) {
//				prod.setProdPhoto(parts[0].getBytes());
//			}
//			
//			//顯示圖片預覽(如果有圖)
//			if(prod.getProdPhoto()!=null) {
//				model.addAttribute("base64Image",
//						Base64.getEncoder().encodeToString(prod.getProdPhoto()));	
//			}else {
//				model.addAttribute("base64Image", 
//						"https://placehold.co/300x200/ffcc00/333?");
//			}
//			   
//			//如果驗證錯誤，回到表單
//			if (result.hasErrors()) {
//				for (FieldError fe : result.getFieldErrors()) {
//					System.out.println("欄位錯誤" + fe.getField() + "->" + 
//				    fe.getDefaultMessage());
//				}
//				// 驗證失敗，一定要再補一次（分類）下拉選單避免null
//		        if (prod.getProductCategory() == null) {
//		        	ProductCategoryVO category = new ProductCategoryVO();
//		        	category.setProdCateId(categoryId);
//		        	prod.setProductCategory(category);
//		        }
//		        
//		        //補上 store
//		        if(prod.getStore() == null) {
//		        	StoreVO storeVO = new StoreVO();
//		        	storeVO.setStorId(storId);
//		        	prod.setStore(storeVO);
//		        }
//		        
//		        //取得店家單一商品prod
//		        model.addAttribute("prod", prod); //單一商品資料（填表用）
//		      
//		        
//		        //取得該店家所有prodCateList
//				List<ProductCategoryVO> prodCateList = prodCateSvcImpl.getAllCategories();
//				model.addAttribute("prodCateList", prodCateList);
//		        
//		        
//		        
//				 //取得該店家所有prods-html沒有用到
//				List<ProductVO> prods = prodSvc.findByStoreId(storId);
//				model.addAttribute("prods", prods);//所有商品（若前端有顯示商品列表）
//				
//				//下拉選單自動選中分類-html沒有用到
//				//Integer categoryId = prod.getProductCategory() != null ? prod.getProductCategory().getProdCateId() : null;
//			//model.addAttribute("prodCate", categoryId);//// 這可選填，若前端用 th:field 就不需要
//				
//				
//				
//				//顯示驗證錯誤訊息:
//				List<String> errorMessages = result.getFieldErrors()
//	                    .stream()
//	                    .map(FieldError::getDefaultMessage)
//	                    .collect(Collectors.toList());
//
//			    model.addAttribute("errorMessages", errorMessages);
//				return "front/store/prod/prodEdit"; //回到prodEdit.html
//			}
//
//			
//			
//			//驗證ok，分流: 新增 or 修改（判斷是否有 prodId）
//			if (prod.getProdId() == null) {
//				//System.out.println(">>> 進行新增");
//				// 補上分類資料（重要！）
//				if (prod.getProductCategory() == null) {
//			        ProductCategoryVO category = new ProductCategoryVO();
//			        category.setProdCateId(categoryId);
//			        prod.setProductCategory(category);
//			    }
//				prod.setStore(loggedInStore); //確保店家綁定正確
//				prod.setProdLastUpdate(new Timestamp(System.currentTimeMillis())); //補上非空值欄位
//				prod.setProdUpdateTime(new Timestamp(System.currentTimeMillis())); //補上非空值欄位
//				prodSvc.addProduct(prod, prod.getProductCategory().getProdCateId());
//				//service: ProductVO addProduct(ProductVO vo, Integer categoryId);
//				
//				//提示新增成功
//				redirectAttr.addFlashAttribute("successMessage", "商品新增成功！");
//				return "redirect:/store/prod/prodEdit";
//
//			} else {
//				//System.out.println(">>> 進行修改，prodId: " + prod.getProdId());
//				
//			
//				// 圖片非必填，去除圖片欄位驗證錯誤
//				result = removeFieldError(prod, result, "upFiles"); //<input type="file" name="upFiles"> removeFieldError 不想因為「圖片未上傳」就不讓表單送出，排除圖片上傳欄位不被檢查
//							 // 處理圖片上傳
//
//				if (result.hasErrors()) { //如果其他欄位驗證格式錯誤，回到原頁面
//					return "redirect:/store/prod/prodEdit?prodId=" + prod.getProdId();	
//				}
//				
//				// 處理商品圖片欄位（如果沒上傳新圖片，就保留原圖）
//				if (parts[0].isEmpty()) {
//				    byte[] originalPhoto = prodSvc.getProductById(prod.getProdId()).getProdPhoto(); // ← 取得原圖
//				    prod.setProdPhoto(originalPhoto);  // ← 設定為原圖
//				    System.out.println("圖片未更新，保留原圖");
//				} else {
//				    byte[] photo = parts[0].getBytes(); // ← 新圖轉 byte[]
//				    prod.setProdPhoto(photo);           // ← 設定新圖
//				    System.out.println("圖片更新成功");
//				}
//				prod.setProdLastUpdate(new Timestamp(System.currentTimeMillis())); //補上非空值欄位
//				prod.setProdUpdateTime(new Timestamp(System.currentTimeMillis())); //補上非空值欄位
//				prodSvc.updateProduct(prod.getProdId(), prod, prod.getProductCategory().getProdCateId());
//				//service:  ProductVO updateProduct(Integer prodId, ProductVO newData, Integer categoryId);
//				redirectAttr.addFlashAttribute("successMessage", "商品修改成功！");
//				
//				System.out.println("更新成功");
//				
////	            return "redirect:/store/prod/prodEdit?prodId=" + prod.getProdId();	 //停在編輯頁面
//				return "redirect:/store/listAllProds";	 // 修改成功後回到該筆資料的listAll 
//			  }
//		
//			}
//			
//		 
//
		
		

}
