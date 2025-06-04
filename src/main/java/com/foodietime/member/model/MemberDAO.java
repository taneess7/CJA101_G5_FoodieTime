package com.foodietime.member.model;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class MemberDAO implements MemDAO_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB3");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	private static final String INSERT_MEMBER = 
			"INSERT INTO MEMBER (MEM_EMAIL,MEM_ACCOUNT,MEM_PASSWORD,MEM_NICKNAME,MEM_NAME,MEM_PHONE,MEM_GENDER,MEM_CITY,MEM_CITYAREA,MEM_ADDRESS,MEM_CODE,MEM_AVATAR,MEM_TIME)VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String GET_ALL_MEMBER = 
			"SELECT MEM_ID,MEM_EMAIL,MEM_ACCOUNT,MEM_PASSWORD,MEM_NICKNAME,MEM_NAME,MEM_PHONE,MEM_GENDER,MEM_CITY,MEM_CITYAREA,MEM_ADDRESS,MEM_CODE,MEM_AVATAR,MEM_TIME,MEM_STATUS,MEM_NO_SPEAK,MEM_NO_POST,MEM_NO_GROUP,MEM_NO_JOINGROUP,TOTAL_STAR_NUM,TOTAL_REVIEWS FROM MEMBER order by MEM_ID";
	private static final String GET_ONE_MEMBER = 
			"SELECT MEM_ID,MEM_EMAIL,MEM_ACCOUNT,MEM_PASSWORD,MEM_NICKNAME,MEM_NAME,MEM_PHONE,MEM_GENDER,MEM_CITY,MEM_CITYAREA,MEM_ADDRESS,MEM_CODE,MEM_AVATAR,MEM_TIME,MEM_STATUS,MEM_NO_SPEAK,MEM_NO_POST,MEM_NO_GROUP,MEM_NO_JOINGROUP,TOTAL_STAR_NUM,TOTAL_REVIEWS FROM MEMBER where MEM_ID = ?";
	private static final String MEMBER_UPDATE = 
			"UPDATE MEMBER set MEMBER_EMAIL = ?, MEM_PASSWORD = ?, MEN_NICKNAME = ?, MEM_NAME = ?, MEM_PHONE = ?, MEM_GENDER = ?, MEM_CITY = ?, MEM_CITYAREA = ? ,MEM_ADDRESS = ?, MEM_AVATAR = ?,  where MEM_ID = ?";
	private static final String MEMBER_PERMISSION_UPDATE = 
			"UPDATE MEMBER set MEMBER_STATUS = ?, MEM_NO_SPEAK = ?, MEM_NO_POST = ?, MEM_NO_GROUP = ?, MEM_NO_JOINGROUP = ? where MEM_ID = ?";
	private static final String GET_ALL_ACCOUNT =
			"SELECT 1 FROM MEMBER WHERE MEMBER_ACCOUNT = ?";
	
	@Override
	public void insert(MemberVO memberVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_MEMBER);

			pstmt.setString(1, memberVO.getMemEmail());
			pstmt.setString(2, memberVO.getMemAccount());
			pstmt.setString(3, memberVO.getMemPassword());
			pstmt.setString(4, memberVO.getMemNickname());
			pstmt.setString(5, memberVO.getMemName());
			pstmt.setString(6, memberVO.getMemPhone());
			pstmt.setInt(7, memberVO.getMemGender());
			pstmt.setString(8, memberVO.getMemCity());
			pstmt.setString(9, memberVO.getMemCityarea());
			pstmt.setString(10, memberVO.getMemAddress());
			pstmt.setBytes(11, memberVO.getMemAvatar());
			pstmt.setTimestamp(12, memberVO.getMemTime());
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

	@Override
	public void update(MemberVO memberVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(MEMBER_UPDATE);

			pstmt.setString(1, memberVO.getMemEmail());
			pstmt.setString(2, memberVO.getMemPassword());
			pstmt.setString(3, memberVO.getMemNickname());
			pstmt.setString(4, memberVO.getMemName());
			pstmt.setString(5, memberVO.getMemPhone());
			pstmt.setInt(6, memberVO.getMemGender());
			pstmt.setString(7, memberVO.getMemCity());
			pstmt.setString(8, memberVO.getMemCityarea());
			pstmt.setString(9, memberVO.getMemAddress());
			pstmt.setBytes(10, memberVO.getMemAvatar());
//			pstmt.setDate(11, memberVO.getMemTime());
//			pstmt.setInt(12, memberVO.getTotalStarNum());
//			pstmt.setInt(13, memberVO.getTotalReviews());
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

	public void updatePermission(MemberVO memberVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(MEMBER_PERMISSION_UPDATE);

			pstmt.setInt(1, memberVO.getMemStatus());
			pstmt.setInt(2, memberVO.getMemNoSpeak());
			pstmt.setInt(3, memberVO.getMemNoPost());
			pstmt.setInt(4, memberVO.getMemNoGroup());
			pstmt.setInt(5, memberVO.getMemNoJoingroup());
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

	@Override
	public MemberVO findByPrimaryKey(Integer memId) {
		MemberVO memberVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE_MEMBER);

			pstmt.setInt(1, memId);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				memberVO = new MemberVO();
				memberVO.setMemId(rs.getInt("MEM_ID"));
				memberVO.setMemEmail(rs.getString("MEM_EMAIL"));
				memberVO.setMemAccount(rs.getString("MEM_ACCOUNT"));
				memberVO.setMemPassword(rs.getString("MEM_PASSWORD"));
				memberVO.setMemNickname(rs.getString("MEM_NICKNAME"));
				memberVO.setMemName(rs.getString("MEM_NAME"));
				memberVO.setMemPhone(rs.getString("MEM_PHONE"));
				memberVO.setMemGender(rs.getInt("MEM_GENDER"));
				memberVO.setMemCity(rs.getString("MEM_CITY"));
				memberVO.setMemCityarea(rs.getString("MEM_CITYAREA"));
				memberVO.setMemAddress(rs.getString("MEM_ADDRESS"));
				memberVO.setMemCode(rs.getString("MEM_CODE"));
				memberVO.setMemAvatar(rs.getBytes("MEM_AVATAR"));
				memberVO.setMemTime(rs.getTimestamp("MEM_TIME"));
				memberVO.setMemStatus(rs.getInt("MEM_STATUS"));
				memberVO.setMemNoSpeak(rs.getInt("MEM_MEM_NO_SPEAK"));
				memberVO.setMemNoPost(rs.getInt("MEM_NO_POST"));
				memberVO.setMemNoGroup(rs.getInt("MEM_NO_GROUP"));
				memberVO.setMemNoJoingroup(rs.getInt("MEM_NO_JOINGROUP"));
				memberVO.setTotalStarNum(rs.getInt("TOTAL_STAR_NUM"));
				memberVO.setTotalReviews(rs.getInt("TOTAL_REVIEWS"));
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
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
		return memberVO;
	}

	@Override
	public List<MemberVO> getAll() {
		List<MemberVO> list = new ArrayList<MemberVO>();
		MemberVO memberVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_MEMBER);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				memberVO = new MemberVO();
				memberVO.setMemId(rs.getInt("MEM_ID"));
				memberVO.setMemEmail(rs.getString("MEM_EMAIL"));
				memberVO.setMemAccount(rs.getString("MEM_ACCOUNT"));
				memberVO.setMemPassword(rs.getString("MEM_PASSWORD"));
				memberVO.setMemNickname(rs.getString("MEM_NICKNAME"));
				memberVO.setMemName(rs.getString("MEM_NAME"));
				memberVO.setMemPhone(rs.getString("MEM_PHONE"));
				memberVO.setMemGender(rs.getInt("MEM_GENDER"));
				memberVO.setMemCity(rs.getString("MEM_CITY"));
				memberVO.setMemCityarea(rs.getString("MEM_CITYAREA"));
				memberVO.setMemAddress(rs.getString("MEM_ADDRESS"));
				memberVO.setMemCode(rs.getString("MEM_CODE"));
				memberVO.setMemAvatar(rs.getBytes("MEM_AVATAR"));
				memberVO.setMemTime(rs.getTimestamp("MEM_TIME"));
				memberVO.setMemStatus(rs.getInt("MEM_STATUS"));
				memberVO.setMemNoSpeak(rs.getInt("MEM_MEM_NO_SPEAK"));
				memberVO.setMemNoPost(rs.getInt("MEM_NO_POST"));
				memberVO.setMemNoGroup(rs.getInt("MEM_NO_GROUP"));
				memberVO.setMemNoJoingroup(rs.getInt("MEM_NO_JOINGROUP"));
				memberVO.setTotalStarNum(rs.getInt("TOTAL_STAR_NUM"));
				memberVO.setTotalReviews(rs.getInt("TOTAL_REVIEWS"));
				list.add(memberVO);

			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
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
	public boolean isAccountExist(String memAccount) {
		boolean exists = false;
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        con = ds.getConnection();
	        pstmt = con.prepareStatement(GET_ALL_ACCOUNT);
	        pstmt.setString(1, memAccount);
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
	
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public void updatePhoto(Integer memId, byte[] memAvatar) {
		
	    MemberVO member = entityManager.find(MemberVO.class, memId);
	    if (member != null) {
	        member.setMemAvatar(memAvatar);
	        entityManager.merge(member);
	    }
	}
	
}
