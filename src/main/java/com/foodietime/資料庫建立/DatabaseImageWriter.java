package com.foodietime.資料庫建立;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class DatabaseImageWriter {

    // --- 1. 資料庫連線設定 (集中管理) ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/g05?serverTimezone=Asia/Taipei";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123456";

    /**
     * 主程式進入點，提供一個選單讓使用者選擇要執行的操作。
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n請選擇要執行的操作：");
                System.out.println("1. 寫入「一般商品」圖片 (product)");
                System.out.println("2. 寫入「商家」圖片 (store)");
                System.out.println("3. 寫入「團購商品」圖片 (group_products)");
                System.out.println("0. 結束程式");
                System.out.print("請輸入選項：");

                try {
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1:
                            writeProductImages();
                            break;
                        case 2:
                            writeStoreImages();
                            break;
                        case 3:
                            writeGroupBuyProductImages();
                            break;
                        case 0:
                            System.out.println("程式已結束。");
                            return;
                        default:
                            System.err.println("無效的選項，請重新輸入。");
                    }
                } catch (InputMismatchException e) {
                    System.err.println("輸入錯誤，請輸入數字。");
                    scanner.next(); // 清除無效的輸入
                }
            }
        }
    }

    // --- 2. 各功能的公開方法 ---

    /**
     * 讀取指定路徑的圖片，並更新到 `product` 資料表。
     */
    public static void writeProductImages() {
        String photosPath = "src/main/resources/static/images/product";
        String updateSql = "UPDATE product SET PROD_PHOTO = ? WHERE PROD_ID = ?";
        System.out.println("\n--- 開始更新「一般商品」圖片 ---");
        executeImageUpdate(photosPath, updateSql, 1);
    }

    /**
     * 讀取指定路徑的圖片，並更新到 `store` 資料表。
     */
    public static void writeStoreImages() {
        String photosPath = "src/main/resources/static/images/store";
        String updateSql = "UPDATE store SET STOR_PHOTO = ? WHERE STOR_ID = ?";
        System.out.println("\n--- 開始更新「商家」圖片 ---");
        executeImageUpdate(photosPath, updateSql, 1);
    }

    /**
     * 讀取指定路徑的圖片，並更新到 `group_products` 資料表。
     */
    public static void writeGroupBuyProductImages() {
        String photosPath = "src/main/resources/static/images/DB_photos2/";
        String updateSql = "UPDATE group_products SET PROD_PHOTO = ? WHERE GB_PROD_ID = ?";
        System.out.println("\n--- 開始更新「團購商品」圖片 ---");
        executeImageUpdate(photosPath, updateSql, 1);
    }

    // --- 3. 核心執行邏輯 (私有方法) ---

    /**
     * 執行圖片更新的核心方法。
     *
     * @param photosPath  存放圖片檔案的資料夾路徑。
     * @param updateSql   要執行的 SQL UPDATE 語句。
     * @param startId     起始的 ID 編號。
     */
    private static void executeImageUpdate(String photosPath, String updateSql, int startId) {
        File photoDir = new File(photosPath);
        File[] photoFiles = photoDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        if (photoFiles == null || photoFiles.length == 0) {
            System.err.println("❌ 在以下路徑找不到圖片或資料夾不存在：" + photoDir.getAbsolutePath());
            return;
        }

        // 依據檔案名稱中的數字進行排序，確保寫入順序正確
        Arrays.sort(photoFiles, Comparator.comparingInt(file -> {
            try {
                // 移除副檔名後，將檔名轉為數字
                return Integer.parseInt(file.getName().replaceAll("\\..*$", ""));
            } catch (NumberFormatException e) {
                // 如果檔名不是純數字，給予一個預設值，使其排在後面
                System.err.println("⚠️ 警告：檔名 " + file.getName() + " 非純數字，可能影響排序。");
                return Integer.MAX_VALUE;
            }
        }));

        // 使用 try-with-resources 確保連線和語句能自動關閉
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = con.prepareStatement(updateSql)) {

            System.out.println("✅ 資料庫連線成功。");
            int count = startId;
            for (File file : photoFiles) {
                // 每個檔案的 FileInputStream 也應在 try-with-resources 中
                try (FileInputStream fin = new FileInputStream(file)) {
                    pstmt.setBinaryStream(1, fin, file.length());
                    pstmt.setInt(2, count);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("✅ 成功更新 ID = " + count + " 的圖片，來源：" + file.getName());
                    } else {
                        System.err.println("⚠️ 警告：更新 ID = " + count + " 時，沒有任何資料列被影響。");
                    }

                    count++;
                } catch (IOException ioEx) {
                    System.err.println("❌ 讀取圖片失敗：" + file.getAbsolutePath());
                    ioEx.printStackTrace();
                }
            }
            System.out.println("\n🎉 所有圖片已成功寫入資料庫！");

        } catch (SQLException sqlEx) {
            System.err.println("❌ 資料庫操作發生錯誤！");
            sqlEx.printStackTrace();
        }
    }
}
