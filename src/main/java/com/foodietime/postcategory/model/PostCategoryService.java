package com.foodietime.postcategory.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.post.model.PostRepository;

@Service("PostCategoryService")
public class PostCategoryService {
	
	@Autowired
	private PostCategoryRepository repository;
	
	public PostCategoryVO addPostCategory(String postCate) {
		PostCategoryVO postcategoryVO = new PostCategoryVO();
		postcategoryVO.setPostCate(postCate);
		
		return repository.save(postcategoryVO);
		
	}
	
	public PostCategoryVO updatePostCategory(Integer postCateId, String postCate) {
		PostCategoryVO postcategoryVO = new PostCategoryVO();
		postcategoryVO.setPostCateId(postCateId);
		postcategoryVO.setPostCate(postCate);
		return repository.save(postcategoryVO);
	}
	
	public void deletePostCategory(Integer postCateId) {
		repository.deleteById(postCateId);
	}
	public PostCategoryVO getOnePostCategory(Integer postCateId) {
		return repository.findById(postCateId).orElse(null);
	}
	public List<PostCategoryVO> getAll(){
		return repository.findAll();
	}

}
