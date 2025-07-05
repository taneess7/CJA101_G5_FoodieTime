package com.foodietime.act.model;

import java.util.List;

public class StoreWithDiscountedProductsDTO {
	private Integer storId;
	    private String storName;
	    private List<ProductDiscountDTO> products;

	    public StoreWithDiscountedProductsDTO(Integer storId, String storName, List<ProductDiscountDTO> products) {
	    	this.storId = storId;  //給參與店家頁面跳轉用
	    	this.storName = storName;
	        this.products = products;
	    }

	    
	    public Integer getStorId() {
	        return storId;
	    }
	    
	    public String getStorName() {
	        return storName;
	    }

	    public List<ProductDiscountDTO> getProducts() {
	        return products;
	    }

	    // 內部類別，僅在這個檔案中使用
	    public static class ProductDiscountDTO {
	        private String prodName;
	        private Integer prodPrice;
	        private Integer discountedPrice;

	        public ProductDiscountDTO(String prodName, Integer prodPrice, Integer discountedPrice) {
	            this.prodName = prodName;
	            this.prodPrice = prodPrice;
	            this.discountedPrice = discountedPrice;
	        }

	        public String getProdName() {
	            return prodName;
	        }

	        public Integer getProdPrice() {
	            return prodPrice;
	        }

	        public Integer getDiscountedPrice() {
	            return discountedPrice;
	        }
	    }
	}