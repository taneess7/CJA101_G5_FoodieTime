package com.foodietime.act.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.act.model.ActCategoryEnum;
import com.foodietime.act.model.ActParticipationService;
import com.foodietime.act.model.ActParticipationVO;
import com.foodietime.act.model.ActService;
import com.foodietime.act.model.ActStoreDTO;
import com.foodietime.act.model.ActVO;
import com.foodietime.act.model.StoreSimpleDTO;
import com.foodietime.act.model.StoreWithDiscountedProductsDTO;
import com.foodietime.act.model.StoreWithDiscountedProductsDTO.ProductDiscountDTO;
import com.foodietime.store.model.StoreService;
import com.foodietime.store.model.StoreVO;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;



@RestController //每個方法回傳物件 Spring自動轉JSON
@RequestMapping("/api") //URL 前綴 GET /api/updateAllActPhotos
public class ActApiController {
	
	@Autowired
	private ActService actSvc;
	
	@Autowired
	private ActParticipationService actPartSvc;
	
	@Autowired
	private StoreService storeSvc;
	
	//確認是否有啟動api
//	@PostConstruct
//	public void init() {
//	    System.out.println("✅ ApiController 已啟動");
//	}
	
	//單一活動
	@GetMapping("/act/{id}")
	public ResponseEntity<ActVO> getActById(@PathVariable Integer id){
		ActVO act = actSvc.findById(id);
		if(act == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(act);
	}

	//進入可參加活動畫面 /api/allActs   http://localhost:8080/api/allActs 回傳所有api
	// 所有活動文字 Spring Boot 會自動轉成 JSON 並包裝為 200 OK
	
	/**取得所有活動**/
//	@GetMapping("/allActs")
//    public List<ActVO> getAllActs() {
//        return actSvc.getAllActs();      
//    }
	
	/**取得所有活動及圖片**/
	@GetMapping("/allActs")
	public List<Map<String, Object>> getAllActs() {
	    List<ActVO> acts = actSvc.getAllActs();
	    List<Map<String, Object>> result = new ArrayList<>();

	    for (ActVO act : acts) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("actId", act.getActId());
	        map.put("actName", act.getActName());
	        map.put("actContent", act.getActContent());
	        map.put("actStartTime", act.getActStartTime());
	        map.put("actEndTime", act.getActEndTime());
	        map.put("actDiscountType", act.getActDiscount());
	        map.put("actDiscountValue", act.getActDiscValue());

	        // ✅ 新增圖片網址（重點） api路徑要對,圖片才不會error
	        map.put("actPhoto", "/api/actpic/" + act.getActId());

	        result.add(map);
	    }

	    return result;
	}
	
	//顯示參與活動的店家 (dto) 沒有用到
	@GetMapping("/with-store")
    public List<ActStoreDTO> getActsWithStoreInfo() {
        return actSvc.getActsWithStores();
    }
	
//	/**參與活動的店家**/
//	@GetMapping("/act/{actId}/stores")
//	@ResponseBody
//	public List<StoreSimpleDTO> getStoresDTOsByActId(@PathVariable Integer actId){ //GET http://localhost:8080/act/5/stores : ，Integer actId = 5 
//	
//		 List<ActParticipationVO> list = actPartSvc.findStoresByActId(actId);
//		    return list.stream()
//		               .map(part -> new StoreSimpleDTO(
//		                     part.getStore().getStorId(),
//		                     part.getStore().getStorName()
//		               ))
//		               .collect(Collectors.toList());
//		}
//	
	@GetMapping("/act/{actId}/stores")
	@ResponseBody
	public List<StoreWithDiscountedProductsDTO> getStoresWithDiscountedProducts(@PathVariable Integer actId) {
	    ActVO act = actSvc.getOneAct(actId);
	    ActCategoryEnum cate = ActCategoryEnum.from(act.getActCate());
	    if (cate == null) return List.of();

	    List<ActParticipationVO> participants = actPartSvc.findStoresByActId(actId);

	    return participants.stream()
	        .map(part -> {
	            StoreVO store = storeSvc.getStoreWithProducts(part.getStore().getStorId());

	            List<ProductDiscountDTO> discounted = store.getProduct().stream()
	                .filter(p -> cate.calculate(p, act) < p.getProdPrice())
	                .map(p -> new ProductDiscountDTO(
	                        p.getProdName(),
	                        p.getProdPrice(),
	                        cate.calculate(p, act)
	                )).toList();

	            return new StoreWithDiscountedProductsDTO(store.getStorId(), store.getStorName(), discounted); //店家頁面跳轉要有storid http://localhost:8080/category/store/storId
	        })
	        .filter(dto -> !dto.getProducts().isEmpty()) // 只回傳有折扣商品的店家
	        .toList();
	}
	//前端載入更新過的活動列表提供給店家(需登入店家)
		@GetMapping("/store/activities")
		public List<Map<String, Object>> getActivitiesWithJoinStatus(HttpSession session) {
		    StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
		    if (store == null) return List.of(); // 如果沒登入就給空

		    List<ActVO> allActs = actSvc.getAllActs(); // 獲取所有活動
		   
		    List<ActParticipationVO> joinedActs = actPartSvc.findByStorId(store.getStorId()); // 獲取店家已參加的活動 ID 集合

		    //從joinedActs 中提取已參加的活動 ID
		    Set<Integer> joinedActIds = joinedActs.stream()
		    		.map(joinedAct -> joinedAct.getAct().getActId()) //提取活動ID
		    		.collect(Collectors.toSet());
		    
		    List<Map<String, Object>> result = new ArrayList<>();
		    
		    //遍歷所有活動並組裝結果	    		
		    for (ActVO act : allActs) {
		        Map<String, Object> map = new HashMap<>();
		        map.put("actId", act.getActId()); //顯示活動編號
		        map.put("actName", act.getActName()); //顯示活動名稱
		        map.put("actContent", act.getActContent()); // 顯示活動內容
		        map.put("actEndTime", act.getActEndTime()); // 顯示有效期限
		        map.put("isJoined", joinedActIds.contains(act.getActId())); // 判斷是否已參加，若已參加會回傳 true，否則 false
		        result.add(map);
		    }

		    return result;
		}
	
	
	//Spring 抓出3,傳參數id，網址請求 url: /api/actpic/3 , @GetMapping("actpic/{id})，變數綁定 @PathVariable 3
	/**載入圖片**/
	@GetMapping("/actpic/{id}")                
	public ResponseEntity<byte[]> getActPic(@PathVariable Integer id){
		byte[] picture = actSvc.getPhotoById(id);
		if(picture == null) {
			return ResponseEntity.notFound().build(); //錯誤處理，回傳404
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		headers.setContentLength(picture.length);
		return new ResponseEntity<>(picture, headers, HttpStatus.OK);
	}
	
	
	//不寫@RestController，手動處理JSON，搭配Service getAllActsToJSON()
//	@PostMapping("/allActs")
//	public ResponseEntity<String> getAllActs(){ //ResponseEntity<T> 是 包含狀態碼、Header、Body 的完整 HTTP 回應物件，<T>提供給前端的自訂泛型
//		String jsonStr = actSvc.getAllActsToJSON(); // 自己轉 JSON 字串
//		return ResponseEntity.ok()//HTTP/1.1 200 OK
//				.contentType(MediaType.APPLICATION_JSON)//Content-Type: application/json
//				.body(jsonStr);
//	}
	

	
	//更新活動照片
	@PostMapping("/updateAllActPhotos")
		public String updatePhotos(@RequestParam("files") MultipartFile[] files) throws IOException {
			actSvc.updateAllActPhotos(files); // 呼叫Service 邏輯
			return "成功上傳" + files.length + "張";
		}
	


	//測試1- 更新活動名稱(預設為全店家適用)
	@PutMapping("/acts/{id}")
	public String updateAct(@PathVariable Integer id, @RequestBody ActVO actVO) {
		return actSvc.updateStoreAct(id, actVO);
	}
	
	
	
	
	@PostMapping("/store/participate")
	@ResponseBody
	public ResponseEntity<String> participateInAct(@RequestBody Map<String, Object> data, HttpSession session) {
		//System.out.println("🟢 Controller 進來了！");
		Integer actId = (Integer) data.get("actId");
	    Boolean join = (Boolean) data.get("join"); // 你有送這個旗標，別忽略它

	    // ⛔ 防呆：一定要檢查 actId != null
	    if (actId == null) return ResponseEntity.badRequest().body("缺少活動 ID");

	    StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
	    if (store == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
	    }

	    ActVO act = actSvc.getOneAct(actId);
	    if (act == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("活動不存在");
	    }

	    // 🔁 檢查 join 是 true 就參加，false 就取消
	    if (Boolean.TRUE.equals(join)) {
	        // 儲存活動參與
	        ActParticipationVO participation = new ActParticipationVO();
	        participation.setStore(store);
	        participation.setAct(act);
	        participation.setJoinedTime(new Timestamp(System.currentTimeMillis()));
	        //System.out.println("✅ store id: " + store.getStorId());
	        //System.out.println("✅ act id: " + act.getActId());
	        actPartSvc.save(participation);
	        return ResponseEntity.ok("您已成功參加活動！");
	    } else {
	        // 刪除活動參與（你也可以加一個 service 方法）
	        actPartSvc.deleteByStoreAndAct(store.getStorId(), actId);
	        return ResponseEntity.ok(" ");//"您已取消參加活動！"
	    }
	}
	
	
}