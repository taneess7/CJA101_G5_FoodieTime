package com.foodietime.post.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.foodietime.member.model.MemberVO;
import com.foodietime.postcategory.model.PostCategoryVO;

@Service("PostService")
public class PostService {

	@Autowired
	private PostRepository repository;

	public PostVO addPost(Integer memId, Integer postCateId, java.sql.Timestamp postDate, byte postStatus,
			java.sql.Timestamp editDate, String postTitle, String postContent, Integer likeCount, Integer views) {
		PostVO postVO = new PostVO();

		MemberVO member = new MemberVO();
		member.setMemId(memId);

		PostCategoryVO postCategory = new PostCategoryVO();
		postCategory.setPostCateId(postCateId);

		postVO.setMember(member);
		postVO.setPostCate(postCategory);
		postVO.setPostDate(postDate);
		postVO.setPostStatus(postStatus);
		postVO.setEditDate(editDate);
		postVO.setPostTitle(postTitle);
		postVO.setPostContent(postContent); // longtext
		postVO.setLikeCount(likeCount);
		postVO.setViews(views);
		return repository.save(postVO);
	}

	public PostVO updatePost(Integer postId, Integer memId, Integer postCateId, java.sql.Timestamp postDate,
			byte postStatus, java.sql.Timestamp editDate, String postTitle, String postContent, Integer likeCount,
			Integer views) {
		PostVO postVO = new PostVO();

		MemberVO member = new MemberVO();
		member.setMemId(memId);

		PostCategoryVO postCategory = new PostCategoryVO();
		postCategory.setPostCateId(postCateId);

		postVO.setPostId(postId);
		postVO.setMember(member);
        postVO.setPostCate(postCategory);
		postVO.setPostDate(postDate);
		postVO.setPostStatus(postStatus);
		postVO.setEditDate(editDate);
		postVO.setPostTitle(postTitle);
		postVO.setPostContent(postContent); // longtext
		postVO.setLikeCount(likeCount);
		postVO.setViews(views);
		return repository.save(postVO);
	}

	public void deletePost(Integer postId) {
		repository.deleteById(postId);
	}

	public PostVO getOnePost(Integer postId) {
		return repository.findById(postId).orElse(null);
	}

	public List<PostVO> getAll() {
		return repository.findAllByPostStatusOrderByEditDateDesc((byte) 0);
	}
	
	// ==================== 新增方法以支援 PostMController ====================

	// 1. 後台查詢：根據狀態、類別、關鍵字進行查詢
		public List<PostVO> findPostsForAdmin(Byte status, Integer categoryId, String keyword) {
			// 直接呼叫 Repository 中使用 @Query 定義的單一查詢方法
			// 這樣可以將複雜的條件判斷邏輯交給資料庫處理，讓 Service 層更簡潔
			String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
			return repository.findFilteredPostsForAdmin(status, categoryId, trimmedKeyword);
		}


		// 2. 後台更新貼文 (簡化參數)
		// This method might be more suitable for admin updates where certain fields
		// like memId, postDate, likeCount, views are not directly editable by admin
		// but kept from the original post.
		public int updatePostAdmin(Integer postId, Integer postCateId, Byte postStatus,
				Timestamp editDate, String postTitle, String postContent) {

			Optional<PostVO> optionalPost = repository.findById(postId);
			if (optionalPost.isPresent()) {
				PostVO postVO = optionalPost.get();
				// Update only the fields that are allowed to be changed by admin
				postVO.setPostCate(new PostCategoryVO());
				postVO.getPostCate().setPostCateId(postCateId);
				postVO.setPostStatus(postStatus);
				postVO.setEditDate(editDate); // Admin updates will change editDate
				postVO.setPostTitle(postTitle);
				postVO.setPostContent(postContent);
				repository.save(postVO);
				return 1; // Indicate success
			}
			return 0; // Indicate failure (post not found)
		}

		// 3. 批量更新貼文狀態
		public int batchUpdateStatus(List<Integer> postIds, byte status) {
			int updatedCount = 0;
			for (Integer postId : postIds) {
				Optional<PostVO> optionalPost = repository.findById(postId);
				if (optionalPost.isPresent()) {
					PostVO postVO = optionalPost.get();
					postVO.setPostStatus(status);
					postVO.setEditDate(new Timestamp(System.currentTimeMillis())); // Update edit date on status change
					repository.save(postVO);
					updatedCount++;
				}
			}
			return updatedCount;
		}

		// 4. 批量刪除貼文
		public int batchDelete(List<Integer> postIds) {
			int deletedCount = 0;
			for (Integer postId : postIds) {
				if (repository.existsById(postId)) { // Check if it exists before deleting
					repository.deleteById(postId);
					deletedCount++;
				}
			}
			return deletedCount;
		}

}
