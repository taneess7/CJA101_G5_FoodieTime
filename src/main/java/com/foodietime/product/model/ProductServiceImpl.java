package com.foodietime.product.model;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class ProductServiceImpl implements ProductService{

	
	private final ProductRepository productRepo;
    private final ProductCategoryRepository categoryRepo;

    private List<Integer> allProductIds; // ✅ 美食轉盤使用的商品 ID 快取
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepo, ProductCategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    // ✅ 搜尋商品關鍵字
    @Override
    public List<ProductVO> searchProductsByKeyword(String keyword) {
        return productRepo.searchByKeyword(keyword);
    }

    // ✅ 取得店家所有商品
    @Override
    public List<ProductVO> findByStoreId(Integer storeId) {
        return productRepo.findByStore_StorId(storeId);
    }

    // ✅ 新增商品
    @Override
    public ProductVO addProduct(ProductVO vo, Integer categoryId) {
        ProductCategoryVO category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("找不到商品分類"));
        vo.setProductCategory(category);
        return productRepo.save(vo);
    }

    // ✅ 更新商品
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

    //美食轉盤  // ✅ 啟動時預載商品 ID（僅限上架商品）
    @PostConstruct
    public void init() {
        // 啟動時載入所有商品 ID
        allProductIds = productRepo.findAllProductIds();
    }

    @Override
    public ProductVO getRandomProduct() {
        if (allProductIds == null || allProductIds.isEmpty()) {
            return null;
        }
        int randomIndex = new Random().nextInt(allProductIds.size());
        Integer prodId = allProductIds.get(randomIndex);
        return productRepo.findById(prodId).orElse(null);
    }

// ============================================================================= //
   
    // ✅ 刪除商品
    @Override
    public void deleteProduct(Integer prodId) {
        productRepo.deleteById(prodId);
    }

    // ✅ 查單一商品
    @Override
    public ProductVO getProductById(Integer prodId) {
        return productRepo.findById(prodId).orElse(null);
    }

    // ✅ 查全部商品
    @Override
    public List<ProductVO> getAllProducts() {
        return productRepo.findAll();
    }
}
