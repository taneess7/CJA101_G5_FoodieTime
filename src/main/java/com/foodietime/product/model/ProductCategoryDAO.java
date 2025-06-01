package com.foodietime.product.model;

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

public class ProductCategoryDAO implements ProductCategoryDAO_interface{
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB1");
		} catch (NamingException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static final String INSERT_STMT = "INSERT INTO PRODUCT_CATEGORY (PROD_CATE_ID,PROD_CATE) VALUES (?,?)";
	private static final String GET_ALL_STMT = "SELECT PROD_CATE_ID,PROD_CATE FROM PRODUCT_CATEGORY ORDER BY PROD_CATE_ID";
	private static final String GET_ONE_STMT = "SELECT PROD_CATE_ID,PROD_CATE FROM PRODUCT_CATEGORY WHERE PROD_CATE_ID = ?";
	private static final String DELETE = "DELETE FROM PRODUCT_CATEGORY WHERE PROD_CATE_ID = ?";
	private static final String UPDATE = "UPDATE PRODUCT_CATEGORY SET PROD_CATE = ? WHERE PROD_CATE_ID = ?";

	
	@Override
	public void insert(ProductCategoryVO productCategoryVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setInt(1, productCategoryVO.getProdCateId());
			pstmt.setString(2, productCategoryVO.getProdCate());

			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured." + se.getMessage());
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
	public void update(ProductCategoryVO productCategoryVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setString(1, productCategoryVO.getProdCate());       // PROD_CATE
			pstmt.setInt(2, productCategoryVO.getProdCateId());         // PROD_CATE_ID

			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured." + se.getMessage());
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
	public void delete(Integer prod_Cate_Id) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, prod_Cate_Id);

			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured." + se.getMessage());
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
	public ProductCategoryVO findByPrimaryKey(Integer prodCateId) {
		ProductCategoryVO productCategoryVO = null;

		try (Connection con = ds.getConnection();
			 PreparedStatement pstmt = con.prepareStatement(GET_ONE_STMT)) {

			pstmt.setInt(1, prodCateId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					productCategoryVO = new ProductCategoryVO();
					productCategoryVO.setProdCateId(rs.getInt("PROD_CATE_ID"));
					productCategoryVO.setProdCate(rs.getString("PROD_CATE"));
				}
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occurred: " + se.getMessage(), se);
		}
		return productCategoryVO;
	}

	@Override
	public List<ProductCategoryVO> getAll() {

		List<ProductCategoryVO> list = new ArrayList<ProductCategoryVO>();
		ProductCategoryVO productCategoryVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				productCategoryVO = new ProductCategoryVO();

				productCategoryVO.setProdCateId(rs.getInt("PROD_CATE_ID"));
				productCategoryVO.setProdCate(rs.getString("PROD_CATE"));

				list.add(productCategoryVO);
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured." + se.getMessage());
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
