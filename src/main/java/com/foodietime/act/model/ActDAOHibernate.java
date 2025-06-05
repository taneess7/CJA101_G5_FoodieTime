package com.foodietime.act.model;

import java.util.List;

import org.hibernate.Session;



import jakarta.persistence.FetchType;

public class ActDAOHibernate implements ActDAO_interface {
	
	

	public ActDAOHibernate() {
		super();
	}

	@Override
	public int add(ActVO act) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Integer id = (Integer) session.save(act); //將 act 存進資料庫，並把它的主鍵值（@Id 欄位）回傳出來
			session.getTransaction().commit();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			 // 處理完錯誤後，不立即 return，而是往下走
		}
		return -1; // 如果發生錯誤才會走到這裡，當成失敗代號
	}

	@Override
	public int update(ActVO act) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.update(act);
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int delete(Integer actId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			ActVO act = session.get(ActVO.class, actId);
			if (act != null) {
				session.delete(act);
				;
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
	public ActVO findByPk(Integer actId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			ActVO act = session.get(ActVO.class, actId);
			session.getTransaction().commit();
			return act;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}

	@Override
	public List<ActVO> getAll() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();  //一次查出活動資料 + 關聯的店家資料，StoreVO ManyToOne設計FetchType.LAZY 避免N+1 及關連查用不到的表
			List<ActVO> list = session.createQuery("select a from ActVO a join fetch a.storId", ActVO.class).list();
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}

}