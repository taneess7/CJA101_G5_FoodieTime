package com.foodietime.coupon.model;

import java.sql.*;
import java.util.*;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class CouponDAO implements CouponDAO_interface{
	
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB1");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private static final String INSERT_STMT = "INSERT INTO COUPON (STOR_ID, COU_TYPE, COU_DES,COU_MIN_ORD,COU_DATE) VALUES (?, ?, ?, ?, ?)";
	private static final String GET_ALL_STMT = "SELECT COU_ID, STOR_ID, COU_TYPE, COU_DES, COU_MIN_ORD, COU_DATE FROM COUPON ORDER BY COU_ID";
	private static final String GET_ONE_STMT = "SELECT COU_ID, STOR_ID, COU_TYPE, COU_DES, COU_MIN_ORD, COU_DATE FROM COUPON WHERE COU_ID = ?";
	private static final String DELETE = "DELETE FROM COUPON WHERE COU_ID = ?";
	private static final String UPDATE = "UPDATE COUPON SET STOR_ID = ?, COU_TYPE = ?, COU_DES = ?, COU_MIN_ORD = ?, COU_DATE= ? WHERE COU_ID = ? ";
	private static final String GET_STOR_COU = "SELECT * FROM COUPON WHERE STOR_ID = ?;";
	private static final String GET_STOR_LIST = "SELECT DISTINCT STOR_ID FROM COUPON ORDER BY STOR_ID";

	@Override
	public void insert(CouponVO couponVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setInt(1, couponVO.getStorId());
			pstmt.setString(2, couponVO.getCouType());
			pstmt.setString(3, couponVO.getCouDes());
			pstmt.setInt(4, couponVO.getCouMinOrd());
			pstmt.setTimestamp(5, couponVO.getCouDate());
		
			pstmt.executeUpdate();

			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	
		
	}

	@Override
	public void update(CouponVO couponVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setInt(1, couponVO.getStorId());
			pstmt.setString(2, couponVO.getCouType());
			pstmt.setString(3, couponVO.getCouDes());
			pstmt.setInt(4, couponVO.getCouMinOrd());
			pstmt.setTimestamp(5, couponVO.getCouDate());
			pstmt.setInt(6, couponVO.getCouId());
			
			pstmt.executeUpdate();
			
			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public void delete(Integer couId) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, couId);

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public CouponVO findByPrimaryKey(Integer couId) {
		CouponVO couponVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1, couId); //參數注入?內

			rs = pstmt.executeQuery();

			while (rs.next()) {
			
				couponVO = new CouponVO();
				couponVO.setCouId(rs.getInt("COU_ID"));
				couponVO.setStorId(rs.getInt("STOR_ID"));
				couponVO.setCouType(rs.getString("COU_TYPE"));
				couponVO.setCouDes(rs.getString("COU_DES"));
				couponVO.setCouMinOrd(rs.getInt("COU_MIN_ORD"));
				couponVO.setCouDate(rs.getTimestamp("COU_DATE"));
			

			}

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		
		}
		
		return couponVO;
	}

	@Override
	public List<CouponVO> getAll() {
		

		List<CouponVO> list = new ArrayList<CouponVO>();
		CouponVO couponVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				
				couponVO = new CouponVO();
				
				couponVO.setCouId(rs.getInt("COU_ID"));
				couponVO.setStorId(rs.getInt("STOR_ID"));
				couponVO.setCouType(rs.getString("COU_TYPE"));
				couponVO.setCouDes(rs.getString("COU_DES"));
				couponVO.setCouMinOrd(rs.getInt("COU_MIN_ORD"));
				couponVO.setCouDate(rs.getTimestamp("COU_DATE"));
				
				list.add(couponVO); // Store the row in the list
			}

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return list;
	}
	
	@Override
	public List<CouponVO> findByStorId(Integer storId){
		
		List<CouponVO> list = new ArrayList<CouponVO>();
		CouponVO couponVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_STOR_COU);

			pstmt.setInt(1, storId); //參數注入?內

			rs = pstmt.executeQuery();

			while (rs.next()) {
			
				couponVO = new CouponVO();
				couponVO.setCouId(rs.getInt("COU_ID"));
				couponVO.setStorId(rs.getInt("STOR_ID"));
				couponVO.setCouType(rs.getString("COU_TYPE"));
				couponVO.setCouDes(rs.getString("COU_DES"));
				couponVO.setCouMinOrd(rs.getInt("COU_MIN_ORD"));
				couponVO.setCouDate(rs.getTimestamp("COU_DATE"));
				list.add(couponVO); // Store the row in the list
				System.out.println("✅ DAO 抓到 storeId: " + rs.getInt("STOR_ID"));

			}

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		
		}
		
		return list;
	}

	
	public List<CouponVO> findDistinctStorId() {
		List<CouponVO> list = new ArrayList<CouponVO>();
		CouponVO couponVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_STOR_LIST);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				
				couponVO = new CouponVO();
			    couponVO.setStorId(rs.getInt("STOR_ID"));
				list.add(couponVO); // Store the row in the list
				System.out.println("✅ DAO 抓到 storeId: " + rs.getInt("STOR_ID"));
			}
			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		
		}
		
		
		
		return list;
	}
	
	


}
