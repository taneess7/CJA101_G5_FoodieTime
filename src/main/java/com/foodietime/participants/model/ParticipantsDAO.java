package com.foodietime.participants.model;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipantsDAO implements ParticipantsDAO_interface {
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB1");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String INSERT_STMT = "INSERT INTO PARTICIPANTS (PAR_ID, MEM_ID, GB_ID, PAR_PHONE, PAR_NAME, PAR_ADDRESS, PAR_LONGITUDE, PAR_LATITUDE, IS_LEADER, PAR_PURCHASE_QUANTITY, PAYMENT_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE = "UPDATE PARTICIPANTS SET MEM_ID = ?, GB_ID = ?, PAR_PHONE = ?, PAR_NAME = ?, PAR_ADDRESS = ?, PAR_LONGITUDE = ?, PAR_LATITUDE = ?, IS_LEADER = ?, PAR_PURCHASE_QUANTITY = ?, PAYMENT_STATUS = ? WHERE PAR_ID = ? ";
	private static final String GET_ONE_STMT = "SELECT * FROM PARTICIPANTS WHERE PAR_ID = ? ";
	private static final String GET_ALL_STMT = "SELECT * FROM PARTICIPANTS ORDER BY PAR_ID";
	private static final String GET_BY_GBID = "SELECT * FROM PARTICIPANTS WHERE GB_ID = ?";

	@Override
	public void insert(ParticipantsVO participantsVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setInt(1, participantsVO.getParId());
			pstmt.setInt(2, participantsVO.getMemId());
			pstmt.setInt(3, participantsVO.getGbId());
			pstmt.setString(4, participantsVO.getParPhone());
			pstmt.setString(5, participantsVO.getParName());
			pstmt.setString(6, participantsVO.getParAddress());
			pstmt.setBigDecimal(7, participantsVO.getParLongitude());
			pstmt.setBigDecimal(8, participantsVO.getParLatitude());
			pstmt.setBoolean(9, participantsVO.isLeader());
			pstmt.setInt(10, participantsVO.getParPurchaseQuantity());
			pstmt.setByte(11, participantsVO.getPaymentStatus());

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
	public void update(ParticipantsVO participantsVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE); 

			pstmt.setInt(1, participantsVO.getParId());
			pstmt.setInt(2, participantsVO.getMemId());
			pstmt.setInt(3, participantsVO.getGbId());
			pstmt.setString(4, participantsVO.getParPhone());
			pstmt.setString(5, participantsVO.getParName());
			pstmt.setString(6, participantsVO.getParAddress());
			pstmt.setBigDecimal(7, participantsVO.getParLongitude());
			pstmt.setBigDecimal(8, participantsVO.getParLatitude());
			pstmt.setBoolean(9, participantsVO.isLeader());
			pstmt.setInt(10, participantsVO.getParPurchaseQuantity());
			pstmt.setByte(11, participantsVO.getPaymentStatus());

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
	public ParticipantsVO findByPrimaryKey(Integer parId) {
		ParticipantsVO participantsVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            
            pstmt = con.prepareStatement(GET_ONE_STMT);

            pstmt.setInt(1, parId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
            	participantsVO = new ParticipantsVO();
            	participantsVO.setParId(rs.getInt("PAR_ID"));
            	participantsVO.setMemId(rs.getInt("MEM_ID"));
            	participantsVO.setGbId(rs.getInt("GB_ID"));
            	participantsVO.setParPhone(rs.getString("PAR_PHONE"));
            	participantsVO.setParName(rs.getString("PAR_NAME"));
            	participantsVO.setParAddress(rs.getString("PAR_ADDRESS"));
            	participantsVO.setParLongitude(rs.getBigDecimal("PAR_LONGITUDE"));
            	participantsVO.setParLatitude(rs.getBigDecimal("PAR_LATITUDE"));
            	participantsVO.setLeader(rs.getBoolean("IS_LEADER"));
            	participantsVO.setParPurchaseQuantity(rs.getInt("PAR_PURCHASE_QUANTITY"));
            	participantsVO.setPaymentStatus(rs.getByte("PAYMENT_STATUS"));
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
        return participantsVO;
	}

	@Override
	public List<ParticipantsVO> getAll() {
		 List<ParticipantsVO> list = new ArrayList<ParticipantsVO>();
		 ParticipantsVO participantsVO = null;

	        Connection con = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;

	        try {
	            con = ds.getConnection();

	            pstmt = con.prepareStatement(GET_ALL_STMT);
	            rs = pstmt.executeQuery();

	            while (rs.next()) {
	            	participantsVO = new ParticipantsVO();
	            	participantsVO.setParId(rs.getInt("PAR_ID"));
	            	participantsVO.setMemId(rs.getInt("MEM_ID"));
	            	participantsVO.setGbId(rs.getInt("GB_ID"));
	            	participantsVO.setParPhone(rs.getString("PAR_PHONE"));
	            	participantsVO.setParName(rs.getString("PAR_NAME"));
	            	participantsVO.setParAddress(rs.getString("PAR_ADDRESS"));
	            	participantsVO.setParLongitude(rs.getBigDecimal("PAR_LONGITUDE"));
	            	participantsVO.setParLatitude(rs.getBigDecimal("PAR_LATITUDE"));
	            	participantsVO.setLeader(rs.getBoolean("IS_LEADER"));
	            	participantsVO.setParPurchaseQuantity(rs.getInt("PAR_PURCHASE_QUANTITY"));
	            	participantsVO.setPaymentStatus(rs.getByte("PAYMENT_STATUS"));
	                
	                list.add(participantsVO);
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
	public List<ParticipantsVO> findByGBId(Integer gbId) {
		List<ParticipantsVO> list = new ArrayList<ParticipantsVO>();
		ParticipantsVO participantsVO = null;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            
            pstmt = con.prepareStatement(GET_BY_GBID);
            pstmt.setInt(1, gbId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	participantsVO = new ParticipantsVO();
            	participantsVO.setParId(rs.getInt("PAR_ID"));
            	participantsVO.setMemId(rs.getInt("MEM_ID"));
            	participantsVO.setGbId(rs.getInt("GB_ID"));
            	participantsVO.setParPhone(rs.getString("PAR_PHONE"));
            	participantsVO.setParName(rs.getString("PAR_NAME"));
            	participantsVO.setParAddress(rs.getString("PAR_ADDRESS"));
            	participantsVO.setParLongitude(rs.getBigDecimal("PAR_LONGITUDE"));
            	participantsVO.setParLatitude(rs.getBigDecimal("PAR_LATITUDE"));
            	participantsVO.setLeader(rs.getBoolean("IS_LEADER"));
            	participantsVO.setParPurchaseQuantity(rs.getInt("PAR_PURCHASE_QUANTITY"));
            	participantsVO.setPaymentStatus(rs.getByte("PAYMENT_STATUS"));
                list.add(participantsVO);
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
