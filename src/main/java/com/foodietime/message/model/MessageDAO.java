package com.foodietime.message.model;

import java.sql.*;
import java.util.*;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessageDAO implements MessageDAO_interface{
	
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB87");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private static final String INSERT_STMT = "INSERT INTO MESSAGE (POST_ID, MEM_ID, MES_DATE, MES_CONTENT) VALUES (?,?,?,?)";
	private static final String GET_ALL_STMT = "SELECT MES_ID, POST_ID, MEM_ID, MES_DATE, MES_CONTENT FROM MESSAGE ORDER BY MES_ID";
	private static final String GET_ONE_STMT = "SELECT MES_ID, POST_ID, MEM_ID, MES_DATE, MES_CONTENT FROM MESSAGE WHERE MES_ID = ?";
	private static final String DELETE = "DELETE FROM MESSAGE WHERE MES_ID = ?";
	private static final String UPDATE = "UPDATE MESSAGE SET POST_ID = ?, MEM_ID = ?, MES_DATE = ?, MES_CONTENT = ?  WHERE MES_ID = ?";
	private static final String GET_BY_MEM_ID = "SELECT MES_ID, POST_ID, MEM_ID, MES_DATE, MES_CONTENT FROM MESSAGE WHERE MEM_ID = ? ORDER BY MES_ID";
	private static final String GET_BY_POST_ID = "SELECT MES_ID, POST_ID, MEM_ID, MES_DATE, MES_CONTENT FROM MESSAGE WHERE POST_ID = ? ORDER BY MES_ID";
	
	@Override
	public void insert(MessageVO messageVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);
			
			pstmt.setInt(1, messageVO.getPostId().getPostId());
			pstmt.setInt(2, messageVO.getMemId().getMemId());
			pstmt.setTimestamp(3, messageVO.getMesDate());
			pstmt.setString(4, messageVO.getMesContent());
			
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
	public void update(MessageVO messageVO) {
		 System.out.println("Updating message: " + messageVO.getMesId());
		    System.out.println("Content: " + messageVO.getMesContent());
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE);
			
			pstmt.setInt(1, messageVO.getPostId().getPostId());   //PostVO 裡面的 getPostId() 方法
			pstmt.setInt(2, messageVO.getMemId().getMemId());
			pstmt.setTimestamp(3, messageVO.getMesDate());
			pstmt.setString(4, messageVO.getMesContent());
			pstmt.setInt(5, messageVO.getMesId());
			
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
	public void delete(Integer mesId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE);
			
			
			pstmt.setInt(1, mesId);
			
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
	public MessageVO findByPrimaryKey(Integer mesId) {
		MessageVO messageVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		System.out.println("DAO 收到的 mesId = " + mesId);
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE_STMT);
			
			pstmt.setInt(1, mesId);  //參數注入到?
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				messageVO = new MessageVO();
				messageVO.setMesId(rs.getInt("MES_ID"));
//				messageVO.setPostId(rs.getInt("POST_ID"));
//				messageVO.setMemId(rs.getInt("MEM_ID"));
				messageVO.setMesDate(rs.getTimestamp("MES_DATE"));
				messageVO.setMesContent(rs.getString("MES_CONTENT"));
				
							
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
		
		return messageVO;
	}
	@Override
	public List<MessageVO> getALL() {		
		List<MessageVO> list = new ArrayList<MessageVO>();
		MessageVO messageVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			System.out.println("DEBUG SQL: " + GET_ALL_STMT);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				messageVO = new MessageVO();
				messageVO.setMesId(rs.getInt("MES_ID"));
//				messageVO.setPostId(rs.getInt("POST_ID"));
//				messageVO.setMemId(rs.getInt("MEM_ID"));
				messageVO.setMesDate(rs.getTimestamp("MES_DATE"));
				messageVO.setMesContent(rs.getString("MES_CONTENT"));
				list.add(messageVO);
				
							
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
	@Override
	public List<MessageVO> findByMemId(Integer memId) {
		List<MessageVO> List = new ArrayList<MessageVO>();
		MessageVO messageVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_BY_MEM_ID);
			pstmt.setInt(1, memId);						
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				messageVO = new MessageVO();
				messageVO.setMesId(rs.getInt("MES_ID"));
//				messageVO.setPostId(rs.getInt("POST_ID"));
//				messageVO.setMemId(rs.getInt("MEM_ID"));
				messageVO.setMesDate(rs.getTimestamp("MES_DATE"));
				messageVO.setMesContent(rs.getString("MES_CONTENT"));
				List.add(messageVO);
				
							
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		} finally {
			if ( rs != null) {
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
		
		return List;
	}
		
	@Override
	public List<MessageVO> findByPostId(Integer postId) {
		List<MessageVO> List = new ArrayList<MessageVO>();
		MessageVO messageVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_BY_POST_ID);
			pstmt.setInt(1, postId);						
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				messageVO = new MessageVO();
				messageVO.setMesId(rs.getInt("MES_ID"));
//				messageVO.setPostId(rs.getInt("POST_ID"));
//				messageVO.setMemId(rs.getInt("MEM_ID"));
				messageVO.setMesDate(rs.getTimestamp("MES_DATE"));
				messageVO.setMesContent(rs.getString("MES_CONTENT"));
				List.add(messageVO);
				
							
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		} finally {
			if ( rs != null) {
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
		
		return List;
	}
	
	
	

}
