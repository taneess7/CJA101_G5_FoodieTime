package com.foodietime.post.model;

import java.util.List;

public class PostService {
	
	private PostDAO_interface dao;
	
	
	public PostService() {
//		dao = new PostDAOImpl();
	}
	public PostVO addPost(Integer memId, Integer postCateId, java.sql.Timestamp postDate, byte postStatus, java.sql.Timestamp editDate, String postTitle , String postContent,Integer likeCount,Integer views) {
		PostVO postVO = new PostVO();
//		postVO.setMember(MemberVO);
//		postVO.setPostCateId(postCateId);
		postVO.setPostDate(postDate);
		postVO.setPostStatus(postStatus);
		postVO.setEditDate(editDate);
		postVO.setPostTitle(postTitle);
		postVO.setPostContent(postContent); //longtext
		postVO.setLikeCount(likeCount);
		postVO.setViews(views);
		dao.insert(postVO);
		return postVO;
	}
	public PostVO updatePost(Integer postId, Integer memId, Integer postCateId, java.sql.Timestamp postDate, byte postStatus, java.sql.Timestamp editDate, String postTitle , String postContent,Integer likeCount,Integer views) {
		PostVO postVO = new PostVO();
		postVO.setPostId(postId);
//		postVO.setMemId(memId);
//		postVO.setPostCateId(postCateId);
		postVO.setPostDate(postDate);
		postVO.setPostStatus(postStatus);
		postVO.setEditDate(editDate);
		postVO.setPostTitle(postTitle);
		postVO.setPostContent(postContent); //longtext
		postVO.setLikeCount(likeCount);
		postVO.setViews(views);
		dao.update(postVO);
		return postVO;
	}
	public void deletePost(Integer postId) {
		dao.delete(postId);
	}
	public PostVO getOnePost(Integer postId) {
		return dao.findByPK(postId);
	}
	public List<PostVO> getAll(){
		return dao.getAll();
	}

}
