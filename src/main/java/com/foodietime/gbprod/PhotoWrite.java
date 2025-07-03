package com.foodietime.gbprod;


import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Comparator;

public class PhotoWrite {

    public static void main(String[] argv) {
        String url = "jdbc:mysql://localhost:3306/g05?serverTimezone=Asia/Taipei";
        String userid = "root";
        String passwd = "123456";
        String photos = "src/main/resources/static/images/DB_photos2/";
        String update = "UPDATE group_products SET PROD_PHOTO = ? WHERE GB_PROD_ID = ?";

        File[] photoFiles = new File(photos).listFiles();

        if (photoFiles == null || photoFiles.length == 0) {
            System.err.println("❌ 資料夾不存在或沒有圖片：" + new File(photos).getAbsolutePath());
            return;
        }

        // ✅ 加上數字排序（避免字串順序錯誤）
        Arrays.sort(photoFiles, Comparator.comparingInt(file -> {
            String name = file.getName().replace(".jpg", "");
            return Integer.parseInt(name); // 假設都是數字命名的 jpg
        }));

        try (Connection con = DriverManager.getConnection(url, userid, passwd);
             PreparedStatement pstmt = con.prepareStatement(update)) {

            int count = 1;
            for (File file : photoFiles) {
                try (FileInputStream fin = new FileInputStream(file)) {
                    pstmt.setBinaryStream(1, fin);
                    pstmt.setInt(2, count); // 按排序後順序更新
                    pstmt.executeUpdate();

                    System.out.println("✅ 更新成功：GB_PROD_ID = " + count + "，圖片 = " + file.getName());
                    count++;
                } catch (IOException ioEx) {
                    System.err.println("⚠️ 無法讀取圖片：" + file.getName());
                    ioEx.printStackTrace();
                }
            }

            System.out.println("🎉 所有圖片已寫入資料庫！");

        } catch (SQLException sqlEx) {
            System.err.println("❌ 資料庫操作錯誤！");
            sqlEx.printStackTrace();
        }
    }
}
