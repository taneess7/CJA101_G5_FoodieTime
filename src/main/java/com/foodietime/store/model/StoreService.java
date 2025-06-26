package com.foodietime.store.model;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.foodietime.coupon.model.CouponRepository;
import com.foodietime.product.model.ProductVO;







@Transactional
@Service
public class StoreService {
	
	@Autowired
	private StoreRepository repository; 
	
	@Autowired
	private CouponRepository couRepository; 
	


	
	public void addStore(StoreVO storeVO) {
		repository.save(storeVO);  //原廠save可以用在新增和修改，有主鍵是update，沒有主鍵是insert
	}
	
	public void updateStore(StoreVO storeVO) {
		repository.save(storeVO);
	}
	
	public void deleteStore(Integer storId) {
		if(repository.existsById(storId))
			repository.deleteByStorId(storId); //StoreRepository 自訂刪除
	}
	
	public StoreVO getOneStore(Integer storId) {
		Optional<StoreVO> optional = repository.findById(storId); //內建單一查詢
		return optional.orElse(null); //如果值存在就回傳其值，否則回傳null
	}
	
	public List<StoreVO> getAll(){
		return repository.findAll(); //回傳全部
	}
	
	public StoreVO findById(Integer id) {
		return repository.findById(id).orElse(null);
	}
	
	//餐廳首頁模糊搜尋(丰)
	public List<StoreVO> searchStores(String keyword) {
	    return repository.findByStorNameContainingIgnoreCaseOrStorAddrContainingIgnoreCase(keyword, keyword);
	}
//	//註冊 storeSvc.register(storeVO) + 防止重複註冊
//	public void register(StoreVO storeVO) {
//		if (repository.findByStorEmail(storeVO.getStorEmail()) != null)
//			throw new RuntimeException("此 Email 已被註冊");
//		repository.save(storeVO);
//	}
	
	//店家登入抓email 
	public StoreVO findByStorEmail(String email) {
		return repository.findByStorEmail(email);
	}
	
	//查詢店家類別
	 public List<StoreVO> findByCateId(Integer cateId) {
	        return repository.findByStoreCateId(cateId);
	    }
	
	

	
	
	//模糊查詢
//	public List<StoreVO> getAll(Map<String, String[]> map){ //回傳key-value類型的參數
//		return HibernateUtil_CompositeQuery_g05.getAllC(map.sessionFactory.openSession());
//		 // 呼叫 Hibernate 複合查詢工具類別的靜態方法 getAllC(...)
//	    // map 是從前端來的查詢條件
//	    // sessionFactory.openSession() 是打開一個新的 Hibernate Session，交給工具處理查詢
//	}
	
	

	//檢舉次數
	public void reportStore(Integer storId) {
		StoreVO store = repository.findById(storId)
				.orElseThrow(() -> new RuntimeException("找不到該店家"));
				
		// 檢舉次數預設為 0
	    byte oldCount = store.getStorReportCount() == null ? 0 : store.getStorReportCount();
	    byte newCount = (byte) (oldCount + 1);

	    store.setStorReportCount(newCount);

	    // 根據檢舉次數決定上下架狀態
	    store.setStorStatus(newCount >= 3 ? (byte) 0 : (byte) 1);

		
		repository.save(store); //存回資料庫
	}
	
	/*********** 可動態帶入資料庫店家類別 ******************/

	// 中式餐廳-改

	public List<StoreVO> getChRestaurants() {
		String keyword = "%中式%";
		return repository.findByStoreCateNameLike(keyword);
	}

	// 日式餐廳-改

	public List<StoreVO> getJaRestaurants() {
		String keyword = "%日式%";
		return repository.findByStoreCateNameLike(keyword);
	}

	// 韓式餐廳-改

	public List<StoreVO> getKoRestaurants() {
		String keyword = "%韓式%";
		return repository.findByStoreCateNameLike(keyword);
	}

	// 泰式餐廳-改

	public List<StoreVO> getThaiRestaurants() {
		String keyword = "%泰式%";
		return repository.findByStoreCateNameLike(keyword);
	}

	// 義式餐廳-改

	public List<StoreVO> getItRestaurants() {
		String keyword = "%義式%";
		return repository.findByStoreCateNameLike(keyword);
	}

	// 美式餐廳-改

	public List<StoreVO> getAmRestaurants() {
		String keyword = "%美式%";
		return repository.findByStoreCateNameLike(keyword);
	}

	// 甜點餐廳-改

	public List<StoreVO> getSwRestaurants() {
		String keyword = "%甜點%";
		return repository.findByStoreCateNameLike(keyword);
	}

	// 素食餐廳-改

	public List<StoreVO> getVeRestaurants() {
		String keyword = "%素食%";
		return repository.findByStoreCateNameLike(keyword);
	}

	public List<ProductVO> getProdsByStoreId(Integer storId){
		return repository.findProdsByStorId(storId);
	}
	


		
	}

	
	


