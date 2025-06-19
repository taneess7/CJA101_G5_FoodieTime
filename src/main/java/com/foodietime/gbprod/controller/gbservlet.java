package com.foodietime.gbprod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodietime.gbprod.model.GbprodService;

@Controller
@RequestMapping("/gb-mem")
public class gbservlet {
	@Autowired
	GbprodService gbprodSvc;
	
	
	
	@GetMapping("/gb_index")
	public String showLoginPage(Model model) {
	    return "front/gb/gbindex"; // 對應到你的 Thymeleaf 登入頁面
	}
}
