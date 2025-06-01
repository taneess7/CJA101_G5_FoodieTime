package com.foodietime.smg.model;

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


public class SmgDAO implements SmgDAO_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB1");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
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
	@Override
	public void insert(SmgVO smgVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setString(1, smgVO.getSmgrEmail());
			pstmt.setString(2, smgVO.getSmgrAccount());
			pstmt.setString(3, smgVO.getSmgrPassword());
			pstmt.setString(4, smgVO.getSmgrName());
			pstmt.setString(5, smgVO.getSmgrPhone());

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
	public void update(SmgVO smgVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
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

			con = ds.getConnection();
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
				smgVO.setSmgrStatus(rs.getInt("smgr_status"));
				smgVO.setSmgId(rs.getInt("smgr_Id"));
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
		return smgVO;
	}
	
	@Override
	public boolean isAccountExist(String smgrAccount) {
		boolean exists = false;
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        con = ds.getConnection();
	        pstmt = con.prepareStatement(GET_ALL_ACCOUNT);
	        pstmt.setString(1, smgrAccount);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            exists = true; // 有資料就代表帳號存在
	        }

	    } catch (SQLException se) {
	        throw new RuntimeException("A database error occurred: " + se.getMessage());
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

	    return exists;
	}

	@Override
	public List<SmgVO> getAll() {
		List<SmgVO> list = new ArrayList<SmgVO>();
		SmgVO smgVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
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
