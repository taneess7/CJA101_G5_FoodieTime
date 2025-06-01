package com.foodietime.smg.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class SmgJDBCDAO implements SmgDAO_interface {

	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/g05?serverTimezone=Asia/Taipei";
	String userid = "root";
	String passwd = "maybeormaybe0214";
		
	private static final String INSERT_STMT = 
			"INSERT INTO servermanager (smgr_email,smgr_account,smgr_password,smgr_name,smgr_phone) VALUES (?, ?, ?, ?, ?)";
	private static final String GET_ALL_STMT = 
			"SELECT smgr_id,smgr_email,smgr_account,smgr_password,smgr_name,smgr_phone,smgr_status FROM servermanager order by smgr_id";
	private static final String GET_ONE_STMT = 
			"SELECT smgr_id,smgr_email,smgr_account,smgr_password,smgr_name,smgr_phone,smgr_status FROM servermanager where smgr_id = ?";
	private static final String UPDATE = 
			"UPDATE servermanager set smgr_email=?, smgr_account=?, smgr_password=?, smgr_name=?, smgr_phone=?, smgr_status=? where smgr_id = ?";
	private static final String GET_ALL_ACCOUNT =
			"SELECT 1 FROM servermanager WHERE smgr_account = ?";
	
	public boolean isAccountExist(String smgrAccount) {
		boolean exists = false;
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        Class.forName(driver);
	        con = DriverManager.getConnection(url, userid, passwd);
	        pstmt = con.prepareStatement(GET_ALL_ACCOUNT);
	        pstmt.setString(1, smgrAccount);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            exists = true; // 有查到資料就代表帳號存在
	        }

	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException("Couldn't load database driver: " + e.getMessage());
	    } catch (SQLException se) {
	        throw new RuntimeException("A database error occurred: " + se.getMessage());
	    } finally {
	        // 資源關閉
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

	    return exists;
	}
	@Override
	public void insert(SmgVO smgVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setString(1, smgVO.getSmgrEmail());
			pstmt.setString(2, smgVO.getSmgrAccount());
			pstmt.setString(3, smgVO.getSmgrPassword());
			pstmt.setString(4, smgVO.getSmgrName());
			pstmt.setString(5, smgVO.getSmgrPhone());

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
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
	public void update(SmgVO smgVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setString(1, smgVO.getSmgrEmail());
			pstmt.setString(2, smgVO.getSmgrAccount());
			pstmt.setString(3, smgVO.getSmgrPassword());
			pstmt.setString(4, smgVO.getSmgrName());
			pstmt.setString(5, smgVO.getSmgrPhone());
			pstmt.setInt(6, smgVO.getSmgrStatus());
			pstmt.setInt(7, smgVO.getSmgId());
			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
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
	public SmgVO findByPrimaryKey(Integer smg_Id) {
		SmgVO smgVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1, smg_Id);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// empVo 也稱為 Domain objects
				smgVO = new SmgVO();
				smgVO.setSmgrEmail(rs.getString("smgr_email"));
				smgVO.setSmgrAccount(rs.getString("smgr_account"));
				smgVO.setSmgrPassword(rs.getString("smgr_password"));
				smgVO.setSmgrName(rs.getString("smgr_name"));
				smgVO.setSmgrPhone(rs.getString("smgr_phone"));
				smgVO.setSmgId(rs.getInt("smgr_Id"));
			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
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
		return smgVO;
	}

	@Override
	public List<SmgVO> getAll() {
		List<SmgVO> list = new ArrayList<SmgVO>();
		SmgVO smgVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// empVO 也稱為 Domain objects
				smgVO = new SmgVO();
				smgVO.setSmgrEmail(rs.getString("smgr_email"));
				smgVO.setSmgrAccount(rs.getString("smgr_account"));
				smgVO.setSmgrPassword(rs.getString("smgr_password"));
				smgVO.setSmgrName(rs.getString("smgr_name"));
				smgVO.setSmgrPhone(rs.getString("smgr_phone"));
				smgVO.setSmgrStatus(rs.getInt("smgr_status"));
				smgVO.setSmgId(rs.getInt("smgr_Id"));
				list.add(smgVO); // Store the row in the list
			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
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
	public static void main(String[] args) {
		SmgJDBCDAO dao = new SmgJDBCDAO();
		
		// 新增
//		SmgVO smgVO1 = new SmgVO();
//		smgVO1.setSmgrEmail("email");
//		smgVO1.setSmgrAccount("account");
//		smgVO1.setSmgrPassword("pwd");
//		smgVO1.setSmgrName("name");
//		smgVO1.setSmgrPhone("phone");
//		dao.insert(smgVO1);
		
		// 修改
//		SmgVO smgVO2 = new SmgVO();
//		smgVO2.setSmgId(11);
//		smgVO2.setSmgrEmail("email2");
//		smgVO2.setSmgrAccount("account2");
//		smgVO2.setSmgrPassword("pwd2");
//		smgVO2.setSmgrName("name2");
//		smgVO2.setSmgrPhone("phone2");
//		smgVO2.setSmgrStatus(0);
//		dao.update(smgVO2);
		// 查詢
//		SmgVO smgVO3 = dao.findByPrimaryKey(11);
//		System.out.print(smgVO3.getSmgId() + ",");
//		System.out.print(smgVO3.getSmgrEmail() + ",");
//		System.out.print(smgVO3.getSmgrAccount() + ",");
//		System.out.print(smgVO3.getSmgrPassword() + ",");
//		System.out.print(smgVO3.getSmgrName() + ",");
//		System.out.print(smgVO3.getSmgrPhone() + ",");
//		System.out.print(smgVO3.getSmgrStatus() + ",");
//		System.out.println("---------------------");

		// 查詢
//		List<SmgVO> list = dao.getAll();
//		for (SmgVO aSmg : list) {
//			System.out.print(aSmg.getSmgId() + ",");
//			System.out.print(aSmg.getSmgrEmail() + ",");
//			System.out.print(aSmg.getSmgrAccount() + ",");
//			System.out.print(aSmg.getSmgrPassword() + ",");
//			System.out.print(aSmg.getSmgrName() + ",");
//			System.out.print(aSmg.getSmgrPhone() + ",");
//			System.out.print(aSmg.getSmgrStatus() + ",");
//			System.out.println();
//		}
		

        String testAccount = "admin1"; // 測試帳號名稱，請改成你資料庫中已存在或不存在的帳號

        boolean exists = dao.isAccountExist(testAccount);

        if (exists) {
            System.out.println("帳號 [" + testAccount + "] 已存在！");
        } else {
            System.out.println("帳號 [" + testAccount + "] 不存在，可以使用！");
        }
		
	}
}
