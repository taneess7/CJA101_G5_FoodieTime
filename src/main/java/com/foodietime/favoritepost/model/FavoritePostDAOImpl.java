package com.foodietime.favoritepost.model;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.foodietime.favoritepost.model.FavoritePostVO;
//import util.HibernateUtil;

public class FavoritePostDAOImpl implements FavoritePostDAO_interface {
	
	SessionFactory factory = new Configuration().configure().buildSessionFactory();
	Session session = factory.getCurrentSession();

	@Override
	public int insert(FavoritePostVO favoritePost) {
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Integer id = (Integer) session.save(favoritePost);
			session.getTransaction().commit();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int update(FavoritePostVO favoritePost) {	
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.update(favoritePost);
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int delete(Integer postId, Integer memId) {		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			FavoritePostPK id = new FavoritePostPK(postId, memId);
			FavoritePostVO favoritePost = session.get(FavoritePostVO.class, id);
			if (favoritePost != null) {
				session.delete(favoritePost);
			}
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public FavoritePostVO findByPK(Integer postId, Integer memId) {
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			FavoritePostPK id = new FavoritePostPK(postId, memId);
			FavoritePostVO favoritePost = session.get(FavoritePostVO.class, id);			
			session.getTransaction().commit();
			return favoritePost;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}

	@Override
	public List<FavoritePostVO> getAll() {
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			List<FavoritePostVO> list = session.createQuery("from FavoritePostVO", FavoritePostVO.class).list();
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}
}
