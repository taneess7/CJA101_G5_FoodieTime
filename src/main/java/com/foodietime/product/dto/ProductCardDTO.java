package com.foodietime.product.dto;

import lombok.Data;

@Data
public class ProductCardDTO {

    private Integer prodId;
    private String prodName;
    private String prodDesc;
    private Integer prodPrice;
    // 可以直接包含關聯物件的欄位
    private String storeName;
    private String categoryName;

    // ★★【關鍵】建立一個接收所有欄位的建構子 ★★
    // JPA 將會使用這個建構子來直接創建 DTO 物件
    public ProductCardDTO(Integer prodId, String prodName, String prodDesc, Integer prodPrice, String storeName, String categoryName) {
        this.prodId = prodId;
        this.prodName = prodName;
        this.prodDesc = prodDesc;
        this.prodPrice = prodPrice;
        this.storeName = storeName;
        this.categoryName = categoryName;
    }

    // Getters and Setters...
}