package com.foodietime.cart.model;

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

public class CartDAO implements CartDAO_interface {

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

    private static final String INSERT_STMT = 
        "INSERT INTO SHOPPING_CART (MEM_ID, PROD_ID, PROD_N) VALUES (?, ? ,?)";
    private static final String GET_ALL_STMT = 
        "SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART ORDER BY SHOP_ID";
    private static final String GET_ONE_STMT = 
        "SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART WHERE SHOP_ID = ?";
    private static final String DELETE = 
        "DELETE FROM SHOPPING_CART WHERE SHOP_ID = ?";
    private static final String UPDATE = 
        "UPDATE SHOPPING_CART SET MEM_ID=?, PROD_ID=?, PROD_N=? WHERE SHOP_ID = ?";
    private static final String GET_BY_MEMID = 
        "SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART WHERE MEM_ID = ? ORDER BY SHOP_ID";
    private static final String GET_BY_MEMID_PRODID = 
        "SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART WHERE MEM_ID = ? AND PROD_ID = ?";

    @Override
    public void insert(CartVO cartVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(INSERT_STMT);
            //"INSERT INTO SHOPPING_CART (MEM_ID, PROD_ID, PROD_N) VALUES (?, ? ,?)"

            pstmt.setInt(1, cartVO.getMemId());
            pstmt.setInt(2, cartVO.getProdId());
            pstmt.setInt(3, cartVO.getProdN());

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. "
                    + se.getMessage());
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
    public void update(CartVO cartVO) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            //"UPDATE SHOPPING_CART SET MEM_ID=?, PROD_ID=?, PROD_N=? WHERE SHOP_ID = ?"
            pstmt = con.prepareStatement(UPDATE);

            pstmt.setInt(1, cartVO.getMemId());
            pstmt.setInt(2, cartVO.getProdId());
            pstmt.setInt(3, cartVO.getProdN());

            pstmt.setInt(4, cartVO.getShopId());

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. "
                    + se.getMessage());
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
    public void delete(Integer shopId) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ds.getConnection();
            //"DELETE FROM SHOPPING_CART WHERE SHOP_ID = ?"
            pstmt = con.prepareStatement(DELETE);

            pstmt.setInt(1, shopId);

            pstmt.executeUpdate();
        } catch (SQLException se) {
            throw new RuntimeException("A database error occured. "
                    + se.getMessage());
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
    // 依據主鍵查詢單一購物車商品
    public CartVO findByPrimaryKey(Integer shopId) {
        CartVO cartVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            //"SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART WHERE SHOP_ID = ?"
            pstmt = con.prepareStatement(GET_ONE_STMT);

            pstmt.setInt(1, shopId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                cartVO = new CartVO();
                cartVO.setShopId(rs.getInt("SHOP_ID"));
                cartVO.setMemId(rs.getInt("MEM_ID"));
                cartVO.setProdId(rs.getInt("PROD_ID"));
                cartVO.setProdN(rs.getInt("PROD_N"));
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
        return cartVO;
    }

    @Override
    // 查詢所有購物車商品
    public List<CartVO> getAll() {
        List<CartVO> list = new ArrayList<CartVO>();
        CartVO cartVO = null;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            //"SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART ORDER BY SHOP_ID"
            pstmt = con.prepareStatement(GET_ALL_STMT);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cartVO = new CartVO();
                cartVO.setShopId(rs.getInt("SHOP_ID"));
                cartVO.setMemId(rs.getInt("MEM_ID"));
                cartVO.setProdId(rs.getInt("PROD_ID"));
                cartVO.setProdN(rs.getInt("PROD_N"));
                list.add(cartVO);
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
    // 查詢某會員的所有購物車商品
    public List<CartVO> findByMemId(Integer memId) {
        List<CartVO> list = new ArrayList<CartVO>();
        CartVO cartVO = null;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            //"SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART WHERE MEM_ID = ? ORDER BY SHOP_ID"
            pstmt = con.prepareStatement(GET_BY_MEMID);
            pstmt.setInt(1, memId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cartVO = new CartVO();
                cartVO.setShopId(rs.getInt("SHOP_ID"));
                cartVO.setMemId(rs.getInt("MEM_ID"));
                cartVO.setProdId(rs.getInt("PROD_ID"));
                cartVO.setProdN(rs.getInt("PROD_N"));
                list.add(cartVO);
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
    // 查詢某會員某商品是否已存在於購物車
    public CartVO findByMemIdAndProdId(Integer memId, Integer prodId) {
        CartVO cartVO = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = ds.getConnection();
            //SELECT SHOP_ID, MEM_ID, PROD_ID , PROD_N FROM SHOPPING_CART WHERE MEM_ID = ? AND PROD_ID = ?"
            pstmt = con.prepareStatement(GET_BY_MEMID_PRODID);
            pstmt.setInt(1, memId);
            pstmt.setInt(2, prodId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                cartVO = new CartVO();
                cartVO.setShopId(rs.getInt("SHOP_ID"));
                cartVO.setMemId(rs.getInt("MEM_ID"));
                cartVO.setProdId(rs.getInt("PROD_ID"));
                cartVO.setProdN(rs.getInt("PROD_N"));
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
        return cartVO;
    }
}
