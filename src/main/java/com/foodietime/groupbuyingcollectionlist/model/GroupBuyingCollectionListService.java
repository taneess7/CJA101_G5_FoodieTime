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
	
	private final GroupBuyingCollectionListRepository repo;

	@Autowired
	public GroupBuyingCollectionListService(GroupBuyingCollectionListRepository repo) {
		this.repo = repo;
	}

	// 新增收藏
	public GroupBuyingCollectionListVO addCollection(GroupBuyingCasesVO gbId, MemberVO memId) {
		// 設置複合主鍵
        
        GroupBuyingCollectionListVO collection = new GroupBuyingCollectionListVO();
        collection.setGroupBuyingCase(gbId);
        collection.setMember(memId);
        collection.setCreateAt(LocalDateTime.now());  // 設置創建時間

        return repo.save(collection);  // 使用 save() 進行新增
    }

	// 刪除收藏
	public void deleteCollection(Integer gbId, Integer memId) {
		repo.deleteById_GbIdAndId_MemId(gbId, memId);
	}

	// 查詢單筆收藏（判斷是否已收藏）
	public GroupBuyingCollectionListVO getOneCollection(Integer gbId, Integer memId) {
		return repo.findById_GbIdAndId_MemId(gbId, memId);
	}

	// 查詢所有收藏
	public List<GroupBuyingCollectionListVO> getAll() {
		return repo.findAll();
	}

	// 查詢某會員的所有收藏
	public List<GroupBuyingCollectionListVO> getByMemId(Integer memId) {
		return repo.findById_MemId(memId);
	}

}
