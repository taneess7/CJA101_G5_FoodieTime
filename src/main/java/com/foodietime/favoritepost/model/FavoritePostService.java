package com.foodietime.favoritepost.model;

import java.util.List;

public interface FavoritePostService {
	
	int addFavoritePost(FavoritePostVO vo);
	int updateFavoritePost(FavoritePostVO vo);
	int deleteFavoritePost(Integer postId, Integer memId);
	FavoritePostVO findByPK(Integer postId, Integer memId);
	List<FavoritePostVO> getAll();
	
//	List<FavoritePostVO> getAllfavoritepost(int currentPage);
//	
//	public int getPageTotal();
	

}
