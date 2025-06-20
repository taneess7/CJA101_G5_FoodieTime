/*
 *  1. 萬用複合查詢-可由客戶端隨意增減任何想查詢的欄位
 *  2. 為了避免影響效能:
 *        所以動態產生萬用SQL的部份,本範例無意採用MetaData的方式,也只針對個別的Table自行視需要而個別製作之
 * */

package com.foodietime.act.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.foodietime.act.model.ActVO;

//import hibernate.util.HibernateUtil;
import java.util.*;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery; //Hibernate 5.2 開始 取代原 org.hibernate.Criteria 介面
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.Query; //Hibernate 5 開始 取代原 org.hibernate.Query 介面




public class HibernateUtil_CompositeQuery_Act { 

	public static Predicate get_aPredicate_For_AnyDB(CriteriaBuilder builder, Root<ActVO> root, String columnName, String value) {

		Predicate predicate = null;

		if ("actId".equals(columnName) || "storId".equals(columnName)) // 用於Integer
			predicate = builder.equal(root.get(columnName), Integer.valueOf(value));
		else if ("actDiscValue".equals(columnName))  // 用於Double
			predicate = builder.equal(root.get(columnName), Double.valueOf(value));
		else if ("actCate".equals(columnName) || "actName".equals(columnName) || "actContent".equals(columnName))// 用於varchar
			predicate = builder.like(root.get(columnName), "%" + value + "%");
		else if ("actSetTime".equals(columnName) || "actStartTime".equals(columnName) || "actEndTime".equals(columnName) || "actLastUpdate".equals(columnName))
			predicate = builder.equal(root.get(columnName), java.sql.Timestamp.valueOf(value));
		else if ("isGlobal".equals(columnName))
			predicate = builder.equal(root.get(columnName), Boolean.valueOf(value));//true : 全店適用的活動 false : 指定店家的活動
		
			//else if ("hiredate".equals(columnName)) // 用於date
//			predicate = builder.equal(root.get(columnName), java.sql.Date.valueOf(value));
//		else if ("deptno".equals(columnName)) { 
//			DeptVO deptVO = new DeptVO(); //有OneToMany 的One才寫這個
//			deptVO.setDeptno(Integer.valueOf(value));
//			predicate = builder.equal(root.get("deptVO"), deptVO);
	
		return predicate;
	}

		
	
//「條件篩選活動列表」。抑制「未經檢查的轉型警告」。
	@SuppressWarnings("unchecked") //店家:訂單需要複合查詢，活動listall 和 複合
	public static List<ActVO> getAllC(Map<String, String[]> map, Session session) {

//		Session session = HibernateUtil.getSessionFactory().openSession(); //不要寫Hibernate是為了和springBoot 和 hibernate做切割?
		Transaction tx = session.beginTransaction();
		List<ActVO> list = null;
		try {
			// 【●創建 CriteriaBuilder】
			CriteriaBuilder builder = session.getCriteriaBuilder();
			// 【●創建 CriteriaQuery】
			CriteriaQuery<ActVO> criteriaQuery = builder.createQuery(ActVO.class);
			// 【●創建 Root】
			Root<ActVO> root = criteriaQuery.from(ActVO.class);

			List<Predicate> predicateList = new ArrayList<Predicate>(); //回傳Predicate物件 可伸縮動態參數
			
			Set<String> keys = map.keySet();  //map 回傳keyset empno
			int count = 0;
			for (String key : keys) {
				String value = map.get(key)[0];
				if (value != null && value.trim().length() != 0 && !"action".equals(key)) {//不查action欄位
					count++;
					predicateList.add(get_aPredicate_For_AnyDB(builder, root, key, value.trim()));
					System.out.println("有送出查詢資料的欄位數count = " + count);
				}
			}
			
			
			System.out.println("predicateList.size()="+predicateList.size());
			criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));//把 Predicate 陣列加到 WHERE 條件中
			criteriaQuery.orderBy(builder.asc(root.get("actId")));// 用 actId 升冪排序
			// 【●最後完成創建 javax.persistence.Query●】
			Query query = session.createQuery(criteriaQuery); //將 CriteriaQuery 轉換成 Hibernate Query; //Hibernate 5 開始 取代原 org.hibernate.Query 介面
			list = query.getResultList();//執行查詢，取得結果並存入 list。

			tx.commit();
		} catch (RuntimeException ex) {
			if (tx != null)
				tx.rollback();
			throw ex; // System.out.println(ex.getMessage());
		} finally {
			session.close();
			// HibernateUtil.getSessionFactory().close();
		}

		return list;
	}
}
