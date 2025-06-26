package com.foodietime.product.model;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService{

	private final ProductRepository productRepo;
    private final ProductCategoryRepository categoryRepo;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepo, ProductCategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<ProductVO> searchProductsByKeyword(String keyword) {
        return productRepo.searchByKeyword(keyword);
    }

    @Override
    public List<ProductVO> findByStoreId(Integer storeId) {
        return productRepo.findByStore_StorId(storeId);
    }

    @Override
    public ProductVO addProduct(ProductVO vo, Integer categoryId) {
        ProductCategoryVO category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("找不到商品分類"));
        vo.setProductCategory(category);
        return productRepo.save(vo);
    }

    @Override
    public ProductVO updateProduct(Integer prodId, ProductVO newData, Integer categoryId) {
        ProductVO existing = productRepo.findById(prodId)
                .orElseThrow(() -> new RuntimeException("找不到商品"));
        ProductCategoryVO category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("找不到商品分類"));

        existing.setProductCategory(category);
        existing.setProdName(newData.getProdName());
        existing.setProdDesc(newData.getProdDesc());
        existing.setProdPrice(newData.getProdPrice());
        existing.setProdStatus(newData.getProdStatus());
        existing.setProdPhoto(newData.getProdPhoto());
        existing.setProdUpdateTime(newData.getProdUpdateTime());
        existing.setProdLastUpdate(newData.getProdLastUpdate());
        existing.setProdReportCount(newData.getProdReportCount());

        return productRepo.save(existing);
    }

    @Override
    public void deleteProduct(Integer prodId) {
        productRepo.deleteById(prodId);
    }

    @Override
    public ProductVO getProductById(Integer prodId) {
        return productRepo.findById(prodId).orElse(null);
    }

    @Override
    public List<ProductVO> getAllProducts() {
        return productRepo.findAll();
    }
}
