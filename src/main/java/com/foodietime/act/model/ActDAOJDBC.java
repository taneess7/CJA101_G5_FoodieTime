//package com.foodietime.act.model;
//
//import java.sql.*;
//import java.util.*;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//
//public class ActDAO implements ActDAO_interface {
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
//	private static final String INSERT_STMT = "INSERT INTO ACTIVITY (STOR_ID, ACT_CATE, ACT_NAME, ACT_CONTENT, ACT_LAUNCHTIME, ACT_STARTTIME, ACT_ENDTIME, ACT_STATUS, ACT_DISCOUNT, ACT_DISCOUNT_VALUE, ACT_PHOTO, ACT_LAST_UPDATE) "
//			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
//	private static final String GET_ALL_STMT = "SELECT ACT_ID, STOR_ID, ACT_CATE, ACT_NAME, ACT_CONTENT, ACT_LAUNCHTIME, ACT_STARTTIME, ACT_ENDTIME, ACT_STATUS, ACT_DISCOUNT, ACT_DISCOUNT_VALUE, ACT_PHOTO, ACT_LAST_UPDATE FROM ACTIVITY ORDER BY ACT_ID";
//	private static final String GET_ONE_STMT = "SELECT ACT_ID, STOR_ID, ACT_CATE, ACT_NAME, ACT_CONTENT, ACT_LAUNCHTIME, ACT_STARTTIME, ACT_ENDTIME, ACT_STATUS, ACT_DISCOUNT, ACT_DISCOUNT_VALUE, ACT_PHOTO, ACT_LAST_UPDATE FROM ACTIVITY WHERE ACT_ID = ?";
//	private static final String DELETE = "DELETE FROM ACTIVITY WHERE ACT_ID = ?";
//	private static final String UPDATE = "UPDATE ACTIVITY SET STOR_ID = ?, ACT_CATE = ?, ACT_NAME = ?, ACT_CONTENT = ?, ACT_LAUNCHTIME = ?, ACT_STARTTIME = ?, ACT_ENDTIME = ?, ACT_STATUS = ?, ACT_DISCOUNT = ?, ACT_DISCOUNT_VALUE = ?, ACT_PHOTO = ?, ACT_LAST_UPDATE = ? WHERE ACT_ID = ? ";
//
//	@Override
//	public void insert(ActVO actVO) {
//		Connection con = null;
//		PreparedStatement pstmt = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(INSERT_STMT);
//
//			pstmt.setInt(1, actVO.getStorId());
//			pstmt.setString(2, actVO.getActCate());
//			pstmt.setString(3, actVO.getActName());
//			pstmt.setString(4, actVO.getActContent());
//			pstmt.setTimestamp(5, actVO.getActSetTime());
//			pstmt.setTimestamp(6, actVO.getActSetTime());
//			pstmt.setTimestamp(7, actVO.getActEndTime());
//			pstmt.setByte(8, actVO.getActStatus());
//			pstmt.setByte(9, actVO.getActDiscount());
//			pstmt.setDouble(10, actVO.getActDiscValue());
//			pstmt.setBytes(11, actVO.getActPhoto());
//			pstmt.setTimestamp(12, actVO.getActLastUpdate());
//
//			pstmt.executeUpdate();
//
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured." + se.getMessage());
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
//	public void update(ActVO actVO) {
//
//		Connection con = null;
//		PreparedStatement pstmt = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(UPDATE);
//
//			pstmt.setInt(1, actVO.getStorId());
//			pstmt.setString(2, actVO.getActCate());
//			pstmt.setString(3, actVO.getActName());
//			pstmt.setString(4, actVO.getActContent());
//			pstmt.setTimestamp(5, actVO.getActSetTime());
//			pstmt.setTimestamp(6, actVO.getActStartTime());
//			pstmt.setTimestamp(7, actVO.getActEndTime());
//			pstmt.setByte(8, actVO.getActStatus());
//			pstmt.setByte(9, actVO.getActDiscount());
//			pstmt.setDouble(10, actVO.getActDiscValue());
//			pstmt.setBytes(11, actVO.getActPhoto());
//			pstmt.setTimestamp(12, actVO.getActLastUpdate());
//			pstmt.setInt(13, actVO.getActId());
//
//			pstmt.executeUpdate();
//
//			// Handle any SQL errors
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured." + se.getMessage());
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
//	public void delete(Integer actId) {
//		Connection con = null;
//		PreparedStatement pstmt = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(DELETE);
//
//			pstmt.setInt(1, actId);
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
//	public ActVO findByPrimaryKey(Integer actId) {
//
//		ActVO actVO = null;
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		try {
//
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(GET_ONE_STMT);
//
//			pstmt.setInt(1, actId);
//
//			rs = pstmt.executeQuery();
//
//			while (rs.next()) {
//
//				actVO = new ActVO();
//				actVO.setActId(rs.getInt("ACT_ID"));
//				actVO.setStorId(rs.getInt("STOR_ID"));
//				actVO.setActCate(rs.getString("ACT_CATE"));
//				actVO.setActCate(rs.getString("ACT_NAME"));
//				actVO.setActCate(rs.getString("ACT_CONTENT"));
//				actVO.setActSetTime(rs.getTimestamp("ACT_LAUNCHTIME"));
//				actVO.setActStartTime(rs.getTimestamp("ACT_STARTTIME"));
//				actVO.setActEndTime(rs.getTimestamp("ACT_ENDTIME"));
//				actVO.setActStatus(rs.getByte("ACT_STATUS"));
//				actVO.setActDiscount(rs.getByte("ACT_DISCOUNT"));
//				actVO.setActDiscValue(rs.getDouble("ACT_DISCOUNT_VALUE"));
//				actVO.setActPhoto(rs.getBytes("ACT_PHOTO"));
//				actVO.setActLastUpdate(rs.getTimestamp("ACT_LAST_UPDATE"));
//			}
//			// Handle any SQL errors
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
//		return actVO;
//	}
//
//	@Override
//	public List<ActVO> getAll() {
//
//		List<ActVO> list = new ArrayList<ActVO>();
//		ActVO actVO = null;
//
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
//				// empVO 也稱為 Domain objects
//				actVO = new ActVO();
//				actVO.setActId(rs.getInt("ACT_ID"));
//				actVO.setStorId(rs.getInt("STOR_ID"));
//				actVO.setActCate(rs.getString("ACT_CATE"));
//				actVO.setActCate(rs.getString("ACT_NAME"));
//				actVO.setActCate(rs.getString("ACT_CONTENT"));
//				actVO.setActSetTime(rs.getTimestamp("ACT_LAUNCHTIME"));
//				actVO.setActStartTime(rs.getTimestamp("ACT_STARTTIME"));
//				actVO.setActEndTime(rs.getTimestamp("ACT_ENDTIME"));
//				actVO.setActStatus(rs.getByte("ACT_STATUS"));
//				actVO.setActDiscount(rs.getByte("ACT_DISCOUNT"));
//				actVO.setActDiscValue(rs.getDouble("ACT_DISCOUNT_VALUE"));
//				actVO.setActPhoto(rs.getBytes("ACT_PHOTO"));
//				actVO.setActLastUpdate(rs.getTimestamp("ACT_LAST_UPDATE"));
//
//				list.add(actVO); // Store the row in the list
//			}
//
//			// Handle any SQL errors
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
//	};
//
//}



