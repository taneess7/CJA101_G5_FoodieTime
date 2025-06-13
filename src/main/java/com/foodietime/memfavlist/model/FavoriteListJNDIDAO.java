//package com.foodietime.memfavlist.model;
//
//import java.sql.*;
//import java.util.*;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//
//public class FavoriteListJNDIDAO implements FavoriteListDAO_interface {
//	private static DataSource ds = null;
//    static {
//        try {
//           Context ctx = new InitialContext();
//            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB1");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }
//
//	private static final String INSERT_STMT =
//			"INSERT INTO FAVORITE_LIST (mem_Id,prod_Id) VALUES (?,?)";
//	private static final String GET_ALL_STMT =
//			"SELECT mem_Id,prod_Id FROM favorite_list";
//	private static final String GET_ONE_STMT =
//			"SELECT mem_Id,prod_Id FROM favorite_list WHERE mem_Id = ? AND prod_Id = ?";
//	private static final String DELETE_ONE =
//	        "DELETE FROM favorite_list WHERE mem_id = ? AND prod_id = ?";
//	private static final String DELETE_ALL =
//	        "DELETE FROM favorite_list WHERE mem_id = ?";
//
//	@Override
//	public void insert(FavoriteListVO favoriteListVO) {
//		Connection con = null;
//		PreparedStatement pstmt = null;
//
//		try {
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(INSERT_STMT);
//
//			pstmt.setInt(1, favoriteListVO.getMemId());
//			pstmt.setInt(2, favoriteListVO.getProdId());
//
//			pstmt.executeUpdate();
//		}catch (SQLException se) {
//			throw new RuntimeException("A database error occured." + se.getMessage());
//		}finally {
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
//	}
//	@Override
//	public void deleteByMemIdAndProdId(Integer memId, Integer prodId) {
//        try (Connection con = ds.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(DELETE_ONE)) {
//            pstmt.setInt(1, memId);
//            pstmt.setInt(2, prodId);
//            pstmt.executeUpdate();
//        } catch (SQLException se) {
//            throw new RuntimeException("A database error occurred: " + se.getMessage());
//        }
//    }
//	@Override
//    public void deleteAllByMemId(Integer memId) {
//        try (Connection con = ds.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(DELETE_ALL)) {
//            pstmt.setInt(1, memId);
//            pstmt.executeUpdate();
//        } catch (SQLException se) {
//            throw new RuntimeException("A database error occurred: " + se.getMessage());
//        }
//    }
//
//	@Override
//	public FavoriteListVO findByPrimaryKey(Integer memId, Integer prodId) {
//		FavoriteListVO favoriteListVO = null;
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(GET_ONE_STMT);
//
//			pstmt.setInt(1, memId);
//			pstmt.setInt(2, prodId);
//
//			rs = pstmt.executeQuery();
//
//			while (rs.next()) {
//				favoriteListVO = new FavoriteListVO();
//				favoriteListVO.setMemId(rs.getInt("MEM_ID"));
//				favoriteListVO.setProdId(rs.getInt("PROD_ID"));
//			}
//		}catch (SQLException se) {
//			throw new RuntimeException("A database error occured. " + se.getMessage());
//			// Clean up JDBC resources
//		}finally {
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
//		return favoriteListVO;
//	}
//	@Override
//	public List<FavoriteListVO> getAll(){
//		List<FavoriteListVO> list = new ArrayList<FavoriteListVO>();
//		FavoriteListVO favoriteListVO = null;
//
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = ds.getConnection();
//			pstmt = con.prepareStatement(GET_ALL_STMT);
//			rs = pstmt.executeQuery();
//
//			while (rs.next()) {
//
//				favoriteListVO = new FavoriteListVO();
//				favoriteListVO.setMemId(rs.getInt("MEM_ID"));
//				favoriteListVO.setProdId(rs.getInt("PROD_ID"));
//
//				list.add(favoriteListVO);
//		}
//	}catch (SQLException se) {
//		throw new RuntimeException("A database error occured. " + se.getMessage());
//		// Clean up JDBC resources
//		}finally {
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
//	};
//}
