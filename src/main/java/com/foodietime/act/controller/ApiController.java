package com.foodietime.act.controller;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.act.model.ActService;
import com.foodietime.act.model.ActVO;

import jakarta.annotation.PostConstruct;



@RestController //每個方法回傳物件 Spring自動轉JSON
@RequestMapping("/api") //URL 前綴 GET /api/updateAllActPhotos
public class ApiController {
	
	@Autowired
	private ActService actSvc;
	
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

	//對應 /api/allActs   http://localhost:8080/api/allActs 回傳所有api
	// 所有活動文字 Spring Boot 會自動轉成 JSON 並包裝為 200 OK
	@GetMapping("/allActs")
    public List<ActVO> getAllActs() {
        return actSvc.getAllActs(); 
    }
	
	//載入圖片 的 doGet("/bookpic/{id}")
	//Spring 抓出3,傳參數id，網址請求 url: /api/actpic/3 , @GetMapping("actpic/{id})，變數綁定 @PathVariable 3
	
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
	
	

}