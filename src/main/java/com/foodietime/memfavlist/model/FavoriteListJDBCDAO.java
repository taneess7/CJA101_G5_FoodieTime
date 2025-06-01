package com.foodietime.memfavlist.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FavoriteListJDBCDAO implements FavoriteListDAO_interface{
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/g05?serverTimezone=Asia/Taipei";
	String userid = "root";
	String passwd = "lavender860201";

	private static final String INSERT_STMT = "INSERT INTO FAVORITE_LIST (mem_Id,prod_Id) VALUES (?,?)";
	private static final String GET_ALL_STMT = "SELECT mem_Id,prod_Id FROM favorite_list";
	private static final String GET_ONE_STMT = "SELECT mem_Id,prod_Id FROM favorite_list WHERE mem_Id = ? AND prod_Id = ?";
	private static final String DELETE_ONE = "DELETE FROM favorite_list WHERE mem_id = ? AND prod_id = ?";
	private static final String DELETE_ALL = "DELETE FROM favorite_list WHERE mem_id = ?";

	// 連線方法
	private Connection getConnection() throws Exception {
		Class.forName(driver);
		return DriverManager.getConnection(url, userid, passwd);
	}

	@Override // 新增
	public void insert(FavoriteListVO favoriteListVO) {
		try (Connection con = getConnection(); 
			PreparedStatement pstmt = con.prepareStatement(INSERT_STMT)) {

			pstmt.setInt(1, favoriteListVO.getMemId());
			pstmt.setInt(2, favoriteListVO.getProdId());
			pstmt.executeUpdate();
			System.out.println("新增成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteByMemIdAndProdId(Integer memId, Integer prodId) {
		try (Connection con = getConnection(); PreparedStatement pstmt = con.prepareStatement(DELETE_ONE)) {

			pstmt.setInt(1, memId);
			pstmt.setInt(2, prodId);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAllByMemId(Integer memId) {
		try (Connection con = getConnection(); PreparedStatement pstmt = con.prepareStatement(DELETE_ALL)) {

			pstmt.setInt(1, memId);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public FavoriteListVO findByPrimaryKey(Integer memId, Integer prodId) {
		FavoriteListVO vo = null;

		try (Connection con = getConnection(); PreparedStatement pstmt = con.prepareStatement(GET_ONE_STMT)) {

			pstmt.setInt(1, memId);
			pstmt.setInt(2, prodId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				vo = new FavoriteListVO();
				vo.setMemId(rs.getInt("mem_id"));
				vo.setProdId(rs.getInt("prod_id"));
			}

			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	@Override
	public List<FavoriteListVO> getAll() {
		List<FavoriteListVO> list = new ArrayList<>();

		try (Connection con = getConnection();
				PreparedStatement pstmt = con.prepareStatement(GET_ALL_STMT);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				FavoriteListVO vo = new FavoriteListVO();
				vo.setMemId(rs.getInt("mem_id"));
				vo.setProdId(rs.getInt("prod_id"));
				list.add(vo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static void main(String[] args) {
	    FavoriteListJDBCDAO dao = new FavoriteListJDBCDAO();

	    // ===== 新增測試 =====
	    FavoriteListVO insertVO = new FavoriteListVO();
	    insertVO.setMemId(1);
	    insertVO.setProdId(2);
	    dao.insert(insertVO);
	    System.out.println("新增完成");

	    // ===== 查詢單筆測試 =====
//	    FavoriteListVO foundVO = dao.findByPrimaryKey(10, 10);
//	    if (foundVO != null) {
//	        System.out.print(foundVO.getMemId() + ",");
//	        System.out.print(foundVO.getProdId());
//	        System.out.println();
//	    } else {
//	        System.out.println("查無此收藏資料");
//	    }

	    // ===== 查詢全部測試 =====
//	    List<FavoriteListVO> list = dao.getAll();
//	    for (FavoriteListVO fav : list) {
//	        System.out.print(fav.getMemId() + ",");
//	        System.out.print(fav.getProdId());
//	        System.out.println();
//	    }

	    // ===== 刪除單筆測試 =====
//	    dao.deleteByMemIdAndProdId(10, 10);
//	    System.out.println("已刪除 memId=99, prodId=999");

	    // ===== 刪除整位會員收藏測試 =====
//	    dao.deleteAllByMemId(1);
//	    System.out.println("已刪除 memId=99 所有收藏");

	}
}
