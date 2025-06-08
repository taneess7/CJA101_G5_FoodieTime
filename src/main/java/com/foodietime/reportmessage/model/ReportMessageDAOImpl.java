package com.foodietime.reportmessage.model;

import java.util.List;

import org.hibernate.Session;
import com.foodietime.favoritepost.model.HibernateUtil;

public class ReportMessageDAOImpl implements ReportMessageDAO_interface{
	
	@Override
	public int insert(ReportMessageVO reportMessage) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Integer id = (Integer) session.save(reportMessage);
			session.getTransaction().commit();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}
	@Override
	public int update(ReportMessageVO reportMessage) {	
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.update(reportMessage);
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int delete(Integer repMesId) {		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			ReportMessageVO reportMessage = session.get(ReportMessageVO.class, repMesId);
			if (reportMessage != null) {
				session.delete(reportMessage);
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
	public ReportMessageVO findByPK(Integer repMesId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			ReportMessageVO reportMessage = session.get(ReportMessageVO.class, repMesId);		
			session.getTransaction().commit();
			return reportMessage;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}

	@Override
	public List<ReportMessageVO> getAll() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			List<ReportMessageVO> list = session.createQuery("from ReportMessageVO", ReportMessageVO.class).list();
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}
}
	


