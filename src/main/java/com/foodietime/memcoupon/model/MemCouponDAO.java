package com.foodietime.memcoupon.model;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemCouponDAO implements MemCouponDAO_interface {

    // 一個應用程式中,針對一個資料庫 ,共用一個DataSource即可
    private static DataSource ds = null;
    static {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB3");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // SQL 語句常數定義
    private static final String INSERT_STMT =
            "INSERT INTO MEM_COUPON (MEM_ID, COU_ID, USE_STATUS) VALUES (?, ?, ?)";

    private static final String UPDATE_STMT =
            "UPDATE MEM_COUPON SET MEM_ID=?, COU_ID=?, USE_STATUS=? WHERE MEM_COU_ID=?";

    private static final String DELETE_STMT =
            "DELETE FROM MEM_COUPON WHERE MEM_COU_ID=?";

    private static final String GET_ONE_STMT =
            "SELECT MEM_COU_ID, MEM_ID, COU_ID, USE_STATUS FROM MEM_COUPON WHERE MEM_COU_ID=?";

    private static final String GET_ALL_STMT =
            "SELECT MEM_COU_ID, MEM_ID, COU_ID, USE_STATUS FROM MEM_COUPON ORDER BY MEM_COU_ID";

    private static final String GET_BY_MEM_ID_STMT =
            "SELECT MEM_COU_ID, MEM_ID, COU_ID, USE_STATUS FROM MEM_COUPON WHERE MEM_ID=? ORDER BY MEM_COU_ID DESC";

    private static final String EXISTS_BY_MEM_ID_AND_COU_ID_STMT =
            "SELECT COUNT(*) FROM MEM_COUPON WHERE MEM_ID=? AND COU_ID=?";

    private static final String GET_UNUSED_BY_MEM_ID_STMT =
            "SELECT MEM_COU_ID, MEM_ID, COU_ID, USE_STATUS FROM MEM_COUPON WHERE MEM_ID=? AND USE_STATUS=0 ORDER BY MEM_COU_ID DESC";

    private static final String GET_USED_BY_MEM_ID_STMT =
            "SELECT MEM_COU_ID, MEM_ID, COU_ID, USE_STATUS FROM MEM_COUPON WHERE MEM_ID=? AND USE_STATUS=1 ORDER BY MEM_COU_ID DESC";

    private static final String UPDATE_USE_STATUS_STMT =
            "UPDATE MEM_COUPON SET USE_STATUS=? WHERE MEM_COU_ID=?";

    private static final String COUNT_MEMBERS_BY_COU_ID_STMT =
            "SELECT COUNT(DISTINCT MEM_ID) FROM MEM_COUPON WHERE COU_ID=?";

    private static final String GET_COUPON_USAGE_STATS_STMT =
            "SELECT USE_STATUS, COUNT(*) as count FROM MEM_COUPON WHERE COU_ID=? GROUP BY USE_STATUS";

    private static final String COUNT_TOTAL_BY_MEM_ID_STMT =
            "SELECT COUNT(*) FROM MEM_COUPON WHERE MEM_ID=?";

    private static final String COUNT_UNUSED_BY_MEM_ID_STMT =
            "SELECT COUNT(*) FROM MEM_COUPON WHERE MEM_ID=? AND USE_STATUS=0";

    // *** 以下 SQL 需要使用到 COUPON 表格 ***
    private static final String GET_BY_MEM_ID_AND_COUPON_TYPE_STMT =
            "SELECT mc.MEM_COU_ID, mc.MEM_ID, mc.COU_ID, mc.USE_STATUS " +
                    "FROM MEM_COUPON mc JOIN COUPON c ON mc.COU_ID = c.COU_ID " +  // 使用 COUPON 表格的 COU_TYPE 欄位
                    "WHERE mc.MEM_ID=? AND c.COU_TYPE=? ORDER BY mc.MEM_COU_ID DESC";

    private static final String GET_EXPIRING_COUPONS_STMT =
            "SELECT mc.MEM_COU_ID, mc.MEM_ID, mc.COU_ID, mc.USE_STATUS " +
                    "FROM MEM_COUPON mc JOIN COUPON c ON mc.COU_ID = c.COU_ID " +  // 使用 COUPON 表格的 COU_END 欄位
                    "WHERE mc.MEM_ID=? AND c.COU_END <= ? AND mc.USE_STATUS=0 ORDER BY c.COU_END ASC";

    @Override
    public void insert(MemCouponVO memCouponVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(INSERT_STMT);

            pstmt.setInt(1, memCouponVO.getMemId());
            pstmt.setInt(2, memCouponVO.getCouId());
            pstmt.setInt(3, memCouponVO.getUseStatus());

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public void update(MemCouponVO memCouponVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(UPDATE_STMT);

            pstmt.setInt(1, memCouponVO.getMemId());
            pstmt.setInt(2, memCouponVO.getCouId());
            pstmt.setInt(3, memCouponVO.getUseStatus());
            pstmt.setInt(4, memCouponVO.getMemCouId());

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public void delete(Integer memCouId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(DELETE_STMT);
            pstmt.setInt(1, memCouId);
            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public MemCouponVO findByPrimaryKey(Integer memCouId) {
        MemCouponVO memCouponVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_ONE_STMT);
            pstmt.setInt(1, memCouId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                memCouponVO = new MemCouponVO();
                memCouponVO.setMemCouId(rs.getInt("MEM_COU_ID"));
                memCouponVO.setMemId(rs.getInt("MEM_ID"));
                memCouponVO.setCouId(rs.getInt("COU_ID"));
                memCouponVO.setUseStatus(rs.getInt("USE_STATUS"));
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return memCouponVO;
    }

    @Override
    public List<MemCouponVO> getAll() {
        List<MemCouponVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_ALL_STMT);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemCouponVO memCouponVO = new MemCouponVO();
                memCouponVO.setMemCouId(rs.getInt("MEM_COU_ID"));
                memCouponVO.setMemId(rs.getInt("MEM_ID"));
                memCouponVO.setCouId(rs.getInt("COU_ID"));
                memCouponVO.setUseStatus(rs.getInt("USE_STATUS"));
                list.add(memCouponVO);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public List<MemCouponVO> findByMemId(Integer memId) {
        List<MemCouponVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_BY_MEM_ID_STMT);
            pstmt.setInt(1, memId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemCouponVO memCouponVO = new MemCouponVO();
                memCouponVO.setMemCouId(rs.getInt("MEM_COU_ID"));
                memCouponVO.setMemId(rs.getInt("MEM_ID"));
                memCouponVO.setCouId(rs.getInt("COU_ID"));
                memCouponVO.setUseStatus(rs.getInt("USE_STATUS"));
                list.add(memCouponVO);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public boolean existsByMemIdAndCouId(Integer memId, Integer couId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(EXISTS_BY_MEM_ID_AND_COU_ID_STMT);
            pstmt.setInt(1, memId);
            pstmt.setInt(2, couId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return exists;
    }

    @Override
    public List<MemCouponVO> findUnusedByMemId(Integer memId) {
        List<MemCouponVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_UNUSED_BY_MEM_ID_STMT);
            pstmt.setInt(1, memId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemCouponVO memCouponVO = new MemCouponVO();
                memCouponVO.setMemCouId(rs.getInt("MEM_COU_ID"));
                memCouponVO.setMemId(rs.getInt("MEM_ID"));
                memCouponVO.setCouId(rs.getInt("COU_ID"));
                memCouponVO.setUseStatus(rs.getInt("USE_STATUS"));
                list.add(memCouponVO);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public List<MemCouponVO> findUsedByMemId(Integer memId) {
        List<MemCouponVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_USED_BY_MEM_ID_STMT);
            pstmt.setInt(1, memId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemCouponVO memCouponVO = new MemCouponVO();
                memCouponVO.setMemCouId(rs.getInt("MEM_COU_ID"));
                memCouponVO.setMemId(rs.getInt("MEM_ID"));
                memCouponVO.setCouId(rs.getInt("COU_ID"));
                memCouponVO.setUseStatus(rs.getInt("USE_STATUS"));
                list.add(memCouponVO);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public void updateUseStatus(Integer memCouId, Integer useStatus) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(UPDATE_USE_STATUS_STMT);
            pstmt.setInt(1, useStatus);
            pstmt.setInt(2, memCouId);
            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public Integer countMembersByCouId(Integer couId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer count = 0;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(COUNT_MEMBERS_BY_COU_ID_STMT);
            pstmt.setInt(1, couId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return count;
    }

    @Override
    public Map<String, Integer> getCouponUsageStats(Integer couId) {
        Map<String, Integer> stats = new HashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_COUPON_USAGE_STATS_STMT);
            pstmt.setInt(1, couId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int useStatus = rs.getInt("USE_STATUS");
                int count = rs.getInt("count");
                String status = (useStatus == 0) ? "unused" : "used";
                stats.put(status, count);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return stats;
    }

    @Override
    public Integer countTotalByMemId(Integer memId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer count = 0;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(COUNT_TOTAL_BY_MEM_ID_STMT);
            pstmt.setInt(1, memId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return count;
    }

    @Override
    public Integer countUnusedByMemId(Integer memId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer count = 0;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(COUNT_UNUSED_BY_MEM_ID_STMT);
            pstmt.setInt(1, memId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return count;
    }

    @Override
    public void batchInsert(List<MemCouponVO> memCoupons) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(INSERT_STMT);

            for (MemCouponVO memCoupon : memCoupons) {
                pstmt.setInt(1, memCoupon.getMemId());
                pstmt.setInt(2, memCoupon.getCouId());
                pstmt.setInt(3, memCoupon.getUseStatus());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            con.commit();
        } catch (SQLException se) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException autoCommitEx) {
                autoCommitEx.printStackTrace();
            }
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public void batchUpdateUseStatus(List<Integer> memCouIds, Integer useStatus) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(UPDATE_USE_STATUS_STMT);

            for (Integer memCouId : memCouIds) {
                pstmt.setInt(1, useStatus);
                pstmt.setInt(2, memCouId);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            con.commit();
        } catch (SQLException se) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException autoCommitEx) {
                autoCommitEx.printStackTrace();
            }
            closeResources(null, pstmt, con);
        }
    }

    // *** 以下兩個方法需要使用到 COUPON 表格 ***
    @Override
    public List<MemCouponVO> findByMemIdAndCouponType(Integer memId, String couponType) {
        // 此方法需要 JOIN COUPON 表格，使用 COUPON.COU_TYPE 欄位
        List<MemCouponVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_BY_MEM_ID_AND_COUPON_TYPE_STMT);
            pstmt.setInt(1, memId);
            pstmt.setString(2, couponType);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemCouponVO memCouponVO = new MemCouponVO();
                memCouponVO.setMemCouId(rs.getInt("MEM_COU_ID"));
                memCouponVO.setMemId(rs.getInt("MEM_ID"));
                memCouponVO.setCouId(rs.getInt("COU_ID"));
                memCouponVO.setUseStatus(rs.getInt("USE_STATUS"));
                list.add(memCouponVO);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public List<MemCouponVO> findExpiringCoupons(Integer memId, Date expiryDate) {
        // 此方法需要 JOIN COUPON 表格，使用 COUPON.COU_END 欄位
        List<MemCouponVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_EXPIRING_COUPONS_STMT);
            pstmt.setInt(1, memId);
            pstmt.setTimestamp(2, new Timestamp(expiryDate.getTime()));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                MemCouponVO memCouponVO = new MemCouponVO();
                memCouponVO.setMemCouId(rs.getInt("MEM_COU_ID"));
                memCouponVO.setMemId(rs.getInt("MEM_ID"));
                memCouponVO.setCouId(rs.getInt("COU_ID"));
                memCouponVO.setUseStatus(rs.getInt("USE_STATUS"));
                list.add(memCouponVO);
            }
        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    // 資源關閉的輔助方法
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection con) {
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
}
