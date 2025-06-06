//package com.foodietime.post.model;
//
//import java.util.List;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//
//import com.foodietime.favoritepost.model.FavoritePostPK;
//import com.foodietime.favoritepost.model.FavoritePostVO;
//
//public class PostDAOImpl implements PostDAO_interface {
//
//	
//
//	@Override
//	public void insert(PostVO post) {
//		SessionFactory factory = new Configuration().configure().buildSessionFactory();
//		Session session = factory.getCurrentSession();
////		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		try {
//			session.beginTransaction();
//			Integer id = (Integer) session.save(post);
//			session.getTransaction().commit();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			session.getTransaction().rollback();
//		}
//		return -1;
//	}
//
//	@Override
//	public void update(PostVO post) {
//		SessionFactory factory = new Configuration().configure().buildSessionFactory();
//		Session session = factory.getCurrentSession();
////		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		try {
//			session.beginTransaction();
//			session.update(post);
//			session.getTransaction().commit();
//			return 1;
//		} catch (Exception e) {
//			e.printStackTrace();
//			session.getTransaction().rollback();
//		}
//		return -1;
//	}
//
//	@Override
//	public void delete(PostVO postId) {
//		SessionFactory factory = new Configuration().configure().buildSessionFactory();
//		Session session = factory.getCurrentSession();
////		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		try {
//			session.beginTransaction();
//			PostVO favoritePost = session.get(PostVO.class, postId);
//			if (favoritePost != null) {
//				session.delete(favoritePost);
//			}
//			session.getTransaction().commit();
//			return 1;
//		} catch (Exception e) {
//			e.printStackTrace();
//			session.getTransaction().rollback();
//		}
//		return -1;
//	}
//
//	@Override
//	public PostVO findByPK(Integer postId) {
//		SessionFactory factory = new Configuration().configure().buildSessionFactory();
//		Session session = factory.getCurrentSession();
////		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		try {
//			session.beginTransaction();
//			PostVO favoritePost = session.get(PostVO.class, postId);
//			session.getTransaction().commit();
//			return favoritePost;
//		} catch (Exception e) {
//			e.printStackTrace();
//			session.getTransaction().rollback();
//		}
//		return null;
//	}
//
//	@Override
//	public List<PostVO> getAll() {
//		SessionFactory factory = new Configuration().configure().buildSessionFactory();
//		Session session = factory.getCurrentSession();
////		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		try {
//			session.beginTransaction();
//			List<PostVO> list = session.createQuery("from PostVO", PostVO.class).list();
//			session.getTransaction().commit();
//			return list;
//		} catch (Exception e) {
//			e.printStackTrace();
//			session.getTransaction().rollback();
//		}
//		return null;
//	}
//
//
//}
