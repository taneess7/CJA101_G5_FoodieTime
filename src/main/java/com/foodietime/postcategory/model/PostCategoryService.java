package com.foodietime.postcategory.model;

import java.util.List;

public class PostCategoryService {
	
	private PostCategoryDAO_interface dao;
	
	public PostCategoryService() {
		dao = new PostCategoryDAO();
	}
	public PostCategoryVO addPostCategory(Integer postCate) {
		PostCategoryVO postcategoryVO = new PostCategoryVO();
		postcategoryVO.setPostCate(postCate);
		dao.insert(postcategoryVO);
		return postcategoryVO;
		
	}
	
	public PostCategoryVO updatePostCategory(Integer postCateId, Integer postCate) {
		PostCategoryVO postcategoryVO = new PostCategoryVO();
		postcategoryVO.setPostCateId(postCateId);
		postcategoryVO.setPostCate(postCate);
		dao.update(postcategoryVO);
		return postcategoryVO;
	}
	
	public void deletePostCategory(Integer postCateId) {
		dao.delete(postCateId);
	}
	public PostCategoryVO getOnePostCategory(Integer postCateId) {
		return dao.findByPK(postCateId);
	}
	public List<PostCategoryVO> getAll(){
		return dao.getAll();
	}

}
