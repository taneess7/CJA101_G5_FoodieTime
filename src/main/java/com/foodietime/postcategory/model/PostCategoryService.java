package com.foodietime.postcategory.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.post.model.PostRepository;

@Service("PostCategoryService")
public class PostCategoryService {
	
	@Autowired
	private PostCategoryRepository repository;
	
	// public PostCategoryVO addPostCategory(String postCate) {
	// 	PostCategoryVO postcategoryVO = new PostCategoryVO();
	// 	postcategoryVO.setPostCate(postCate);
		
	// 	return repository.save(postcategoryVO);
		
	// }
	
	// public PostCategoryVO updatePostCategory(Integer postCateId, String postCate) {
	// 	PostCategoryVO postcategoryVO = new PostCategoryVO();
	// 	postcategoryVO.setPostCateId(postCateId);
	// 	postcategoryVO.setPostCate(postCate);
	// 	return repository.save(postcategoryVO);
	// }
	
	public void deletePostCategory(Integer postCateId) {
		repository.deleteById(postCateId);
	}
	public PostCategoryVO getOnePostCategory(Integer postCateId) {
		return repository.findById(postCateId).orElse(null);
	}
	public List<PostCategoryVO> getAll(){
		return repository.findAll();
	}

	// JPA 的 save 方法會自動判斷：如果物件的 ID 為 null，則執行 INSERT；如果 ID 存在，則執行 UPDATE。
    
   public PostCategoryVO save(PostCategoryVO postCategoryVO) {
       // 檢查分類名稱是否重複
       Optional<PostCategoryVO> existingCategory = repository.findByPostCate(postCategoryVO.getPostCate());

       // 如果找到了同名分類，且它的 ID 和我們正在儲存的物件 ID 不同，則表示名稱重複
       if (existingCategory.isPresent() && !existingCategory.get().getPostCateId().equals(postCategoryVO.getPostCateId())) {
           // 拋出一個自訂的例外，或直接回傳 null，讓 Controller 處理
           // 這裡我們拋出例外，讓 Controller 捕捉
           throw new IllegalStateException("分類名稱 '" + postCategoryVO.getPostCate() + "' 已存在。");
       }

       return repository.save(postCategoryVO);
   }
}
