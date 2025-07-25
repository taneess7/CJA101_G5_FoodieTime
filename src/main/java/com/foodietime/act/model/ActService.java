package com.foodietime.act.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.act.hibernate.HibernateUtil_CompositeQuery_Act;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreVO;

import jakarta.transaction.Transactional;

@Transactional // 管理這個 Service 裡的交易行為
@Service
public class ActService {

	@Autowired
	private ActRepository repository;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ActParticipationRepository actPartRepo;

	@Autowired
	ActParticipationService actPartSvc;

	// id 為 null 或該筆不存在，會變成新增。
	public void addAct(ActVO actVO) {
		repository.save(actVO);
	}

	// id 有值且對應資料庫有該筆資料，則是更新。(若有null的欄位會蓋掉原本資料)
	public void updateAct(ActVO actVO) {
		repository.save(actVO);
	}

	public void deleteAct(Integer actId) {
		if (repository.existsById(actId))
			repository.deleteByactId(actId);
	}

	public ActVO getOneAct(Integer actId) {
		Optional<ActVO> optional = repository.findById(actId);
		return optional.orElse(null);
	}

	public List<ActVO> getAllActs() { // 查所有資料
		return repository.findAll();
	}

	public ActVO findById(Integer id) { // 查單筆資料
		return repository.findById(id).orElse(null);
	}

	public List<ActVO> findActsBetween(String start, String end) { // 查區間資料
		return repository.findActsBetween(start, end);
	}
	
	public boolean existsByStoreAndAct(Integer storId, Integer actId) {//確認有無參加活動
		return actPartRepo.existsByStore_StorIdAndAct_ActId(storId, actId);
	}
	
   //顯示參與活動的店家 (dto) 沒有用到
	public List<ActStoreDTO> getActsWithStores() {
        return repository.findAllWithStore();
    }


	// 更新 - 查資料後再改欄位
	public String updateStoreAct(Integer id, ActVO newActData) {
		ActVO act = repository.findById(id).orElse(null);
		if (act != null)
			return "找不到活動";

		act.setActCate(newActData.getActCate());
		act.setActName(newActData.getActName());
		act.setActContent(newActData.getActContent());
		act.setActLastUpdate(new Timestamp(System.currentTimeMillis()));// 活動建立時間
		act.setActStartTime(newActData.getActStartTime());
		act.setActEndTime(newActData.getActEndTime());
		act.setActStatus(newActData.getActStatus());
		act.setActDiscount(newActData.getActDiscount());
		act.setActDiscValue(newActData.getActDiscValue());
		act.setActPhoto(newActData.getActPhoto());
		act.setActLastUpdate(new Timestamp(System.currentTimeMillis()));// 最後更新時間
		act.setIsGlobal(newActData.getIsGlobal());

		repository.save(act); // 更新
		return "活動已更新（ID: " + id + "，是否全店適用: " + newActData.getIsGlobal() + "）";
	}

	// 更新所有圖片
	public void updateAllActPhotos(MultipartFile[] files) throws IOException {
		List<ActVO> actList = repository.findAll(); // 撈出所有活動
		int count = Math.min(files.length, actList.size());

		for (int i = 0; i < count; i++) {
			MultipartFile file = files[i];
			if (!file.isEmpty()) {
				actList.get(i).setActPhoto(file.getBytes());
			}

			repository.saveAll(actList); // 批次更新資料庫
		}

	}

	// 複合查詢
	public List<ActVO> getAllMap(Map<String, String[]> map) {

		return HibernateUtil_CompositeQuery_Act.getAllC(map, sessionFactory.openSession());
	}

//	//取得活動列表 ToJSON
//	public String getAllActsToJSON() {
//		List<ActVO> actList = repository.findAll();
//		return new Gson().toJson(actList);
//	}

	// 取得圖片ById
	public byte[] getPhotoById(Integer id) {
		return repository.findActPhotoById(id);
	}
	
	//===================================================================================================//
	//回傳多個全站活動
	public List<ActVO> getCurrentGlobalActs(){
		Timestamp now = new Timestamp(System.currentTimeMillis());
		return repository.findAll().stream()
				.filter(act -> Boolean.TRUE.equals(act.getIsGlobal()))
				.filter(act -> act.getActStatus() == 1)
				.filter(act -> act.getActStartTime().before(now))
				.filter(act -> act.getActEndTime().after(now))
				.collect(Collectors.toList());
	} //讓 calculateDisplayPrice()選擇符合條件的活動
	
	
	//回傳一個全站活動
	public ActVO getCurrentGlobalAct() {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		return repository.findAll().stream()
				.filter(act -> Boolean.TRUE.equals(act.getIsGlobal())) //只取全站活動
				.filter(act -> act.getActStatus() == 1)
				.filter(act -> act.getActStartTime().before(now))
				.filter(act -> act.getActEndTime().after(now)) //時間尚未結束
				.findFirst()
				.orElse(null);
	}

	// ===================================================================================================//
	// 計算顯示價格（ 支援多種活動類型（折扣、買一送一），會檢查「商品是否符合活動關鍵字條件」）
	public int calDisplayPrice(ProductVO prod, ActVO act, StoreVO store) {
		
		//1. 活動或商品無效，原價
		if (act == null || prod == null || prod.getProdPrice() == null) {
			return prod.getProdPrice(); 
		}

		//2.非全站活動，就需要檢查店家是否參加
		if (!Boolean.TRUE.equals(act.getIsGlobal())) { // 只要是true的都當作false 所以非全站活動才做下面的判斷
			boolean hasJoined = actPartRepo.existsByStoreAndAct(store, act); // 店家有參加嗎?
			if (!hasJoined) { // 沒有參加
				return prod.getProdPrice(); // 原價
			}
		}

		// 3.依活動類型呼叫 enum 折扣策略，支援定義的別名("素食推薦")
		try {
			ActCategoryEnum cate = ActCategoryEnum.from(act.getActCate());
			return cate != null? cate.calculate(prod, act) : prod.getProdPrice(); //傳入productVO. actVO.
		} catch (IllegalArgumentException e) {
			return prod.getProdPrice();// 沒對應的活動類型-> 原價
		}

	}
	
	//===================================================================================================//
	//店家點選「參加活動」按鈕時判斷資格
	public boolean isStoreEligibleForAct(StoreVO store, ActVO act) {
		if(store == null || store.getProduct() == null || store.getProduct().isEmpty()) {
			return false;
		}
		
		ActCategoryEnum category = ActCategoryEnum.from(act.getActCate());
		if(category == null) {
			return false;//活動類型未定義
		}
		
		//檢查是否至少有一個商品符合活動條件 (折扣後價格有變)
		for (ProductVO product : store.getProduct()) {
			int originalPrice = product.getProdPrice();
			int discountedPrice = category.calculate(product, act);
			
			if(discountedPrice != originalPrice) {
				return true;//符合資格
			}
		}
		
		return false; //全部商品不符合活動條件
	}



	
	
}
