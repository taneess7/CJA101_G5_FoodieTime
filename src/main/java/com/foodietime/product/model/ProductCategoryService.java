package com.foodietime.product.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    private ProductCategoryRepository categoryRepo;
    private final StoreRepository storeRepo;
    
    @Autowired
	public ProductCategoryService(ProductCategoryRepository categoryRepo,StoreRepository storeRepo) {
		this.categoryRepo = categoryRepo;
		this.storeRepo = storeRepo;
	}

    public List<StoreVO> getStoresByCategoryId(Integer cateId) {
        return storeRepo.findByStoreCate_StorCateId(cateId);
    }
    
    // 美式餐廳 (可動態帶入資料庫店家 分類ID = 6)
//	public List<StoreVO> getAmericanRestaurants() {
//        return storeRepo.findByStoreCate_StorCateId(6);
//    }

    // 修改分類
    public ProductCategoryVO updateCategory(Integer id, String newName) {
        Optional<ProductCategoryVO> optional = categoryRepo.findById(id);
        if (optional.isPresent()) {
            ProductCategoryVO vo = optional.get();
            vo.setProdCate(newName);
            return categoryRepo.save(vo);
        } else {
            throw new RuntimeException("找不到該分類");
        }
    }

    // 刪除分類
    public void deleteCategory(Integer id) {
        categoryRepo.deleteById(id);
    }

    // 查全部
    public List<ProductCategoryVO> getAllCategories() {
        return categoryRepo.findAll();
    }

    // 查單筆
    public ProductCategoryVO getCategoryById(Integer id) {
        return categoryRepo.findById(id).orElse(null);
    }

}
