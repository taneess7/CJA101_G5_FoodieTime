//package com.foodietime.memfavlist.model;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FavoriteListService {
//
//	private final FavoriteListRepository fvlistrepo;
//
//	@Autowired
//	public FavoriteListService(FavoriteListRepository fvlistrepo) {
//		this.fvlistrepo = fvlistrepo;
//	}
//
//	// 新增收藏
//	public FavoriteListVO addFavoriteList(Integer memId, Integer prodId) {
//		FavoriteListVO favoriteListVO = new FavoriteListVO();
//
//		// 設定複合主鍵的屬性
//		favoriteListVO.getId().setMemId(memId);
//		favoriteListVO.getId().setProdId(prodId);
//
//		fvlistrepo.save(favoriteListVO);
//
//		return favoriteListVO;
//	}
//
//	// 刪除單一收藏
//	public void deleteFavorite(Integer memId, Integer prodId) {
//		fvlistrepo.deleteByIdMemIdAndIdProdId(memId, prodId);
//	}
//
//	// 刪除某會員所有收藏
//	public void deleteAllFavoritesByMem(Integer memId) {
//		fvlistrepo.deleteAllByIdMemId(memId);
//	}
//
//	// 查詢單一收藏
//	public FavoriteListVO getOneFavorite(Integer memId, Integer prodId) {
//		return fvlistrepo.findByIdMemIdAndIdProdId(memId, prodId);
//	}
//
//	// 查詢所有收藏
//	public List<FavoriteListVO> getAll() {
//		return fvlistrepo.findAll();
//	}
//
//	// 查詢某會員所有收藏
//	public List<FavoriteListVO> getFavoritesByMemId(Integer memId) {
//		return fvlistrepo.findAllByIdMemId(memId);
//	}
//}

