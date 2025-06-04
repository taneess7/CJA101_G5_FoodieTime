package com.foodietime.favoritepost.model;

//import static idv.david.util.Constants.PAGE_MAX_RESULT;

import java.util.List;

public class FavoritePostServiceImpl implements FavoritePostService{
	
	private FavoritePostDAO_interface dao;
	public FavoritePostServiceImpl() {
		dao = new FavoritePostDAOImpl(); 
	}
	
	@Override
	public int addFavoritePost(FavoritePostVO vo) {
		return dao.insert(vo);
	}
	
	@Override
	public int updateFavoritePost(FavoritePostVO vo) {
		return dao.update(vo);
	}
	
	@Override
	public int deleteFavoritePost(Integer postId, Integer memId) {
		return dao.delete(postId, memId);
	}
	
	@Override
	public FavoritePostVO findByPK(Integer postId, Integer memId) {
		return dao.findByPK(postId, memId);
	}
	
	@Override
	public List<FavoritePostVO> getAll(){
		return dao.getAll();
	}
	/*
	private FavoritePostDAO dao;

	public FavoritePostServiceImpl() {
		dao = new FavoritePostDAO_interface;
	}
	@Override
	public List<FavoritePostVO> getAllfavoritepost(int currentPage) {
		return dao.getAll(currentPage);
	}

	@Override
	public int getPageTotal() {
		long total = dao.getTotal();
		int pageQty = (int)(total % PAGE_MAX_RESULT == 0 ? (total / PAGE_MAX_RESULT) : (total / PAGE_MAX_RESULT + 1));
		return pageQty;
	}
	
	*/
	

}
