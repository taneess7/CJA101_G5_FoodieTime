package com.foodietime.reportpost.model;

import java.util.List;
import org.hibernate.Session;

import com.foodietime.reportpost.model.HibernateUtil;
import com.foodietime.reportpost.model.ReportPostVO;

public class ReportPostDAOImpl implements ReportPostDAO_interface{

	@Override
	public int insert(ReportPostVO reportPost) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Integer id = (Integer) session.save(reportPost);
			session.getTransaction().commit();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int update(ReportPostVO reportPost) {	
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.update(reportPost);
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int delete(Integer repPostId) {		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			ReportPostVO reportPost = session.get(ReportPostVO.class, repPostId);
			if (reportPost != null) {
				session.delete(reportPost);
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
	public ReportPostVO findByPK(Integer repPostId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			ReportPostVO reportPost = session.get(ReportPostVO.class, repPostId);			
			session.getTransaction().commit();
			return reportPost;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}

	@Override
	public List<ReportPostVO> getAll() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			List<ReportPostVO> list = session.createQuery("from ReportPostVO", ReportPostVO.class).list();
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}
	

}
