package com.foodietime.post.model;

import java.util.List;

public interface PostDAO_interface {
	
	int insert(PostVO postVO);
	int update(PostVO postVO);
	int delete(Integer postId);
	
	public PostVO findByPK(Integer postId);
	public List<PostVO> getAll();
	

}
