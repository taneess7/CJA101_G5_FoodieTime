package com.foodietime.memfavlist.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FavoriteListService {

	private final FavoriteListRepository fvlistrepo;
	
	@Autowired
	public FavoriteListService(FavoriteListRepository fvlistrepo) {
		this.fvlistrepo = fvlistrepo;
	}
	
	public FavoriteListVO addFavoriteList(Integer memId,Integer prodId) {
		FavoriteListVO favoriteListVO = new FavoriteListVO();
		
		favoriteListVO.setMemId(memId);
		favoriteListVO.setProdId(prodId);
		fvlistrepo.save(favoriteListVO);
		
		return favoriteListVO;
	}
	
	public void deleteFavorite(Integer memId, Integer prodId) {
		fvlistrepo.deleteByIdMemIdAndIdProdId(memId, prodId);
	}
	public void deleteAllFavoritesByMem(Integer memId) {
		fvlistrepo.deleteAllByIdMemId(memId);
	}

	public FavoriteListVO getOneFavorite(Integer memId,Integer prodId) {
		return fvlistrepo.findByIdMemIdAndIdProdId(memId, prodId);
	}
	
	public List<FavoriteListVO> getAll(){
		return fvlistrepo.findAll();
	}
	
	public List<FavoriteListVO> getFavoritesByMemId(Integer memId) {
		return fvlistrepo.findAllByIdMemId(memId);
	}
}
