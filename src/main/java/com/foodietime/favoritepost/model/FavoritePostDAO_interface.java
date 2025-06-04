package com.foodietime.favoritepost.model;

import java.util.List;

public interface FavoritePostDAO_interface {
	public int insert(FavoritePostVO favoritePost);

	public int update(FavoritePostVO favoritePost);

	public int delete(Integer postId, Integer memId);

	public FavoritePostVO findByPK(Integer postId, Integer memId);

	public List<FavoritePostVO> getAll();

}
