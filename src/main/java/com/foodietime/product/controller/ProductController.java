package com.foodietime.product.controller;

import com.foodietime.product.model.*;
import com.foodietime.store.model.StoreVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 根據商品 ID 獲取商品圖片。
     * 這個方法專門用來處理前端 <img src="..."> 的請求。
     *
     * @param prodId 商品ID，從 URL 路徑中獲取。
     * @return 返回包含圖片 byte 陣列的 ResponseEntity。如果找不到商品或圖片，返回 404。
     */
    @GetMapping("/image/{prodId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Integer prodId) {
        // ==================== 1. 根據 ID 查找商品實體 ====================
        ProductVO productVO = productService.getProductById(prodId);

        // ==================== 2. 檢查商品是否存在以及是否有圖片 ====================
        if (productVO == null || productVO.getProdPhoto() == null || productVO.getProdPhoto().length == 0) {
            // 如果商品不存在或沒有圖片，返回 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // ==================== 3. 準備 HTTP Headers ====================
        // 設定回傳的內容類型為圖片格式 (這裡假設為 JPEG，您可以根據實際情況調整)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        // ==================== 4. 回傳圖片數據 ====================
        // 將圖片的 byte[] 陣列、Headers 和 OK 狀態碼包裝成 ResponseEntity 回傳
        return new ResponseEntity<>(productVO.getProdPhoto(), headers, HttpStatus.OK);      
    }
    
    //美食轉盤
    @GetMapping("/roulette")
    @ResponseBody
    public Map<String, Object> getRandomProduct() {
        ProductVO product = productService.getRandomProduct();

        if (product == null) {
            return Map.of("error", "找不到商品");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("prodId", product.getProdId());
        result.put("prodName", product.getProdName());
        result.put("prodDesc", product.getProdDesc());

        if (product.getStore() != null) {
            result.put("storId", product.getStore().getStorId());
        }

        return result;
    }
}