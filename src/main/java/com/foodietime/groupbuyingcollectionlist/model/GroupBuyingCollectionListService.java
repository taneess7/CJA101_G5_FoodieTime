package com.foodietime.groupbuyingcollectionlist.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesRepository;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcollectionlist.controller.GroupBuyingCollectionListController;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Service
public class GroupBuyingCollectionListService {

		
		private static final Logger log = LoggerFactory.getLogger(GroupBuyingCollectionListController.class);
		
		@Autowired
	    private GroupBuyingCollectionListRepository repository;
		
		@Autowired
	    private GroupBuyingCasesRepository gbcasesrepository;
	    
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
	    public GroupBuyingCollectionListVO addToCollection(Integer memId, Integer gbId, HttpSession session) {
	        // 檢查是否已經收藏過
	        log.info("檢查是否已經收藏，會員ID: {}, 團購ID: {}", memId, gbId);
	        if (isAlreadyInCollection(memId, gbId)) {
	            log.warn("此商品已在收藏清單中，會員ID: {}, 團購ID: {}", memId, gbId);
	            throw new RuntimeException("此商品已在收藏清單中");
	        }

	        // 確保從資料庫中獲取 GroupBuyingCasesVO
	        log.info("從資料庫中獲取團購商品，團購ID: {}", gbId);
	        GroupBuyingCasesVO groupBuyingCase = gbcasesrepository.findById(gbId)
	            .orElseThrow(() -> {
	                log.error("指定的團購商品不存在，團購ID: {}", gbId);
	                return new RuntimeException("指定的團購商品不存在");
	            });

	        // 確保從 session 中獲取到會員資訊並賦值
	        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
	        if (member != null) {
	            memId = member.getMemId(); // 若 session 存在會員資料，則更新 memId
	            log.info("從 session 中獲取到會員資料，會員ID: {}", memId);
	        } else {
	            log.error("會員資料不存在，無法設定 member");
	            throw new RuntimeException("會員資料不存在");
	        }

	        GroupBuyingCollectionListId id = new GroupBuyingCollectionListId(memId, gbId);
	        GroupBuyingCollectionListVO collection = new GroupBuyingCollectionListVO();
	        collection.setId(id);
	        collection.setCreateAt(LocalDateTime.now());
	        collection.setGroupBuyingCase(groupBuyingCase);
	        collection.setMember(member); // 設定會員資料

	        // 儲存收藏資料
	        try {
	            log.info("儲存收藏資料，會員ID: {}, 團購ID: {}", memId, gbId);
	            return repository.save(collection);  // 儲存操作
	        } catch (DataIntegrityViolationException e) {
	            log.error("資料完整性違規，可能已經收藏過該項目，會員ID: {}, 團購ID: {}", memId, gbId);
	            throw new RuntimeException("資料完整性違規，可能已經收藏過該項目。");
	        } catch (Exception e) {
	            log.error("資料庫錯誤，錯誤訊息: {}", e.getMessage());
	            throw new RuntimeException("資料庫錯誤：" + e.getMessage());
	        }
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
