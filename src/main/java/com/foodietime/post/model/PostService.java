//package com.foodietime.post.model;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.foodietime.member.model.MemberVO;
//import com.foodietime.postcategory.model.PostCategoryVO;
//
//@Service("PostService")
//public class PostService {
//
//	@Autowired
//	private PostRepository repository;
//
//	public PostVO addPost(Integer memId, Integer postCateId, java.sql.Timestamp postDate, byte postStatus,
//			java.sql.Timestamp editDate, String postTitle, String postContent, Integer likeCount, Integer views) {
//		PostVO postVO = new PostVO();
//
//		MemberVO member = new MemberVO();
//		member.setMemId(memId);
//
//		PostCategoryVO postCategory = new PostCategoryVO();
//		postCategory.setPostCateId(postCateId);
//
//		postVO.setMember(member);
//		postVO.setPostCate(postCategory);
//		postVO.setPostDate(postDate);
//		postVO.setPostStatus(postStatus);
//		postVO.setEditDate(editDate);
//		postVO.setPostTitle(postTitle);
//		postVO.setPostContent(postContent); // longtext
//		postVO.setLikeCount(likeCount);
//		postVO.setViews(views);
//		return repository.save(postVO);
//	}
//
//	public PostVO updatePost(Integer postId, Integer memId, Integer postCateId, java.sql.Timestamp postDate,
//			byte postStatus, java.sql.Timestamp editDate, String postTitle, String postContent, Integer likeCount,
//			Integer views) {
//		PostVO postVO = new PostVO();
//
//		MemberVO member = new MemberVO();
//		member.setMemId(memId);
//
//		PostCategoryVO postCategory = new PostCategoryVO();
//		postCategory.setPostCateId(postCateId);
//
//		postVO.setPostId(postId);
//		postVO.setMember(member);
//        postVO.setPostCate(postCategory);
//		postVO.setPostDate(postDate);
//		postVO.setPostStatus(postStatus);
//		postVO.setEditDate(editDate);
//		postVO.setPostTitle(postTitle);
//		postVO.setPostContent(postContent); // longtext
//		postVO.setLikeCount(likeCount);
//		postVO.setViews(views);
//		
//		return repository.save(postVO);
//	}
//
//	public void deletePost(Integer postId) {
//		repository.deleteById(postId);
//	}
//
//	public PostVO getOnePost(Integer postId) {
//		return repository.findById(postId).orElse(null);
//	}
//
//	public List<PostVO> getAll() {
//		return repository.findAll();
//	}
//
//}
