package com.foodietime.orddet.model;

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

public class OrdDetDAO implements OrdDetDAO_interface {

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
            "INSERT INTO ORDER_DETAILS (ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_STMT =
            "UPDATE ORDER_DETAILS SET ORD_ID=?, PROD_ID=?, PROD_QTY=?, PROD_PRICE=?, ORD_DESC=? " +
                    "WHERE ORD_DET_ID=?";

    private static final String DELETE_STMT =
            "DELETE FROM ORDER_DETAILS WHERE ORD_DET_ID=?";

    // 查詢操作
    private static final String GET_ONE_STMT =
            "SELECT ORD_DET_ID, ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC " +
                    "FROM ORDER_DETAILS WHERE ORD_DET_ID=?";

    private static final String GET_ALL_STMT =
            "SELECT ORD_DET_ID, ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC " +
                    "FROM ORDER_DETAILS ORDER BY ORD_DET_ID DESC";

    // *** 以下 SQL 需要使用到 ORDERS 表格 ***
    private static final String GET_BY_MEM_ID_STMT =
            "SELECT od.ORD_DET_ID, od.ORD_ID, od.PROD_ID, od.PROD_QTY, od.PROD_PRICE, od.ORD_DESC " +
                    "FROM ORDER_DETAILS od JOIN ORDERS o ON od.ORD_ID = o.ORD_ID " +  // 使用 ORDERS 表格
                    "WHERE o.MEM_ID=? ORDER BY od.ORD_DET_ID DESC";

    // *** 以下 SQL 需要使用到 ORDERS 表格 ***
    private static final String EXISTS_BY_MEM_AND_ORDER_DETAIL_STMT =
            "SELECT COUNT(*) FROM ORDER_DETAILS od JOIN ORDERS o ON od.ORD_ID = o.ORD_ID " +  // 使用 ORDERS 表格
                    "WHERE o.MEM_ID=? AND od.ORD_DET_ID=?";

    // 業務邏輯查詢
    private static final String GET_BY_ORDER_ID_STMT =
            "SELECT ORD_DET_ID, ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC " +
                    "FROM ORDER_DETAILS WHERE ORD_ID=? ORDER BY ORD_DET_ID";

    private static final String GET_BY_PRODUCT_ID_STMT =
            "SELECT ORD_DET_ID, ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC " +
                    "FROM ORDER_DETAILS WHERE PROD_ID=? ORDER BY ORD_DET_ID DESC";

    private static final String GET_TOTAL_QUANTITY_BY_ORDER_STMT =
            "SELECT COALESCE(SUM(PROD_QTY), 0) FROM ORDER_DETAILS WHERE ORD_ID=?";

    private static final String GET_TOTAL_AMOUNT_BY_ORDER_STMT =
            "SELECT COALESCE(SUM(PROD_QTY * PROD_PRICE), 0) FROM ORDER_DETAILS WHERE ORD_ID=?";

    private static final String GET_TOTAL_SOLD_BY_PRODUCT_STMT =
            "SELECT COALESCE(SUM(PROD_QTY), 0) FROM ORDER_DETAILS " +
                    "WHERE PROD_ID=?";

    private static final String GET_POPULAR_PRODUCTS_RANKING_STMT =
            "SELECT PROD_ID, SUM(PROD_QTY) as total_quantity, COUNT(DISTINCT ORD_ID) as order_count " +
                    "FROM ORDER_DETAILS " +
                    "GROUP BY PROD_ID " +
                    "ORDER BY total_quantity DESC " +
                    "LIMIT ?";

    // *** 以下 SQL 需要使用到 ORDERS 表格 ***
    private static final String GET_PRODUCT_SALES_BY_STORE_STMT =
            "SELECT od.PROD_ID, SUM(od.PROD_QTY) as total_quantity, " +
                    "SUM(od.PROD_QTY * od.PROD_PRICE) as total_amount " +
                    "FROM ORDER_DETAILS od JOIN ORDERS o ON od.ORD_ID = o.ORD_ID " +  // 使用 ORDERS 表格
                    "WHERE o.STOR_ID=? " +
                    "GROUP BY od.PROD_ID " +
                    "ORDER BY total_quantity DESC";

    private static final String GET_BY_PRICE_RANGE_STMT =
            "SELECT ORD_DET_ID, ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC " +
                    "FROM ORDER_DETAILS WHERE PROD_PRICE BETWEEN ? AND ? ORDER BY PROD_PRICE";

    private static final String DELETE_BY_ORDER_ID_STMT =
            "DELETE FROM ORDER_DETAILS WHERE ORD_ID=?";

    private static final String UPDATE_QUANTITY_STMT =
            "UPDATE ORDER_DETAILS SET PROD_QTY=? WHERE ORD_DET_ID=?";

    private static final String UPDATE_PRICE_STMT =
            "UPDATE ORDER_DETAILS SET PROD_PRICE=? WHERE ORD_DET_ID=?";

    // *** 以下 SQL 需要使用到 ORDERS 表格 ***
    private static final String GET_BY_DATE_RANGE_STMT =
            "SELECT od.ORD_DET_ID, od.ORD_ID, od.PROD_ID, od.PROD_QTY, od.PROD_PRICE, od.ORD_DESC " +
                    "FROM ORDER_DETAILS od JOIN ORDERS o ON od.ORD_ID = o.ORD_ID " +  // 使用 ORDERS 表格
                    "WHERE o.ORD_DATE BETWEEN ? AND ? ORDER BY o.ORD_DATE DESC";

    private static final String GET_BY_DESCRIPTION_KEYWORD_STMT =
            "SELECT ORD_DET_ID, ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC " +
                    "FROM ORDER_DETAILS WHERE ORD_DESC LIKE ? ORDER BY ORD_DET_ID DESC";

    // ========== 基本 CRUD 操作實作 ==========

    @Override
    public void insert(OrdDetVO ordDetVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(INSERT_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：設定新增訂單明細的所有欄位資料
            pstmt.setInt(1, ordDetVO.getOrdId());           // 訂單編號
            pstmt.setInt(2, ordDetVO.getProdId());          // 商品編號
            pstmt.setInt(3, ordDetVO.getProdQty());         // 商品數量
            pstmt.setInt(4, ordDetVO.getProdPrice());       // 商品單價
            pstmt.setString(5, ordDetVO.getOrdDesc());      // 訂單備註

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
    public void update(OrdDetVO ordDetVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(UPDATE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：更新訂單明細的所有欄位資料
            pstmt.setInt(1, ordDetVO.getOrdId());
            pstmt.setInt(2, ordDetVO.getProdId());
            pstmt.setInt(3, ordDetVO.getProdQty());
            pstmt.setInt(4, ordDetVO.getProdPrice());
            pstmt.setString(5, ordDetVO.getOrdDesc());
            pstmt.setInt(6, ordDetVO.getOrdDetId());        // WHERE條件：訂單明細編號

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
    public void delete(Integer ordDetId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(DELETE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：指定要刪除的訂單明細編號
            pstmt.setInt(1, ordDetId);

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
    public OrdDetVO findByPrimaryKey(Integer ordDetId) {
        OrdDetVO ordDetVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_ONE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：指定要查詢的訂單明細編號
            pstmt.setInt(1, ordDetId);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                ordDetVO = createOrdDetVOFromResultSet(rs);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟5：關閉資源 ==========
            closeResources(rs, pstmt, con);
        }
        return ordDetVO;
    }

    @Override
    public List<OrdDetVO> getAll() {
        List<OrdDetVO> list = new ArrayList<>();
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
                OrdDetVO ordDetVO = createOrdDetVOFromResultSet(rs);
                list.add(ordDetVO);
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
    public List<OrdDetVO> findByMemberId(Integer memId) {
        // *** 此方法需要 JOIN ORDERS 表格 ***
        List<OrdDetVO> list = new ArrayList<>();
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
                OrdDetVO ordDetVO = createOrdDetVOFromResultSet(rs);
                list.add(ordDetVO);
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
    public Boolean existsByMemberAndOrderDetail(Integer memId, Integer ordDetId) {
        // *** 此方法需要 JOIN ORDERS 表格 ***
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Boolean exists = false;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(EXISTS_BY_MEM_AND_ORDER_DETAIL_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：檢查指定會員是否擁有指定訂單明細
            pstmt.setInt(1, memId);
            pstmt.setInt(2, ordDetId);

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
    public List<OrdDetVO> findByOrderId(Integer ordId) {
        return executeQueryWithSingleParam(GET_BY_ORDER_ID_STMT, ordId);
    }

    @Override
    public List<OrdDetVO> findByProductId(Integer prodId) {
        return executeQueryWithSingleParam(GET_BY_PRODUCT_ID_STMT, prodId);
    }

    @Override
    public Integer getTotalQuantityByOrderId(Integer ordId) {
        return executeStatisticQuery(GET_TOTAL_QUANTITY_BY_ORDER_STMT, ordId);
    }

    @Override
    public Integer getTotalAmountByOrderId(Integer ordId) {
        return executeStatisticQuery(GET_TOTAL_AMOUNT_BY_ORDER_STMT, ordId);
    }

    @Override
    public Integer getTotalSoldQuantityByProductId(Integer prodId) {
        return executeStatisticQuery(GET_TOTAL_SOLD_BY_PRODUCT_STMT, prodId);
    }

    @Override
    public List<Object[]> getPopularProductsRanking(Integer limit) {
        List<Object[]> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_POPULAR_PRODUCTS_RANKING_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：限制回傳的熱門商品數量
            pstmt.setInt(1, limit);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                Object[] stats = new Object[3];
                stats[0] = rs.getInt("PROD_ID");           // 商品編號
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

    @Override
    public List<Object[]> getProductSalesByStore(Integer storId) {
        // *** 此方法需要 JOIN ORDERS 表格 ***
        List<Object[]> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_PRODUCT_SALES_BY_STORE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：指定要查詢的店家編號
            pstmt.setInt(1, storId);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                Object[] stats = new Object[3];
                stats[0] = rs.getInt("PROD_ID");           // 商品編號
                stats[1] = rs.getInt("total_quantity");    // 總銷售數量
                stats[2] = rs.getInt("total_amount");      // 總銷售金額
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

    @Override
    public List<OrdDetVO> findByPriceRange(Integer minPrice, Integer maxPrice) {
        List<OrdDetVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_BY_PRICE_RANGE_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：設定價格範圍的最低價和最高價
            pstmt.setInt(1, minPrice);
            pstmt.setInt(2, maxPrice);

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                OrdDetVO ordDetVO = createOrdDetVOFromResultSet(rs);
                list.add(ordDetVO);
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
    public void batchInsert(List<OrdDetVO> ordDetList) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            con.setAutoCommit(false);  // 開啟交易模式
            pstmt = con.prepareStatement(INSERT_STMT);

            // ========== 步驟2：批次設定參數值 ==========
            // 參數用途：批次新增多筆訂單明細資料
            for (OrdDetVO ordDetVO : ordDetList) {
                pstmt.setInt(1, ordDetVO.getOrdId());
                pstmt.setInt(2, ordDetVO.getProdId());
                pstmt.setInt(3, ordDetVO.getProdQty());
                pstmt.setInt(4, ordDetVO.getProdPrice());
                pstmt.setString(5, ordDetVO.getOrdDesc());
                pstmt.addBatch();
            }

            // ========== 步驟3：執行批次操作 ==========
            pstmt.executeBatch();
            con.commit();  // 提交交易

        } catch (SQLException se) {
            try {
                if (con != null) {
                    con.rollback();  // 發生錯誤時回滾交易
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            // ========== 步驟4：關閉資源 ==========
            try {
                if (con != null) {
                    con.setAutoCommit(true);  // 恢復自動提交模式
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            closeResources(null, pstmt, con);
        }
    }

    @Override
    public void deleteByOrderId(Integer ordId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(DELETE_BY_ORDER_ID_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：刪除指定訂單的所有明細
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

    @Override
    public Integer updateQuantity(Integer ordDetId, Integer quantity) {
        return executeUpdateWithTwoParams(UPDATE_QUANTITY_STMT, quantity, ordDetId);
    }

    @Override
    public Integer updatePrice(Integer ordDetId, Integer price) {
        return executeUpdateWithTwoParams(UPDATE_PRICE_STMT, price, ordDetId);
    }

    @Override
    public List<OrdDetVO> findByDateRange(Timestamp startDate, Timestamp endDate) {
        // *** 此方法需要 JOIN ORDERS 表格 ***
        List<OrdDetVO> list = new ArrayList<>();
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
                OrdDetVO ordDetVO = createOrdDetVOFromResultSet(rs);
                list.add(ordDetVO);
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
    public List<OrdDetVO> findByDescriptionKeyword(String keyword) {
        List<OrdDetVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ========== 步驟1：建立資料庫連線 ==========
            con = ds.getConnection();
            pstmt = con.prepareStatement(GET_BY_DESCRIPTION_KEYWORD_STMT);

            // ========== 步驟2：設定參數值 ==========
            // 參數用途：設定搜尋關鍵字（使用模糊查詢）
            pstmt.setString(1, "%" + keyword + "%");

            // ========== 步驟3：執行查詢 ==========
            rs = pstmt.executeQuery();

            // ========== 步驟4：處理查詢結果 ==========
            while (rs.next()) {
                OrdDetVO ordDetVO = createOrdDetVOFromResultSet(rs);
                list.add(ordDetVO);
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
     * 從 ResultSet 建立 OrdDetVO 物件
     * 參數用途：將資料庫查詢結果轉換為 Java 物件
     */
    private OrdDetVO createOrdDetVOFromResultSet(ResultSet rs) throws SQLException {
        OrdDetVO ordDetVO = new OrdDetVO();
        ordDetVO.setOrdDetId(rs.getInt("ORD_DET_ID"));
        ordDetVO.setOrdId(rs.getInt("ORD_ID"));
        ordDetVO.setProdId(rs.getInt("PROD_ID"));
        ordDetVO.setProdQty(rs.getInt("PROD_QTY"));
        ordDetVO.setProdPrice(rs.getInt("PROD_PRICE"));
        ordDetVO.setOrdDesc(rs.getString("ORD_DESC"));
        return ordDetVO;
    }

    /**
     * 執行單一參數的查詢
     * 參數用途：簡化重複的查詢邏輯
     */
    private List<OrdDetVO> executeQueryWithSingleParam(String sql, Integer param) {
        List<OrdDetVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, param);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrdDetVO ordDetVO = createOrdDetVOFromResultSet(rs);
                list.add(ordDetVO);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return list;
    }

    /**
     * 執行統計查詢
     * 參數用途：簡化重複的統計查詢邏輯
     */
    private Integer executeStatisticQuery(String sql, Integer param) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer result = 0;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, param);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getInt(1);
            }

        } catch (SQLException se) {
            throw new RuntimeException("A database error occurred. " + se.getMessage());
        } finally {
            closeResources(rs, pstmt, con);
        }
        return result;
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
