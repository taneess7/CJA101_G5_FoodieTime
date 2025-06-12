package com.foodietime.favoritepost.model;

import java.util.List;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

public class FavoritePostService {
	/*
	private FavoritePostDAO_interface dao;
	
	public FavoritePostService() {
		dao = new FavoritePostDAOImpl(); 
	}
	
	public FavoritePostVO addFavoritePost(PostVO post, MemberVO member) {
		
		FavoritePostVO favoritePostVO = new FavoritePostVO();
		
		favoritePostVO.setPostId(post);
		favoritePostVO.setMemId(member);		
		dao.insert(favoritePostVO);
		
		return favoritePostVO;
	}
	
	public FavoritePostVO updateFavoritePost(PostVO post, MemberVO member) {
		FavoritePostVO favoritePostVO = new FavoritePostVO();
		favoritePostVO.setPostId(post);
		favoritePostVO.setMemId(member);
		dao.update(favoritePostVO);
		
		return favoritePostVO;
	}
	
	public int deleteFavoritePost(Integer postId, Integer memId) {
		return dao.delete(postId, memId);
	}
	
	public FavoritePostVO findByPK(Integer postId, Integer memId) {
		return dao.findByPK(postId, memId);
	}
	
	public List<FavoritePostVO> getAll(){
		return dao.getAll();
	}*/
	
	
	
	
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
