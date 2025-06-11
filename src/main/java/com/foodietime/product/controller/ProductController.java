package com.foodietime.product.controller;

import com.foodietime.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 新增商品
//    @PostMapping
//    public ProductVO createProduct(@RequestBody ProductCreateRequest request) {
//        ProductVO vo = new ProductVO();
//        vo.setProdName(request.getProdName());
//        vo.setProdDesc(request.getProdDesc());
//        vo.setProdPrice(request.getProdPrice());
//        vo.setProdStatus(request.getProdStatus());
//        vo.setProdPhoto(request.getProdPhoto());
//        vo.setProdUpdateTime(request.getProdUpdateTime());
//        vo.setProdLastUpdate(request.getProdLastUpdate());
//        vo.setProdReportCount(request.getProdReportCount());
//        vo.setStorId(request.getStorId());
//
//        return productService.addProduct(vo, request.getProdCateId());
//    }

    // 修改商品
//    @PutMapping("/{id}")
//    public ProductVO updateProduct(@PathVariable Integer id, @RequestBody ProductCreateRequest request) {
//        ProductVO vo = new ProductVO();
//        vo.setProdName(request.getProdName());
//        vo.setProdDesc(request.getProdDesc());
//        vo.setProdPrice(request.getProdPrice());
//        vo.setProdStatus(request.getProdStatus());
//        vo.setProdPhoto(request.getProdPhoto());
//        vo.setProdUpdateTime(request.getProdUpdateTime());
//        vo.setProdLastUpdate(request.getProdLastUpdate());
//        vo.setProdReportCount(request.getProdReportCount());
//        vo.setStorId(request.getStorId());
//
//        return productService.updateProduct(id, vo, request.getProdCateId());
//    }

    // 查詢單一商品
    @GetMapping("/{id}")
    public ProductVO getProduct(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    // 查詢全部商品
    @GetMapping
    public List<ProductVO> getAllProducts() {
        return productService.getAllProducts();
    }

    // 刪除商品
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }
}
