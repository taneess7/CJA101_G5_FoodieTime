package com.foodietime.product.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository repo;

    @Autowired
    public ProductCategoryService(ProductCategoryRepository repo) {
        this.repo = repo;
    }

    // 修改分類
    public ProductCategoryVO updateCategory(Integer id, String newName) {
        Optional<ProductCategoryVO> optional = repo.findById(id);
        if (optional.isPresent()) {
            ProductCategoryVO vo = optional.get();
            vo.setProdCate(newName);
            return repo.save(vo);
        } else {
            throw new RuntimeException("找不到該分類");
        }
    }

    // 刪除分類
    public void deleteCategory(Integer id) {
        repo.deleteById(id);
    }

    // 查全部
    public List<ProductCategoryVO> getAllCategories() {
        return repo.findAll();
    }

    // 查單筆
    public ProductCategoryVO getCategoryById(Integer id) {
        return repo.findById(id).orElse(null);
    }

}
