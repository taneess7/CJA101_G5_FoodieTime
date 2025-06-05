package com.foodietime.act.model;


	import org.hibernate.Session;
import org.hibernate.SessionFactory;
	import org.hibernate.boot.MetadataSources;
	import org.hibernate.boot.registry.StandardServiceRegistry;
	import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

	public class HibernateUtil {
		private static StandardServiceRegistry registry;
		
		////2.宣告一個私有的static方法，透過呼叫 creatSessionFactory()得到回傳的SessionFactory物件
		private static final SessionFactory sessionFactory = createSessionFactory();
		
		////3.宣告一個公開的getter static方法，透過類別名稱呼叫，取得唯一的SessionFactory
		public static SessionFactory getSessionFactory() { 
			return sessionFactory;
		}

		////1.宣告一個私有的static方法，用來初始化並回傳SessionFactory
		private static SessionFactory createSessionFactory() { //創建工廠只能在內部呼叫
			try {

				registry = new StandardServiceRegistryBuilder() //初始化
						.configure()
						.build();
				////建立成本高，執行緒安全   一般整個系統只會建一次（單例 Singleton）
				SessionFactory sessionFactory = new MetadataSources(registry)
						.buildMetadata()
						.buildSessionFactory();

				return sessionFactory;

			} catch (Exception e) {
				e.printStackTrace();
				throw new ExceptionInInitializerError(e);
			}

		}
		
		//關閉factory
		public static void shutdown() {
			if (registry != null)
				StandardServiceRegistryBuilder.destroy(registry);
		}
		
		
	
			

		
	}


