//package com.foodietime.post.model;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.foodietime.member.model.MemService;
//import com.foodietime.member.model.MemberVO;
//import com.foodietime.postcategory.model.PostCategoryService;
//import com.foodietime.postcategory.model.PostCategoryVO;
//
//@Service
//public class PostService {
//
//	@Autowired
//	PostRepository repository;
//
//	@Autowired
//	private MemService memService; // 新增這兩個 Service
//
//	@Autowired
//	private PostCategoryService postCategoryService;
///*
//	public PostService() {
//		dao = new PostDAOImpl();
//		memService = new MemService();
//		postCategoryService = new PostCategoryService();
//	}
//
//	public PostVO addPost(Integer memId, Integer postCateId, java.sql.Timestamp postDate, byte postStatus,
//			java.sql.Timestamp editDate, String postTitle, String postContent, Integer likeCount, Integer views) {
//		PostVO postVO = new PostVO();
//
////		MemberVO member = memService.getOneMember(memId);
//		PostCategoryVO postCategory = postCategoryService.getOnePostCategory(postCateId);
//
////		postVO.setMemId(member);
//		postVO.setPostCateId(postCategory);
//		postVO.setPostDate(postDate);
//		postVO.setPostStatus(postStatus);
//		postVO.setEditDate(editDate);
//		postVO.setPostTitle(postTitle);
//		postVO.setPostContent(postContent); // longtext
//		postVO.setLikeCount(likeCount);
//		postVO.setViews(views);
//		dao.insert(postVO);
//		return postVO;
//	}
//
//	public PostVO updatePost(Integer postId, Integer memId, Integer postCateId, java.sql.Timestamp postDate,
//			byte postStatus, java.sql.Timestamp editDate, String postTitle, String postContent, Integer likeCount,
//			Integer views) {
//		PostVO postVO = new PostVO();
//
////		MemberVO member = memService.getOneMember(memId);
//		PostCategoryVO postCategory = postCategoryService.getOnePostCategory(postCateId);
//
//		postVO.setPostId(postId);
////		postVO.setMemId(member);
//		postVO.setPostCateId(postCategory);
//		postVO.setPostDate(postDate);
//		postVO.setPostStatus(postStatus);
//		postVO.setEditDate(editDate);
//		postVO.setPostTitle(postTitle);
//		postVO.setPostContent(postContent); // longtext
//		postVO.setLikeCount(likeCount);
//		postVO.setViews(views);
//		dao.update(postVO);
//		return postVO;
//	}
//
//	public void deletePost(Integer postId) {
//		dao.delete(postId);
//	}
//
//	public PostVO getOnePost(Integer postId) {
//		return dao.findByPK(postId);
//	}
//
//	public List<PostVO> getAll() {
//		return dao.getAll();
//	}
//*/
//}
