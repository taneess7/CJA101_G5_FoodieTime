package com.foodietime.store.controller;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.coupon.model.CouponService;
import com.foodietime.coupon.model.CouponVO;
import com.foodietime.product.model.ProductService;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;
import com.foodietime.storeCate.model.StoreCateService;
import com.foodietime.storeCate.model.StoreCateVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/*-------------------------------------------------------路徑還沒改成專案位置------------------------------------------------------*/
//@Controller
public class PageController {
	
	@Autowired
	private StoreService storeSvc;
	
	@Autowired
	private StoreCateService storeCateSvc;
	
	@Autowired
	private CouponService couponSvc;
	
	@Autowired
	private ProductService productSvc;


	 //測試 首頁 確認8080活著
    @GetMapping("/")
    public String home() {
        return "index"; // 找 templates/index.html 首頁
    }
    
    //測試 到下一頁
    @GetMapping("/goNext")
    public String goNextPage() {
        return "next"; // 找 templates/next.html
    }
    
    //測試 帶出使用者名稱
    @GetMapping("/index2")
    public String showIndex2(Model model) {
        model.addAttribute("username", "小新");
        return "index2";  // 對應 templates/index2.html
    }
    
    //歡迎招呼語- 示範
    @GetMapping("/storecoupon")
    public String showCoupon(Model model) {
    	StoreVO storeVO = storeSvc.findById(1);
        model.addAttribute("storeVO", storeVO);//model存入一筆 id=1 的店家資料
        return "storecoupon";  // 對應 templates/storecoupon.html
    }
    
    
/*-----------------------------------------------------------測試功能-------------------------------------------------------*/
    
  //主要測試頁面 store_edit2 -  簡單版店家頁面 - store_edit2 - 目前可自動撈資料
    @GetMapping("/store_edit2")
    public String goStoreEdit(Model model) {  //th:field="*{storName}" 一定要給model storeVO
    	StoreVO storeVO = new StoreVO(); // 從資料庫撈資料
    	storeVO = storeSvc.findById(2);  // 取出店家編號 ---- 這行控制進入網頁帶出的店家
        model.addAttribute("storeVO", storeVO); // 這一行必不可少！
        model.addAttribute("storCatNameList", storeCateSvc.getAll());
        //顯示預覽圖//
        if (storeVO.getStorPhoto() != null) {
            String base64Image = Base64.getEncoder().encodeToString(storeVO.getStorPhoto());
            model.addAttribute("base64Image", base64Image);
        } else {
            model.addAttribute("base64Image", ""); // 或放預設圖片連結
        }
        
        //撈取資料後回到原頁面//
        return "store_edit2"; // 找 templates/store_edit2  測試thymleaf 、圖片預覽、地址轉經緯度功能
    }
    
    
  
    
    //測試1-失敗- store_edit2 update  同時更新所有欄位，不能empty，綁定storeVO後炸掉
//    @PostMapping("/updateDayOff")
//    public String updateStore(@ModelAttribute StoreVO storeVO,@RequestParam("storWeeklyOffDay") String[] offDays) {
//        String result = String.join(",", offDays); // ex: "1,6"
//        storeVO.setStorOffDay(result);
//        storeSvc.updateStore(storeVO);
//        return "redirect:/index";
//    }
    
    //測試2-- store_edit2 update 只更新公休日欄位 -- 測試成功，使用前要先把Html action改成"@{/updateDayOff}"
    @PostMapping("/updateDayOff")
    public String updateDayOff(@RequestParam("storId")Integer storId,@RequestParam("storWeeklyOffDay") String[] offDays) {

        // 1️⃣ 從 Session 拿登入店家的 ID（或 Email）
//        Integer storId = (Integer) session.getAttribute("storId");
//        if (storId == null) {
//            return "redirect:/store/login"; // 尚未登入，導回登入頁
//        }
    	//Integer testStorId = 2; // 指定更改的店家id 不可以跟Get不同
    	
    	//1. 暫時先用Get指定的 ID（之後用 Session）  查出原本的店家資料
     	StoreVO storeVO = storeSvc.findById(storId);  

        //2. 更新公休日欄位（例如 "1,6"）
        String result = String.join(",", offDays);
        storeVO.setStorOffDay(result);

        //3 儲存回資料庫
        storeSvc.updateStore(storeVO);

        return "redirect:/"; // ✅ 導回8080測試頁  @GetMapping("/")
    }
    
    
  // 測試3-- store_edit2 update  只更新圖片欄位，無驗證功能 -- 圖片更新成功
    @PostMapping("/updatePhotoOnly")
       	public String updatePhoto(@RequestParam("storId") Integer storId,@RequestParam("upFiles") MultipartFile[] parts, Model model) throws IOException {
        // 1️⃣ 從 Session 拿登入店家的 ID（或 Email）
//        Integer storId = (Integer) session.getAttribute("storId");
//        if (storId == null) {
//            return "redirect:/store/login"; // 尚未登入，導回登入頁
//        }
    	//1. 暫時先用Get指定的 ID（之後用 Session）
    	StoreVO storeVO = storeSvc.findById(storId);
    	
       
        //2 處理圖片
        if (parts[0].isEmpty()) {
            // 沒選新圖片 → 保留原本圖片
            byte[] originalImage = storeVO.getStorPhoto();
            storeVO.setStorPhoto(originalImage);
        } else {
            // 有選圖片 → 使用新圖片
            byte[] newImage = parts[0].getBytes();
            storeVO.setStorPhoto(newImage);
        }

        //3. 儲存回資料庫
        storeSvc.updateStore(storeVO);

        return "redirect:/"; // ✅ 導回8080測試頁  @GetMapping("/")
    }
    
    
    //測試4--更新全部欄位
    @PostMapping("/updateAll")
    public String updateStore(
            @Valid @ModelAttribute("storeVO") StoreVO storeVO,
            BindingResult result,
            Model model,
            @RequestParam("upFiles") MultipartFile[] parts,
            @RequestParam("storWeeklyOffDay") String[] offDays // 公休日欄位：多選
    ) throws IOException {

        System.out.println("[後端進入 update]");
        System.out.println("接收到 ID：" + storeVO.getStorId());

        // 1. 若有驗證錯誤（來自 @NotBlank, @Email 等），就返回原頁
        if (result.hasErrors()) {
            model.addAttribute("storeVO", storeVO);
            model.addAttribute("error", "請檢查欄位輸入");
            model.addAttribute("storCatNameList", storeCateSvc.getAll());
            return "store_edit2"; // 下一頁
        }

        // 2. 處理圖片欄位（若未上傳新圖片，保留舊圖）
        if (parts[0].isEmpty()) {
            byte[] originalPhoto = storeSvc.findById(storeVO.getStorId()).getStorPhoto();
            storeVO.setStorPhoto(originalPhoto);
        } else {
            byte[] photo = parts[0].getBytes();
            storeVO.setStorPhoto(photo);
        }
        
        // 2.1 處理經緯度欄位（若為 null 則保留原本資料）
        StoreVO originalStore = storeSvc.findById(storeVO.getStorId());
        if (storeVO.getStorLat() == null) {
            storeVO.setStorLat(originalStore.getStorLat());
        }
        if (storeVO.getStorLon() == null) {
            storeVO.setStorLon(originalStore.getStorLon());
        }
        
       //放入所有類別 
        List<StoreCateVO> storCatNameList = storeCateSvc.getAll(); 
        model.addAttribute("storCatNameList", storCatNameList);
        
        // 3. 公休日多選 → 字串儲存（如 "1,6"）
        String joinedOffDays = String.join(",", offDays);
        storeVO.setStorOffDay(joinedOffDays);

        // 4. 更新
        storeSvc.updateStore(storeVO);

        return "redirect:/"; // 或你想回去的頁面
    }
    
  //測試5--新增商品圖片
//    @PostMapping("/uploadProducts")
//    public String uploadProducts(@RequestParam("prodimages") MultipartFile file) throws IOException {
//    	 Integer storId = 1; // 寫死測試用店家 ID
//    	// 把圖片陣列 files 和店家 ID storId 傳到 storeSvc 的 uploadProductPhoto方法裡
//    	 storeSvc.uploadProductPhotoByStorId(storId, file);
//    	 
//        return "redirect:/products"; // 跳轉商品頁
//    }
    
  //測試5-1--顯示商品頁
    @GetMapping("/products")
    	public String showProducts(Model model) { 
    	ProductVO productVO = productSvc.getProductById(1);
    	  model.addAttribute("productVO", productVO);
        return "products"; // 跳轉商品頁
    }
    	

    
    
    /*-----------------------------------------------------------整合頁面-------------------------------------------------------*/
    
    //店家整合編輯
    @GetMapping("/sc")
    public String editStoreCoupon(Model model){
    	StoreVO store = storeSvc.findById(1);
        model.addAttribute("storeVO", store);
        return "store_coupon_prodphoto";
    	
    }
   
    //店家彙總維護頁-複雜版-店家編輯- 包含 店家類別list - 可查看，無法更新資料
    @GetMapping("/store_edit")
    public String showEditForm(Model model) {
//        StoreVO storeVO = new StoreVO(); // 從資料庫撈資料
//        model.addAttribute("storeVO", storeVO); // 這一行必不可少！
        
        StoreVO store = storeSvc.findById(2);
        model.addAttribute("storeVO", store);
        
        List<StoreCateVO> storCatNameList = storeCateSvc.getAll(); //放入所有類別
        model.addAttribute("storCatNameList", storCatNameList);
        
       //取出公休日欄位（例如 "6" 或 "1,3,5"）給 checkbox
        model.addAttribute("storOffDay", store.getStorOffDay());
        
       //取出資料庫的圖檔
        if (store.getStorPhoto() != null) {
            String base64Image = Base64.getEncoder().encodeToString(store.getStorPhoto());
            model.addAttribute("base64Image", base64Image);
        } else {
            model.addAttribute("base64Image", ""); // 或放預設圖片連結
        }

        return "store_edit";
    }
    
    
    //泰式餐廳-未改
    @GetMapping("/thai")
    public String showTaiStore(Model model) {
    	List<StoreVO> thaiStores = storeSvc.getThaiRestaurants();
    	model.addAttribute("thaiStores", thaiStores);//存入多筆店家資料(一群VO)，傳給Thymleaf
    	return "thai-cuisine"; //找 templates/thai-cuisine.html
    }
    
    //泰式餐廳-改 - 可動態帶入資料庫店家
    @GetMapping("/thai2")
    public String showTaiStore2(Model model) {
    	List<StoreVO> thaiStores = storeSvc.getThaiRestaurants();
    	model.addAttribute("thaiStores", thaiStores);//存入多筆店家資料(一群VO)，傳給Thymleaf
    	return "thai-cuisine2"; //找 templates/thai-cuisine.html
    }
    
    //中式餐廳 - 未改，只有導頁功能
    @GetMapping("/chinese")
    public String showChineseTaiStore(Model model) {
//    	List<StoreVO> thaiStores = storeSvc.getThaiRestaurants();
//    	model.addAttribute("thaiStores", thaiStores);//存入多筆店家資料(一群VO)，傳給Thymleaf
    	return "chinese-cuisine"; //找 templates/thai-cuisine.html
    }
    
    //優惠券編輯頁
    @GetMapping("/coupon_edit")
    public String couponEdit(Model model) {
    	CouponVO couponVO = new CouponVO();
    	model.addAttribute("couponVO", couponVO);
    	return "coupon_edit";
    }
   
    //用session存id的方法
//    @GetMapping("/store_edit")
//    public String showEditForm(HttpSession session, Model model) {
//        Integer storeId = (Integer) session.getAttribute("storeId");
//        StoreVO storeVO = storeSvc.getOneStore(storeId);
//        model.addAttribute("storeVO", storeVO);
//        return "store_edit";
//    }

    
   
    
  //店家彙總維護頁，優惠券--還沒做
    @GetMapping("/cupon_edit")
    public String showCouponEditForm(Model model) {
        CouponVO couponVO = new CouponVO(); // 從資料庫撈資料
        model.addAttribute("couponVO", couponVO); // 這一行必不可少！
        
        //CouponVO coupon = couponSvc.findById(1);
        model.addAttribute("couponVO", couponVO);
        
//        List<StoreCateVO> storCatNameList = storeCateSvc.getAll(); //放入所有類別
//        model.addAttribute("storCatNameList", storCatNameList);

        return "coupon_edit";
    }
    
    
    
    
    
    //*---------------------------------老師的參考做法  新增 + 更新------------------------------------------------------------------------*//
    
    //儲存變更按鈕- 新增表單頁錯誤回傳，storeVO加回model 在畫面上回顯原輸入
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
			return "/store_edit"; // 輸入錯誤回到商家
		}

		/*************************** 2.開始新增資料 *****************************************/
		storeSvc.addStore(storeVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<StoreVO> list = storeSvc.getAll();
		model.addAttribute("storeListData", list);
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/store/listAllStores";
											
	}

	/*
	 * 修改店家資料 1.先查詢 查詢某一筆資料以供更新
	 */// 表示從 HTTP 請求的 參數中取得名為 storId 的值，並將這個值轉成 String 型別，放到名為 storId 的變數中使用
	@PostMapping("getOne_For_Update") // ModelMap Spring 提供的物件，讓你把資料傳到 JSP 頁面
	public String getOne_For_Update(@RequestParam("storId") String storId, ModelMap model) {
																							
																						
																							
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		StoreVO storeVO = storeSvc.getOneStore(Integer.valueOf(storId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("storeVO", storeVO);
		return "store_edit"; // 查詢完成後轉交
	}

	/* 修改店家資料 2.修改圖片 */
	@PostMapping("/update")
	public String update(@Valid @ModelAttribute("storeVO") StoreVO storeVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {
		System.out.println("[後端進入了 update 方法]");
		System.out.println("接收到的 storeId: " + storeVO.getStorId());
		System.out.println("接收到的 storeName: " + storeVO.getStorName());
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 
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
			return "redirect:/store_edit";
		}
		/*************************** 2.開始修改資料 *****************************************/

		storeSvc.updateStore(storeVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		storeVO = storeSvc.getOneStore(Integer.valueOf(storeVO.getStorId()));
		model.addAttribute("storeVO", storeVO);
		return "redirect:/store_edit";

	}
    public BindingResult removeFieldError(StoreVO storeVO, BindingResult result, String removedFieldname) {
    	
   			// 1. 把所有錯誤中，排除掉指定欄位的錯誤
   			List<FieldError> errorsListToKeep = result.getFieldErrors().stream().filter(fieldError -> !fieldError.getField().equals(removedFieldname)).collect(Collectors.toList());
    	
   			// 2. 建立一個新的 BindingResult，只保留其他欄位錯誤
   			result = new BeanPropertyBindingResult(storeVO, "storeVO");
    	
    			// 3. 把保留的錯誤加進新的 BindingResult
    			for (FieldError fieldError : errorsListToKeep) {
   				result.addError(fieldError);
   			}

   			return result;
    }  
    
 
}

