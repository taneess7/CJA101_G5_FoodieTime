package com.foodietime.product.model;

import java.util.List;

public interface ProductCategoryDAO_interface {
	public void insert(ProductCategoryVO productCategoryVO);
	public void update(ProductCategoryVO productCategoryVO);
	public void delete(Integer prod_Cate_Id);
	public ProductCategoryVO findByPrimaryKey(Integer prodCateId);
	public List<ProductCategoryVO> getAll();
}
