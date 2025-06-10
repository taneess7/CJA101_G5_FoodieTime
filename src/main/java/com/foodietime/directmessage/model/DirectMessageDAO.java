package com.foodietime.directmessage.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.foodietime.member.model.MemberVO;

public class DirectMessageDAO implements DirectMessageDAO_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB3");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	private static final String INSERT = 
			"INSERT INTO DIRECT_MESSAGE (MEM_ID,SMGR_ID,MESS_CONTENT,MESS_TIME,MESS_DIRECTION) VALUES (?, ?, ?, ?, ?)";
	private static final String GET_ALL= 
			"SELECT DM_ID,MEM_ID,SMGR_ID,MESS_CONTENT,MESS_TIME,MESS_DIRECTION FROM DIRECT_MESSAGE order by DM_ID";
	private static final String GET_ONE = 
			"SELECT DM_ID,MEM_ID,SMGR_ID,MESS_CONTENT,MESS_TIME,MESS_DIRECTION FROM DIRECT_MESSAGE where DM_ID=?";
	private static final String UPDATE = 
			"UPDATE DIRECT_MESSAGE set MEM_ID=?, SMGR_ID=?, MESS_CONTENT=?, MESS_DIRECTION=? where DM_ID = ?";
	private static final String DELETE = "DELETE FROM DIRECT_MESSAGE WHERE DM_ID=?";
	public void insert(DirectMessageVO dmVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT);

			pstmt.setInt(1, dmVO.getMember().getMemId());
			pstmt.setInt(2, dmVO.getSmgrId());
			pstmt.setString(3, dmVO.getMessContent());
			pstmt.setTimestamp(4, dmVO.getMessTime());
			pstmt.setInt(5, dmVO.getMessDirection());

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

	public void update(DirectMessageVO dmVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setInt(1, dmVO.getMember().getMemId());
			pstmt.setInt(2, dmVO.getSmgrId());
			pstmt.setString(3, dmVO.getMessContent());
			pstmt.setTimestamp(4, dmVO.getMessTime());
			pstmt.setInt(5, dmVO.getMessDirection());
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
	public DirectMessageVO findByPrimaryKey(Integer dmId) {
		DirectMessageVO dmVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE);

			pstmt.setInt(1, dmId);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// empVo 也稱為 Domain objects
				dmVO = new DirectMessageVO();
				MemberVO member = new MemberVO();
				member.setMemId(rs.getInt("MEM_ID"));
				dmVO.setMember(member);
				dmVO.setSmgrId(rs.getInt("SMGR_ID"));
				dmVO.setMessContent(rs.getString("MESS_CONTENT"));
				dmVO.setMessTime(rs.getTimestamp("MESS_TIME"));
				dmVO.setMessDirection(rs.getInt("MESS_DIRECTION"));
				dmVO.setDmId(rs.getInt("DM_ID"));
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
		return dmVO;
	}
	
	@Override
	public List<DirectMessageVO> getAll() {
		List<DirectMessageVO> list = new ArrayList<DirectMessageVO>();
		DirectMessageVO dmVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				dmVO = new DirectMessageVO();
				MemberVO member = new MemberVO();
				member.setMemId(rs.getInt("MEM_ID"));
				dmVO.setMember(member);
				dmVO.setSmgrId(rs.getInt("SMGR_ID"));
				dmVO.setMessContent(rs.getString("MESS_CONTENT"));
				dmVO.setMessTime(rs.getTimestamp("MESS_TIME"));
				dmVO.setMessDirection(rs.getInt("MESS_DIRECTION"));
				dmVO.setDmId(rs.getInt("DM_ID"));;
				list.add(dmVO); // Store the row in the list
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
	public void delete(Integer dmId) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, dmId);

			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
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
	
}
