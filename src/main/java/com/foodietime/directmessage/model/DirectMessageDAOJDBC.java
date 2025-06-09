package com.foodietime.directmessage.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.foodietime.member.model.MemberVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DirectMessageDAOJDBC implements DirectMessageDAO_interface {
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/g05?serverTimezone=Asia/Taipei";
	String userid = "root";
	String passwd = "ABLEpassedaway5236";
	private static final String INSERT = 
			"INSERT INTO DIRECT_MESSAGE (MEM_ID,SMGR_ID,MESS_CONTENT,MESS_TIME,MESS_DIRECTION) VALUES (?,?, ?, ?, ?)";
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

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(INSERT);

			pstmt.setInt(1, dmVO.getMember().getMemId());
			pstmt.setInt(2, dmVO.getSmgrId());
			pstmt.setString(3, dmVO.getMessContent());
			pstmt.setTimestamp(4, new java.sql.Timestamp(dmVO.getMessTime().getTime()));
			pstmt.setInt(5, dmVO.getMessDirection());

			pstmt.executeUpdate();

			// Handle any SQL errors
		}catch(ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. " + e.getMessage());
		}catch (SQLException se) {
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
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setInt(1, dmVO.getMember().getMemId());
			pstmt.setInt(2, dmVO.getSmgrId());
			pstmt.setString(3, dmVO.getMessContent());
			pstmt.setTimestamp(4, new java.sql.Timestamp(dmVO.getMessTime().getTime()));
			pstmt.setInt(6, dmVO.getDmId());
			
			pstmt.executeUpdate();

			// Handle any driver errors
		}catch(ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. " + e.getMessage());
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

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);

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
		}catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			}
 
		catch (SQLException se) {
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

			Class.forName(driver);
			con = DriverManager.getConnection(url,userid,passwd);
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
		}catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			}
 
		catch (SQLException se) {
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
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, dmId);

			pstmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			}

		catch (SQLException se) {
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
	public static void main(String[] args) {
		DirectMessageDAOJDBC dao = new DirectMessageDAOJDBC();
//		Date now = new Date() ;
//		long long1 = now.getTime();
//		java.sql.Date date1 = new java.sql.Date(long1);
//		java.util.Date now = new java.util.Date(new Date().getTime());
//		LocalDateTime now = LocalDateTime.now();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		String formattedDate = now.format(formatter);
//		System.out.println("格式化后的时间：" + formattedDate);
//		Calendar calendar = Calendar.getInstance();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		 //新增
//		DirectMessageVO dmVO1 = new DirectMessageVO();
//		dmVO1.setMemId(1);
//		dmVO1.setSmgrId(1);
//		dmVO1.setMessContent("你好");
//		dmVO1.setMessTime("2025-05-07");
//		dmVO1.setMessDirection(1);
//		dao.insert(dmVO1);
		
		// 修改
//		DirectMessageVO dmVO2 = new DirectMessageVO();
//		dmVO2.setDmId(16);
//		dmVO2.setMemId(6);
//		dmVO2.setSmgrId(6);
//		dmVO2.setMessContent("測試測試");
//		dmVO2.setMessTime(now);
//		dmVO2.setMessDirection(0);
//		dao.update(dmVO2);
		// 查詢
//		DirectMessageVO dmVO3 = dao.findByPrimaryKey(16);
//		System.out.print(dmVO3.getDmId() + ",");
//		System.out.print(dmVO3.getMemId() + ",");
//		System.out.print(dmVO3.getSmgrId() + ",");
//		System.out.print(dmVO3.getMessContent() + ",");
//		System.out.print(dmVO3.getMessTime() + ",");
//		System.out.print(dmVO3.getMessDirection() + ",");
//		System.out.println("---------------------");

		// 查詢
//		List<DirectMessageVO> list = dao.getAll();
//		for (DirectMessageVO aDm : list) {
//			System.out.print(aDm.getDmId() + ",");
//			System.out.print(aDm.getMemId() + ",");
//			System.out.print(aDm.getSmgrId() + ",");
//			System.out.print(aDm.getMessContent() + ",");
//			System.out.print(aDm.getMessTime() + ",");
//			System.out.print(aDm.getMessDirection() + ",");
//			System.out.println();
//		}
//	}
	//刪除
//		DirectMessageVO dmVO4 = new DirectMessageVO();
//		dmVO4.setDmId(16);
//		dao.delete(16);
	}
}
