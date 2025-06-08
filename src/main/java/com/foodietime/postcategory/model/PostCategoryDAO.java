package com.foodietime.postcategory.model;

import java.sql.*;
import java.util.*;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

public class PostCategoryDAO implements PostCategoryDAO_interface{
	
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB87");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private static final String INSERT_STMT = "INSERT INTO POST_CATEGORY (POST_CATE) VALUES (?)";
	private static final String GET_ALL_STMT = "SELECT POST_CATE_ID, POST_CATE FROM POST_CATEGORY ORDER BY POST_CATE_ID";
	private static final String GET_ONE_STMT = "SELECT POST_CATE_ID, POST_CATE FROM POST_CATEGORY WHERE POST_CATE_ID = ?";
	private static final String DELETE = "DELETE FROM POST_CATEGORY WHERE POST_CATE_ID = ?";
	private static final String UPDATE = "UPDATE POST_CATEGORY SET POST_CATE_ID = ?, POST_CATE = ?  WHERE POST_CATE_ID_ID = ?";

	
	@Override
	public void insert(PostCategoryVO postCategoryVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);
		
			pstmt.setInt(1, postCategoryVO.getPostCate());			
			
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch(SQLException se) {
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
	public void update(PostCategoryVO postCategoryVO) {
		 System.out.println("Updating postCategory: " + postCategoryVO.getPostCateId());
		    System.out.println("Content: " + postCategoryVO.getPostCate());
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE);
			
			pstmt.setInt(1, postCategoryVO.getPostCateId());
			pstmt.setInt(2, postCategoryVO.getPostCate());

			
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch(SQLException se) {
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
	public void delete(Integer postCateId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE);
			
			
			pstmt.setInt(1, postCateId);
			
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch(SQLException se) {
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
	public PostCategoryVO findByPK(Integer postCateId) {
		PostCategoryVO postCategoryVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE_STMT);
			
			pstmt.setInt(1, postCateId);  //參數注入到?
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				postCategoryVO = new PostCategoryVO();
				postCategoryVO.setPostCateId(rs.getInt("POST_CATE_ID"));
				postCategoryVO.setPostCate(rs.getInt("POST_CATE"));
				
				
							
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch(SQLException se) {
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
		
		return postCategoryVO;
	}
	@Override
	public List<PostCategoryVO> getAll() {		
		List<PostCategoryVO> list = new ArrayList<PostCategoryVO>();
		PostCategoryVO postCategoryVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			System.out.println("DEBUG SQL: " + GET_ALL_STMT);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				postCategoryVO = new PostCategoryVO();
				postCategoryVO.setPostCateId(rs.getInt("POST_CATE_ID"));
				postCategoryVO.setPostCate(rs.getInt("POST_CATE"));
				
				list.add(postCategoryVO);
				
							
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
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
				} catch(SQLException se) {
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
