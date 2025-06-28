package com.foodietime.act.model;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.foodietime.product.model.ProductVO;

public enum ActCategory {
	    優惠活動((prod, rule) -> prod.getProdPrice() - 5),
	    新品上市((prod, rule) -> prod.getProdName().contains("草莓") ? (int)(prod.getProdPrice() * 0.9) : prod.getProdPrice()),
	    限時優惠((prod, rule) -> prod.getProdPrice() / 2),
	    會員日((prod, rule) -> (int)(prod.getProdPrice() * 0.85)),
	    飲品日((prod, rule) -> prod.getProductCategory().getProdCate().contains("飲料") ? prod.getProdPrice() / 2 : prod.getProdPrice()),
	    素食推薦((prod, rule) -> prod.getProductCategory().getProdCate().contains("素食") ? (int)(prod.getProdPrice() * 0.8) : prod.getProdPrice()),
	    速食優惠((prod, rule) -> prod.getProductCategory().getProdCate().contains("速食") ? (int)(prod.getProdPrice() * 0.75) : prod.getProdPrice()),
	    義式美食節((prod, rule) -> prod.getProdName().contains("義大利麵") ? (int)(prod.getProdPrice() * 0.9) : prod.getProdPrice()),
	    燒烤日((prod, rule) -> prod.getProdName().contains("烤") ? (int)(prod.getProdPrice() * 0.75) : prod.getProdPrice()),
	    火鍋季((prod, rule) -> prod.getProdName().contains("火鍋") ? prod.getProdPrice() - 50 : prod.getProdPrice());
	
	 private final BiFunction<ProductVO, String, Integer> discountStrategy;
	 
	 ActCategory(BiFunction<ProductVO, String, Integer> strategy) {
		 this.discountStrategy = strategy;
	 }
	 
	 public int calculate(ProductVO product, String rule) {
		 return discountStrategy.apply(product, rule);
	 }

}
