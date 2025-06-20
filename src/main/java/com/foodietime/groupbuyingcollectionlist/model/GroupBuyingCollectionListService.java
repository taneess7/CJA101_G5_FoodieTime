package com.foodietime.groupbuyingcollectionlist.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;

@Service
public class GroupBuyingCollectionListService {


	 @Autowired
	    private GroupBuyingCollectionListRepository repository;
	    
	    // 查詢單筆收藏
	    public GroupBuyingCollectionListVO findByMemIdAndGbId(Integer memId, Integer gbId) {
	        return repository.findByMemIdAndGbId(memId, gbId).orElse(null);
	    }
	    
	    // 查詢會員收藏
	    public List<GroupBuyingCollectionListVO> findByMemId(Integer memId) {
	        return repository.findByMemId(memId);
	    }
	    
	    // 檢查是否已收藏
	    public boolean isAlreadyInCollection(Integer memId, Integer gbId) {
	        return repository.existsByMemIdAndGbId(memId, gbId);
	    }
	    
	    // 新增收藏
	    public GroupBuyingCollectionListVO addToCollection(Integer memId, Integer gbId) {
	        if (isAlreadyInCollection(memId, gbId)) {
	            throw new RuntimeException("此商品已在收藏清單中");
	        }
	        
	        GroupBuyingCollectionListId id = new GroupBuyingCollectionListId(memId, gbId);
	        GroupBuyingCollectionListVO collection = new GroupBuyingCollectionListVO();
	        collection.setId(id);
	        collection.setCreateAt(LocalDateTime.now());
	        
	        return repository.save(collection);
	    }
	    
	     // 查詢會員收藏（包含詳細資料）
	    public List<GroupBuyingCollectionListVO> findByMemIdWithDetails(Integer memId) {
	        return repository.findByMemIdWithDetails(memId);
	    }
	    
	    // 刪除
	    public boolean removeFromCollection(Integer memId, Integer gbId) {
	        if (!isAlreadyInCollection(memId, gbId)) {
	            return false;
	        }
	        repository.deleteByMemIdAndGbId(memId, gbId);
	        return true;
	    }
	    
	    
	   
	    

	    //查詢所有收藏清單
	    public List<GroupBuyingCollectionListVO> findAll() {
	        return repository.findAll();
	    }
	    
//	    public GroupBuyingCollectionListVO save(GroupBuyingCollectionListVO collection) {
//	        return repository.save(collection);
//	    }
}
