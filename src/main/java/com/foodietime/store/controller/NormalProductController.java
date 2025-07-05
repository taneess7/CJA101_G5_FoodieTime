package com.foodietime.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodietime.product.model.ProductService;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreVO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class NormalProductController {

	@Autowired
	ProductService prodSvc;
	
	@GetMapping("/store/api/products")
	@ResponseBody
	public List<Map<String, Object>> getAllProducts(HttpSession session){
		StoreVO store = (StoreVO) session.getAttribute("loggedInStore");
		List<ProductVO> prods = prodSvc.findByStoreId(store.getStorId());
		
		return prods.stream().map(p->{
			Map<String, Object> map = new HashMap<>();
			 map.put("prodId", p.getProdId());
		        map.put("prodCate", p.getProductCategory().getProdCate());
		        map.put("prodName", p.getProdName());
		        map.put("prodDesc", p.getProdDesc());
		        map.put("prodPrice", p.getProdPrice());
		        map.put("prodStatus", p.getProdStatus());
		        map.put("prodLastUpdate", p.getProdLastUpdate());
		        return map;
		}).toList();
	}
}
