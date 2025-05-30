package com.foodietime.orders.model;

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
import java.util.List;

public class OrdersDAO implements OrdersDAO_interface {

    // ========== 資料來源設定 ==========
    // 一個應用程式中，針對一個資料庫，共用一個DataSource即可
    private static DataSource ds = null;
    static {
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB3");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // ========== SQL 語句常數定義 ==========

    // 基本 CRUD 操作
    private static final String INSERT_STMT =
            "INSERT INTO ORDERS (MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_STMT =
            "UPDATE ORDERS SET MEM_ID=?, STOR_ID=?, ORD_DATE=?, ORD_SUM=?, PAYMENT_STATUS=?, " +
                    "PAY_METHOD=?, DELIVER=?, ORDER_STATUS=?, ACT_ID=?, COU_ID=?, CANCEL_REASON=?, " +
                    "COMMENT=?, RATING=?, PROMO_DISCOUNT=?, COUPON_DISCOUNT=?, ACTUAL_PAYMENT=?, ORD_ADDR=? " +
                    "WHERE ORD_ID=?";

    private static final String DELETE_STMT =
            "DELETE FROM ORDERS WHERE ORD_ID=?";

    // 查詢操作
    private static final String GET_ONE_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE ORD_ID=?";

    private static final String GET_ALL_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS ORDER BY ORD_ID DESC";

    private static final String GET_BY_MEM_ID_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE MEM_ID=? ORDER BY ORD_DATE DESC";

    private static final String EXISTS_BY_MEM_AND_ORDER_STMT =
            "SELECT COUNT(*) FROM ORDERS WHERE MEM_ID=? AND ORD_ID=?";

    // 業務邏輯查詢
    private static final String GET_BY_ORDER_STATUS_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE ORDER_STATUS=? ORDER BY ORD_DATE DESC";

    private static final String GET_BY_PAYMENT_STATUS_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE PAYMENT_STATUS=? ORDER BY ORD_DATE DESC";

    private static final String GET_BY_STORE_ID_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE STOR_ID=? ORDER BY ORD_DATE DESC";

    private static final String GET_BY_DATE_RANGE_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE ORD_DATE BETWEEN ? AND ? ORDER BY ORD_DATE DESC";

    private static final String GET_BY_COUPON_ID_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE COU_ID=? ORDER BY ORD_DATE DESC";

    private static final String GET_BY_ACTIVITY_ID_STMT =
            "SELECT ORD_ID, MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, " +
                    "PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID, COU_ID, CANCEL_REASON, " +
                    "COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR " +
                    "FROM ORDERS WHERE ACT_ID=? ORDER BY ORD_DATE DESC";

    // 狀態更新
    private static final String UPDATE_ORDER_STATUS_STMT =
            "UPDATE ORDERS SET ORDER_STATUS=? WHERE ORD_ID=?";

    private static final String UPDATE_PAYMENT_STATUS_STMT =
            "UPDATE ORDERS SET PAYMENT_STATUS=? WHERE ORD_ID=?";

    // 統計查詢
    private static final String GET_TOTAL_SPENT_BY_MEMBER_STMT =
            "SELECT COALESCE(SUM(ACTUAL_PAYMENT), 0) FROM ORDERS " +
                    "WHERE MEM_ID=? AND PAYMENT_STATUS=1 AND ORDER_STATUS=2";

    // *** 以下 SQL 需要使用到 ORDER_DETAIL 表格 ***
    private static final String GET_POPULAR_ITEMS_STATS_STMT =
            "SELECT od.ITEM_ID, SUM(od.QUANTITY) as total_quantity, COUNT(DISTINCT o.ORD_ID) as order_count " +
                    "FROM ORDERS o JOIN ORDER_DETAIL od ON o.ORD_ID = od.ORD_ID " +  // 使用 ORDER_DETAIL 表格
                    "WHERE o.ORDER_STATUS=2 " +
                    "GROUP BY od.ITEM_ID " +
                    "ORDER BY total_quantity DESC " +
                    "LIMIT ?";

    // ========== 基本 CRUD 操作實作 ==========

    @Override
    public void insert(OrdersVO ordersVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(INSERT_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：設定新增訂單的所有欄位資料
            pstmt.setInt(1, ordersVO.getMemId());           // 會員編號
            pstmt.setInt(2, ordersVO.getStorId());          // 店家編號
            pstmt.setTimestamp(3, ordersVO.getOrdDate());   // 訂單成立時間
            pstmt.setInt(4, ordersVO.getOrdSum());          // 訂單金額
            pstmt.setInt(5, ordersVO.getPaymentStatus());   // 付款狀態
            pstmt.setInt(6, ordersVO.getPayMethod());       // 付款方式
            pstmt.setInt(7, ordersVO.getDeliver());         // 取餐方式
            pstmt.setInt(8, ordersVO.getOrderStatus());     // 訂單狀態
            pstmt.setObject(9, ordersVO.getActId());        // 活動編號（可為null）
            pstmt.setObject(10, ordersVO.getCouId());       // 優惠券編號（可為null）
            pstmt.setString(11, ordersVO.getCancelReason()); // 取消原因
            pstmt.setString(12, ordersVO.getComment());     // 評價
            pstmt.setObject(13, ordersVO.getRating());      // 星等（可為null）
            pstmt.setObject(14, ordersVO.getPromoDiscount()); // 活動優惠金額（可為null）
            pstmt.setObject(15, ordersVO.getCouponDiscount()); // 優惠券優惠金額（可為null）
            pstmt.setObject(16, ordersVO.getActualPayment()); // 實付金額（可為null）
            pstmt.setString(17, ordersVO.getOrdAddr());     // 外送地址

            // ========== 步驟3：執行SQL語句 ==========
            pstmt.executeUpdate();

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟4：關閉資源 ==========
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public void update(OrdersVO ordersVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(UPDATE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：更新訂單的所有欄位資料
            pstmt.setInt(1, ordersVO.getMemId());
            pstmt.setInt(2, ordersVO.getStorId());
            pstmt.setTimestamp(3, ordersVO.getOrdDate());
            pstmt.setInt(4, ordersVO.getOrdSum());
            pstmt.setInt(5, ordersVO.getPaymentStatus());
            pstmt.setInt(6, ordersVO.getPayMethod());
            pstmt.setInt(7, ordersVO.getDeliver());
            pstmt.setInt(8, ordersVO.getOrderStatus());
            pstmt.setObject(9, ordersVO.getActId());
            pstmt.setObject(10, ordersVO.getCouId());
            pstmt.setString(11, ordersVO.getCancelReason());
            pstmt.setString(12, ordersVO.getComment());
            pstmt.setObject(13, ordersVO.getRating());
            pstmt.setObject(14, ordersVO.getPromoDiscount());
            pstmt.setObject(15, ordersVO.getCouponDiscount());
            pstmt.setObject(16, ordersVO.getActualPayment());
            pstmt.setString(17, ordersVO.getOrdAddr());
            pstmt.setInt(18, ordersVO.getOrdId());          // WHERE條件：訂單編號

            // ========== 步驟3：執行SQL語句 ==========
            pstmt.executeUpdate();

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟4：關閉資源 ==========
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public void delete(Integer ordId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(DELETE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：指定要刪除的訂單編號
            pstmt.setInt(1, ordId);

            // ========== 步驟3：執行SQL語句 ==========
            pstmt.executeUpdate();

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟4：關閉資源 ==========
            closeResources(null, pstmt, con);
        }
    }

    // ========== 查詢操作實作 ==========

    @Override
    public OrdersVO findByPrimaryKey(Integer ordId) {
        OrdersVO ordersVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_ONE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：指定要查詢的訂單編號
            pstmt.setInt(1, ordId);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                ordersVO = createOrdersVOFromResultSet(rs);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟5：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return ordersVO;
    }

    @Override
    public List<OrdersVO> getAll() {
        List<OrdersVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_ALL_STMT);

            // ========== 步驟2：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟3：處理查詢結果 ==========
            while (rs.next()) {
                OrdersVO ordersVO = createOrdersVOFromResultSet(rs);
                list.add(ordersVO);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟4：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public List<OrdersVO> findByMemberId(Integer memId) {
        List<OrdersVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_BY_MEM_ID_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：指定要查詢的會員編號
            pstmt.setInt(1, memId);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                OrdersVO ordersVO = createOrdersVOFromResultSet(rs);
                list.add(ordersVO);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟5：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public Boolean existsByMemberAndOrder(Integer memId, Integer ordId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Boolean exists = false;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(EXISTS_BY_MEM_AND_ORDER_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：檢查指定會員是否擁有指定訂單
            pstmt.setInt(1, memId);
            pstmt.setInt(2, ordId);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟5：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return exists;
    }

    // ========== 業務邏輯查詢實作 ==========

    @Override
    public List<OrdersVO> findByOrderStatus(Integer orderStatus) {
        return executeQueryWithSingleParam(GET_BY_ORDER_STATUS_STMT, orderStatus);
    }

    @Override
    public List<OrdersVO> findByPaymentStatus(Integer paymentStatus) {
        return executeQueryWithSingleParam(GET_BY_PAYMENT_STATUS_STMT, paymentStatus);
    }

    @Override
    public List<OrdersVO> findByStoreId(Integer storId) {
        return executeQueryWithSingleParam(GET_BY_STORE_ID_STMT, storId);
    }

    @Override
    public List<OrdersVO> findByDateRange(Timestamp startDate, Timestamp endDate) {
        List<OrdersVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_BY_DATE_RANGE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：設定日期範圍的開始和結束時間
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                OrdersVO ordersVO = createOrdersVOFromResultSet(rs);
                list.add(ordersVO);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟5：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    @Override
    public List<OrdersVO> findByCouponId(Integer couId) {
        return executeQueryWithSingleParam(GET_BY_COUPON_ID_STMT, couId);
    }

    @Override
    public List<OrdersVO> findByActivityId(Integer actId) {
        return executeQueryWithSingleParam(GET_BY_ACTIVITY_ID_STMT, actId);
    }

    // ========== 狀態更新實作 ==========

    @Override
    public Integer updateOrderStatus(Integer ordId, Integer orderStatus) {
        return executeUpdateWithTwoParams(UPDATE_ORDER_STATUS_STMT, orderStatus, ordId);
    }

    @Override
    public Integer updatePaymentStatus(Integer ordId, Integer paymentStatus) {
        return executeUpdateWithTwoParams(UPDATE_PAYMENT_STATUS_STMT, paymentStatus, ordId);
    }

    // ========== 統計查詢實作 ==========

    @Override
    public Integer getTotalSpentByMember(Integer memId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer totalSpent = 0;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_TOTAL_SPENT_BY_MEMBER_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：計算指定會員的總消費金額（僅計算已付款且已完成的訂單）
            pstmt.setInt(1, memId);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            if (rs.next()) {
                totalSpent = rs.getInt(1);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟5：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return totalSpent;
    }

    @Override
    public List<Object[]> getPopularItemsStatistics(Integer limit) {
        // *** 此方法需要 JOIN ORDER_DETAIL 表格 ***
        List<Object[]> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_POPULAR_ITEMS_STATS_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：限制回傳的熱門商品數量
            pstmt.setInt(1, limit);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                Object[] stats = new Object[3];
                stats[0] = rs.getInt("ITEM_ID");           // 商品編號
                stats[1] = rs.getInt("total_quantity");    // 總銷售數量
                stats[2] = rs.getInt("order_count");       // 訂單數量
                list.add(stats);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟5：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    // ========== 輔助方法 ==========

    /**
     * 從 ResultSet 建立 OrdersVO 物件
     * 參數用途：將資料庫查詢結果轉換為 Java 物件
     */
    private OrdersVO createOrdersVOFromResultSet(ResultSet rs) throws SQLException {
        OrdersVO ordersVO = new OrdersVO();
        ordersVO.setOrdId(rs.getInt("ORD_ID"));
        ordersVO.setMemId(rs.getInt("MEM_ID"));
        ordersVO.setStorId(rs.getInt("STOR_ID"));
        ordersVO.setOrdDate(rs.getTimestamp("ORD_DATE"));
        ordersVO.setOrdSum(rs.getInt("ORD_SUM"));
        ordersVO.setPaymentStatus(rs.getInt("PAYMENT_STATUS"));
        ordersVO.setPayMethod(rs.getInt("PAY_METHOD"));
        ordersVO.setDeliver(rs.getInt("DELIVER"));
        ordersVO.setOrderStatus(rs.getInt("ORDER_STATUS"));

        // 處理可能為 null 的欄位
        Integer actId = rs.getObject("ACT_ID", Integer.class);
        ordersVO.setActId(actId);

        Integer couId = rs.getObject("COU_ID", Integer.class);
        ordersVO.setCouId(couId);

        ordersVO.setCancelReason(rs.getString("CANCEL_REASON"));
        ordersVO.setComment(rs.getString("COMMENT"));

        Integer rating = rs.getObject("RATING", Integer.class);
        ordersVO.setRating(rating);

        Integer promoDiscount = rs.getObject("PROMO_DISCOUNT", Integer.class);
        ordersVO.setPromoDiscount(promoDiscount);

        Integer couponDiscount = rs.getObject("COUPON_DISCOUNT", Integer.class);
        ordersVO.setCouponDiscount(couponDiscount);

        Integer actualPayment = rs.getObject("ACTUAL_PAYMENT", Integer.class);
        ordersVO.setActualPayment(actualPayment);

        ordersVO.setOrdAddr(rs.getString("ORD_ADDR"));

        return ordersVO;
    }

    /**
     * 執行單一參數的查詢
     * 參數用途：簡化重複的查詢邏輯
     */
    private List<OrdersVO> executeQueryWithSingleParam(String sql, Integer param) {
        List<OrdersVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, param);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrdersVO ordersVO = createOrdersVOFromResultSet(rs);
                list.add(ordersVO);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    /**
     * 執行雙參數的更新操作
     * 參數用途：簡化重複的更新邏輯
     */
    private Integer executeUpdateWithTwoParams(String sql, Integer param1, Integer param2) {
        Connection con = null;
        PreparedStatement pstmt = null;
        Integer updateCount = 0;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, param1);
            pstmt.setInt(2, param2);
            updateCount = pstmt.executeUpdate();

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(null, pstmt, con);
        }
        return updateCount;
    }

    /**
     * 資源關閉的輔助方法
     * 參數用途：確保資料庫連線資源正確釋放
     */
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

