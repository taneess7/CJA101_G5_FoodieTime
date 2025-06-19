package com.foodietime.act.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.act.model.ActService;
import com.foodietime.act.model.ActVO;


@RestController //每個方法回傳物件自動轉成JSON
@RequestMapping("/api") //URL 前綴 GET /api/updateAllActPhotos
public class ActController {
	
	@Autowired
	private ActService actSvc;

	@GetMapping("/acts")
	public List<ActVO> getAllActs(){
		return actSvc.getAllActs();// Spring自動轉JSON
	}
	
	@PostMapping("/updateAllActPhotos") 
		public String updatePhotos(@RequestParam("files") MultipartFile[] files) throws IOException {
			actSvc.updateAllActPhotos(files); // 呼叫Service 邏輯
			return "成功上傳" + files.length + "張";
		}
	}


