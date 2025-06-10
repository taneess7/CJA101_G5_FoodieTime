//package com.foodietime.store.model;
//
//import java.sql.*;
//import java.util.*;
//
//
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//
//public class StoreDAO implements StoreDAO_interface {
//
//	private static DataSource ds = null;
//	static {
//		try {
//			Context ctx = new InitialContext();
//			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB1");
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static final String INSERT_STMT = "INSERT INTO STORE (STORE_CATE_ID, STOR_NAME, STOR_DESC, STOR_ADDR, STOR_LONGITUDE, STOR_LATITUDE, STOR_PHONE, STOR_WEB, STOR_OPEN_TIME, STOR_CLOSE_TIME, STOR_WEEKLY_OFF_DAY, STOR_DELIVER, STOR_OPEN, STOR_PHOTO, STOR_REPORT_COUNT, TOTAL_STAR_NUM, TOTAL_REVIEWS) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//	private static final String GET_ALL_STMT = "SELECT STOR_ID, STORE_CATE_ID, STOR_NAME, STOR_DESC, STOR_ADDR, STOR_LONGITUDE, STOR_LATITUDE, STOR_PHONE, STOR_WEB, STOR_OPEN_TIME, STOR_CLOSE_TIME, STOR_WEEKLY_OFF_DAY, STOR_DELIVER, STOR_OPEN, STOR_PHOTO, STOR_REPORT_COUNT, TOTAL_STAR_NUM, TOTAL_REVIEWS FROM STORE ORDER BY STOR_ID";
//	private static final String GET_ONE_STMT = "SELECT STORE_CATE_ID, STORE_CATE FROM STORE WHERE STORE_CATE_ID = ?";
//	private static final String DELETE = "DELETE FROM STORE WHERE STOR_ID = ?";
//	private static final String UPDATE = "UPDATE STORE SET STORE_CATE_ID = ?, STOR_NAME = ?, STOR_DESC = ?, STOR_ADDR = ?, STOR_LONGITUDE = ?, STOR_LATITUDE = ?, STOR_PHONE = ?, STOR_WEB = ?, STOR_OPEN_TIME = ?, STOR_CLOSE_TIME = ?, STOR_WEEKLY_OFF_DAY = ?, STOR_DELIVER = ?, STOR_OPEN = ?, STOR_PHOTO = ?, STOR_REPORT_COUNT = ?, TOTAL_STAR_NUM = ?, TOTAL_REVIEWS WHERE STOR_ID = ?";
//
//	@Override
//	public void insert(StoreVO storeVO) {
//		Connection con = null;
//		PreparedStatement pstmt = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(INSERT_STMT);
//
////			pstmt.setInt(1, storeVO.getStoreCateId());
////			pstmt.setString(2, storeVO.getStorName());
////			pstmt.setString(3, storeVO.getStorDesc());
////			pstmt.setString(4, storeVO.getStorAddr());
////			pstmt.setDouble(5, storeVO.getStorLon());
////			pstmt.setDouble(6, storeVO.getStorLat());
////			pstmt.setString(7, storeVO.getStorPhone());
////			pstmt.setString(8, storeVO.getStorWeb());
////			pstmt.setTime(9, storeVO.getStorOnTime());
////			pstmt.setTime(10, storeVO.getStorOffTime());
////			pstmt.setString(11, storeVO.getStorOffDay());
////			pstmt.setByte(12, storeVO.getStorDeliver());
////			pstmt.setByte(13, storeVO.getStorStatus());
////			pstmt.setBytes(14, storeVO.getStorPhoto());
////			pstmt.setByte(15, storeVO.getStorReportCount());
////			pstmt.setInt(16, storeVO.getStarNum());
////			pstmt.setInt(17, storeVO.getReviews());
//
//			pstmt.executeUpdate();
//
//			// Handle any SQL errors
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured. " + se.getMessage());
//			// Clean up JDBC resources
//		} finally {
//			if (pstmt != null) {
//				try {
//					pstmt.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (con != null) {
//				try {
//					con.close();
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//
//	}
//
//	@Override
//	public void update(StoreVO storeVO) {
//		Connection con = null;
//		PreparedStatement pstmt = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(UPDATE);
//
////			pstmt.setInt(1, storeVO.getStoreCateId());
////			pstmt.setString(2, storeVO.getStorName());
////			pstmt.setString(3, storeVO.getStorDesc());
////			pstmt.setString(4, storeVO.getStorAddr());
////			pstmt.setDouble(5, storeVO.getStorLon());
////			pstmt.setDouble(6, storeVO.getStorLat());
////			pstmt.setString(7, storeVO.getStorPhone());
////			pstmt.setString(8, storeVO.getStorWeb());
////			pstmt.setTime(9, storeVO.getStorOnTime());
////			pstmt.setTime(10, storeVO.getStorOffTime());
////			pstmt.setString(11, storeVO.getStorOffDay());
////			pstmt.setByte(12, storeVO.getStorDeliver());
////			pstmt.setByte(13, storeVO.getStorStatus());
////			pstmt.setBytes(14, storeVO.getStorPhoto());
////			pstmt.setByte(15, storeVO.getStorReportCount());
////			pstmt.setInt(16, storeVO.getStarNum());
////			pstmt.setInt(17, storeVO.getReviews());
////			pstmt.setInt(18, storeVO.getStorId());
//
//			pstmt.executeUpdate();
//
//			// Handle any driver errors
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured. " + se.getMessage());
//			// Clean up JDBC resources
//		} finally {
//			if (pstmt != null) {
//				try {
//					pstmt.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (con != null) {
//				try {
//					con.close();
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//
//	}
//
//	@Override
//	public void delete(Integer storId) {
//		Connection con = null;
//		PreparedStatement pstmt = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(DELETE);
//
//			pstmt.setInt(1, storId);
//
//			pstmt.executeUpdate();
//
//			// Handle any driver errors
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured. " + se.getMessage());
//			// Clean up JDBC resources
//		} finally {
//			if (pstmt != null) {
//				try {
//					pstmt.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (con != null) {
//				try {
//					con.close();
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//
//	}
//
//	@Override
//	public StoreVO findByPrimaryKey(Integer storId) {
//		StoreVO storeVO = null;
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(GET_ONE_STMT);
//
//			pstmt.setInt(1, storId); // 參數注入?內
//
//			rs = pstmt.executeQuery();
//
//			while (rs.next()) {
//
////				storeVO = new StoreVO();
////				storeVO.setStorId(rs.getInt("STOR_ID"));
////				storeVO.setStoreCateId(rs.getInt("STORE_CATE_ID"));
////				storeVO.setStorName(rs.getString("STOR_NAME"));
////				storeVO.setStorDesc(rs.getString("STOR_DESC"));
////				storeVO.setStorAddr(rs.getString("STOR_ADDR"));
////				storeVO.setStorLon(rs.getDouble("STOR_LONGITUDE"));
////				storeVO.setStorLat(rs.getDouble("STOR_LATITUDE"));
////				storeVO.setStorPhone(rs.getString("STOR_PHONE"));
////				storeVO.setStorWeb(rs.getString("STOR_WEB"));
////				storeVO.setStorOnTime(rs.getTime("STOR_OPEN_TIME"));
////				storeVO.setStorOffTime(rs.getTime("STOR_CLOSE_TIME"));
////				storeVO.setStorOffDay(rs.getString("STOR_WEEKLY_OFF_DAY"));
////				storeVO.setStorDeliver(rs.getByte("STOR_DELIVER"));
////				storeVO.setStorStatus(rs.getByte("STOR_OPEN"));
////				storeVO.setStorPhoto(rs.getBytes("STOR_PHOTO"));
////				storeVO.setStorReportCount(rs.getByte("STOR_REPORT_COUNT"));
////				storeVO.setStarNum(rs.getInt("TOTAL_STAR_NUM"));
////				storeVO.setReviews(rs.getInt("TOTAL_REVIEWS"));
//
//			}
//
//			// Handle any driver errors
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured. " + se.getMessage());
//			// Clean up JDBC resources
//		} finally {
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (pstmt != null) {
//				try {
//					pstmt.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (con != null) {
//				try {
//					con.close();
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//
//		}
//
//		return storeVO;
//	}
//
//	@Override
//	public List<StoreVO> getAll() {
//		List<StoreVO> list = new ArrayList<StoreVO>();
//		StoreVO storeVO = null;
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(GET_ALL_STMT);
//			rs = pstmt.executeQuery();
//
//			while (rs.next()) {
//
//				storeVO = new StoreVO();
//
////				storeVO.setStorId(rs.getInt("STOR_ID"));
////				storeVO.setStoreCateId(rs.getInt("STORE_CATE_ID"));
////				storeVO.setStorName(rs.getString("STOR_NAME"));
////				storeVO.setStorDesc(rs.getString("STOR_DESC"));
////				storeVO.setStorAddr(rs.getString("STOR_ADDR"));
////				storeVO.setStorLon(rs.getDouble("STOR_LONGITUDE"));
////				storeVO.setStorLat(rs.getDouble("STOR_LATITUDE"));
////				storeVO.setStorPhone(rs.getString("STOR_PHONE"));
////				storeVO.setStorWeb(rs.getString("STOR_WEB"));
////				storeVO.setStorOnTime(rs.getTime("STOR_OPEN_TIME"));
////				storeVO.setStorOffTime(rs.getTime("STOR_CLOSE_TIME"));
////				storeVO.setStorOffDay(rs.getString("STOR_WEEKLY_OFF_DAY"));
////				storeVO.setStorDeliver(rs.getByte("STOR_DELIVER"));
////				storeVO.setStorStatus(rs.getByte("STOR_OPEN"));
////				storeVO.setStorPhoto(rs.getBytes("STOR_PHOTO"));
////				storeVO.setStorReportCount(rs.getByte("STOR_REPORT_COUNT"));
////				storeVO.setStarNum(rs.getInt("TOTAL_STAR_NUM"));
////				storeVO.setReviews(rs.getInt("TOTAL_REVIEWS"));
//
//				list.add(storeVO); // Store the row in the list
//			}
//
//			// Handle any driver errors
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured. " + se.getMessage());
//			// Clean up JDBC resources
//		} finally {
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (pstmt != null) {
//				try {
//					pstmt.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (con != null) {
//				try {
//					con.close();
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//		return list;
//
//	}
//
//}





