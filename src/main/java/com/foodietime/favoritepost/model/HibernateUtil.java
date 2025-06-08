package com.foodietime.favoritepost.model;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	 private static SessionFactory sessionFactory;

	    static {
	        try {
	            // 載入 hibernate.cfg.xml 組態
	            Configuration configuration = new Configuration().configure();
	            
	            // 建立 ServiceRegistry
	            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
	                    .applySettings(configuration.getProperties()).build();

	            // 建立 SessionFactory
	            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

	        } catch (Throwable ex) {
	            System.err.println("Initial SessionFactory creation failed." + ex);
	            throw new ExceptionInInitializerError(ex);
	        }
	    }

	    // 提供全域取得 SessionFactory 的方式
	    public static SessionFactory getSessionFactory() {
	        return sessionFactory;
	    }

	    // 可選：提供釋放資源的方法（例如應用關閉時呼叫）
	    public static void shutdown() {
	        getSessionFactory().close();
	    }
}
