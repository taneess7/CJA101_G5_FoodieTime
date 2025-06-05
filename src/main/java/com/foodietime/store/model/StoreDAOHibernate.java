package com.foodietime.store.model;


import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;



import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.foodietime.act.model.ActVO;
import com.foodietime.act.model.HibernateUtil;
import com.foodietime.storeCate.model.StoreCateVO;

public class StoreDAOHibernate implements StoreDAO_interface {

	@Override
	public int add(StoreVO store) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Integer id = (Integer) session.save(store);
			session.getTransaction().commit();
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int update(StoreVO store) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.update(store);
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return -1;
	}

	@Override
	public int delete(Integer storId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			StoreVO store = session.get(StoreVO.class, storId);
			if (store != null) {
				session.delete(store);
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
	public StoreVO findByPk(Integer storId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			StoreVO store = session.get(StoreVO.class, storId);
			session.getTransaction().commit();
			return store;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}

	@Override
	public List<StoreVO> getAll() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();  
			List<StoreVO> list = session.createQuery("select a from StoreVO a join fetch a.storeCateId",StoreVO.class).list();
			session.getTransaction().commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		return null;
	}


	@Override
	public int add(StoreVO store, StoreCateVO cate) {
		
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try {
				session.beginTransaction();
				//新增後要回傳 pk
				Integer storId = (Integer) session.save(store);
				Integer cateId = (Integer) session.save(cate);
				
				//新增後不需要回傳值，只要存起來就好
//				session.save(store);
//				session.save(cate);	
				
				session.getTransaction().commit();
				return cateId;
				
				
			} catch (Exception e) {
				e.printStackTrace();
				session.getTransaction().rollback();
			}
			return -1;
		}


	public StoreDAOHibernate() {
		super();
		
	}
	

	
}

	
