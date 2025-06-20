package com.foodietime.product.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.store.model.StoreRepository;
import com.foodietime.store.model.StoreVO;

@Service
public class ProductService {

	private final ProductRepository productRepo;
	private final ProductCategoryRepository categoryRepo;
//	private final StoreRepository storeRepo;

	@Autowired
	public ProductService(ProductRepository productRepo, ProductCategoryRepository categoryRepo) {
		this.productRepo = productRepo;
		this.categoryRepo = categoryRepo;
//		this.storeRepo = storeRepo;
	}

	// 新增商品
	public ProductVO addProduct(ProductVO vo, Integer categoryId) {
		ProductCategoryVO category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("找不到商品分類"));

		vo.setProductCategory(category);
		return productRepo.save(vo);
	}

	// 修改商品
	public ProductVO updateProduct(Integer prodId, ProductVO newData, Integer categoryId) {
		ProductVO existing = productRepo.findById(prodId).orElseThrow(() -> new RuntimeException("找不到商品"));

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

	// 刪除商品
	public void deleteProduct(Integer prodId) {
		productRepo.deleteById(prodId);
	}

	// 查詢單筆商品
	public ProductVO getProductById(Integer prodId) {
		return productRepo.findById(prodId).orElse(null);
	}

	// 查詢全部商品
	public List<ProductVO> getAllProducts() {
		return productRepo.findAll();
	}
}
