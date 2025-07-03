package com.foodietime.favoritepost.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.member.model.MemberVO;
import com.foodietime.post.model.PostVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

@Service("FavoritePostService")
public class FavoritePostService {

	@Autowired
	private FavoritePostRepository repository;
	
	@PersistenceContext
    private EntityManager entityManager;

	public FavoritePostVO addFavoritePost(PostVO post, MemberVO member) {
		FavoritePostVO favoritePostVO = new FavoritePostVO();
		FavoritePostId id = new FavoritePostId(post.getPostId(), member.getMemId());
		favoritePostVO.setId(id);
		favoritePostVO.setPost(post);
		favoritePostVO.setMember(member);
		return repository.save(favoritePostVO);
	}

	public FavoritePostVO updateFavoritePost(PostVO post, MemberVO member) {
		FavoritePostVO favoritePostVO = new FavoritePostVO();
		favoritePostVO.setPost(post);
		favoritePostVO.setMember(member);

		return repository.save(favoritePostVO);
	}

	public void deleteFavoritePost(Integer postId, Integer memId) {
		FavoritePostId id = new FavoritePostId(postId, memId);
		repository.deleteById(id);
	}

	public FavoritePostVO findByPK(Integer postId, Integer memId) {
		FavoritePostId id = new FavoritePostId(postId, memId);
		return repository.findById(id).orElse(null);
	}

	public List<FavoritePostVO> getAll() {
		return repository.findAll();
	}
	
	//Spring Boot/JPA 實作 Criteria Query 寫法
    public List<PostVO> findFavoritePostsByMemberIdCriteria(Integer memId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PostVO> cq = cb.createQuery(PostVO.class);
        Root<FavoritePostVO> favoriteRoot = cq.from(FavoritePostVO.class);
        Join<FavoritePostVO, PostVO> postJoin = favoriteRoot.join("post");
        cq.select(postJoin)
          .where(cb.equal(favoriteRoot.get("member").get("memId"), memId));
        return entityManager.createQuery(cq).getResultList();
    }
    


}
