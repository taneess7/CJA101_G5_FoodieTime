package com.foodietime.memfavlist.model;

import java.util.List;

public class FavoriteListService {
private FavoriteListDAO_interface dao;
	
	public FavoriteListService() {
		dao = new FavoriteListJNDIDAO();
	}
	
	public FavoriteListVO addFavoriteList(Integer memId,Integer prodId) {
		FavoriteListVO favoriteListVO = new FavoriteListVO();
		
		favoriteListVO.setMemId(memId);
		favoriteListVO.setProdId(prodId);
		dao.insert(favoriteListVO);
		
		return favoriteListVO;
	}
	
	public void deleteFavorite(Integer memId,Integer prodId) {
		dao.deleteByMemIdAndProdId(memId, prodId);
	}
	
	public void deleteAllFavoritesByMem(Integer memeId) {
		dao.deleteAllByMemId(memeId);
	}

	public FavoriteListVO getOneFavorite(Integer memId,Integer prodId) {
		return dao.findByPrimaryKey(memId, prodId);
	}
	
	public List<FavoriteListVO> getAll(){
		return dao.getAll();
	}
}
