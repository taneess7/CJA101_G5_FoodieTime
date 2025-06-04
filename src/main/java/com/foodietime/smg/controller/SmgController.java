package com.foodietime.smg.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/smg")
public class SmgController {
	@Autowired
	SmgService smgSvc;
	
	
	
}
