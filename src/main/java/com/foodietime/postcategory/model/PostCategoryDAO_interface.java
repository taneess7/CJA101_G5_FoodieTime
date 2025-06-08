package com.foodietime.postcategory.model;

import java.util.List;

public interface PostCategoryDAO_interface {
	
	public void insert(PostCategoryVO postcategoryVO);
	public void update(PostCategoryVO PostcategoryVO);
	public void delete(Integer postCateId);
	
	public PostCategoryVO findByPK(Integer postCateId);
	public List<PostCategoryVO> getAll();
	

}
