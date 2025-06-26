package com.foodietime.memfavlist.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;

@Service
public class FavoriteListService {

	private final FavoriteListRepository repository;

    @Autowired
    public FavoriteListService(FavoriteListRepository repository) {
        this.repository = repository;
    }
    
    public List<FavoriteListVO> getFavoritesByMemId(Integer memId) {
        return repository.findAllByMemId(memId);
    }

    // 新增收藏
	public void addFavoriteList(Integer memId, Integer prodId) {
	    FavoriteListVO vo = new FavoriteListVO();
	    vo.setMemId(memId);
	    vo.setProdId(prodId);
	    repository.save(vo);
	}
	
	// 移除收藏
	public void deleteFavorite(Integer memId, Integer prodId) {
		repository.deleteById(new FavoriteListId(memId, prodId));
	}
	 
//	 public void addCouponFavorite(Integer memId, Integer couId) {
//	        FavoriteCouponVO vo = new FavoriteCouponVO();
//	        vo.setMemId(memId);
//	        vo.setCouId(couId);
//	        couponRepo.save(vo);
//	    }
	 
//	@Autowired
//	public FavoriteListService(FavoriteListRepository fvlistrepo) {
//		this.fvlistrepo = fvlistrepo;
//	}
//
//	//新增收藏
//	public FavoriteListVO addFavoriteList(Integer memId,Integer prodId) {
//		FavoriteListVO favoriteListVO = new FavoriteListVO();
//
//		favoriteListVO.setMemId(memId);
//		favoriteListVO.setProdId(prodId);
//		fvlistrepo.save(favoriteListVO);
//
//		return favoriteListVO;
//	}
//

//	//刪除某會員所有收藏
//	public void deleteAllFavoritesByMem(Integer memId) {
//		fvlistrepo.deleteAllByIdMemId(memId);

//	}
//
//	//查詢單一收藏
//	public FavoriteListVO getOneFavorite(Integer memId,Integer prodId) {

//		return fvlistrepo.findByIdMemIdAndIdProdId(memId, prodId);
//	}
//

//	//查詢所有收藏
//	public List<FavoriteListVO> getAll(){
//		return fvlistrepo.findAll();
//	}

//
//	//查詢某會員所有收藏
//	public List<FavoriteListVO> getFavoritesByMemId(Integer memId) {
//		return fvlistrepo.findAllByIdMemId(memId);

//	}
}
