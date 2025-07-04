package com.foodietime.資料庫建立;

import com.foodietime.util.ImageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class DatabaseImageWriter {

    // --- 1. 資料庫連線設定 (維持不變) ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/g05?serverTimezone=Asia/Taipei";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "123456";

    // ★★【新增】定義一個統一的商品圖片壓縮尺寸 ★★
    private static final int PRODUCT_IMAGE_COMPRESS_SIZE = 380; // 將商品圖片最長邊壓縮至 200px

    /**
     * 主程式進入點 (維持不變)
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n請選擇要執行的操作：");
                System.out.println("1. 寫入「一般商品」圖片 (product) - ★會進行壓縮★");
                System.out.println("2. 寫入「商家」圖片 (store)");
                System.out.println("3. 寫入「團購商品」圖片 (group_products)");
                System.out.println("4. 寫入「平台活動」圖片 (activity)");
                System.out.println("0. 結束程式");
                System.out.print("請輸入選項：");

                try {
                    int choice = scanner.nextInt();
                    switch (choice) {
                        case 1: writeProductImages(); break;
                        case 2: writeStoreImages(); break;
                        case 3: writeGroupBuyProductImages(); break;
                        case 4: writeActivityImages(); break;
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

    // --- 2. 各功能的公開方法 (修改呼叫方式) ---

    /**
     * 讀取並「壓縮」指定路徑的圖片，更新到 `product` 資料表。
     */
    public static void writeProductImages() {
        String photosPath = "src/main/resources/static/images/product";
        String updateSql = "UPDATE product SET PROD_PHOTO = ? WHERE PROD_ID = ?";
        System.out.println("\n--- 開始更新「一般商品」圖片 (啟用壓縮) ---");
        // ★★ 呼叫核心方法時，傳入 true 來啟用壓縮 ★★
        executeImageUpdate(photosPath, updateSql, 1, true);
    }

    /**
     * 讀取指定路徑的圖片，並更新到 `store` 資料表 (不壓縮)。
     */
    public static void writeStoreImages() {
        String photosPath = "src/main/resources/static/images/store";
        String updateSql = "UPDATE store SET STOR_PHOTO = ? WHERE STOR_ID = ?";
        System.out.println("\n--- 開始更新「商家」圖片 (不壓縮) ---");
        executeImageUpdate(photosPath, updateSql, 1, false);
    }

    /**
     * 讀取指定路徑的圖片，並更新到 `group_products` 資料表 (不壓縮)。
     */
    public static void writeGroupBuyProductImages() {
        String photosPath = "src/main/resources/static/images/DB_photos2/";
        String updateSql = "UPDATE group_products SET PROD_PHOTO = ? WHERE GB_PROD_ID = ?";
        System.out.println("\n--- 開始更新「團購商品」圖片 (不壓縮) ---");
        executeImageUpdate(photosPath, updateSql, 1, false);
    }

    /**
     * 讀取指定路徑的圖片，並更新到 `activity` 資料表 (不壓縮)。
     */
    public static void writeActivityImages() {
        String photosPath = "src/main/resources/static/images/act";
        String updateSql = "UPDATE activity SET ACT_PHOTO = ? WHERE ACT_ID = ?";
        System.out.println("\n--- 開始更新「平台活動」圖片 (不壓縮) ---");
        executeImageUpdate(photosPath, updateSql, 1, false);
    }

    // --- 3. 核心執行邏輯 (重構後) ---

    /**
     * 【重構版】執行圖片更新的核心方法，增加了是否壓縮的選項。
     *
     * @param photosPath     存放圖片檔案的資料夾路徑。
     * @param updateSql      要執行的 SQL UPDATE 語句。
     * @param startId        起始的 ID 編號。
     * @param shouldCompress 是否要對圖片進行壓縮。
     */
    private static void executeImageUpdate(String photosPath, String updateSql, int startId, boolean shouldCompress) {
        File photoDir = new File(photosPath);
        File[] photoFiles = photoDir.listFiles((dir, name) -> {
            String lowerCaseName = name.toLowerCase();
            return lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpeg");
        });

        if (photoFiles == null || photoFiles.length == 0) {
            System.err.println("❌ 在以下路徑找不到圖片或資料夾不存在：" + photoDir.getAbsolutePath());
            return;
        }

        // 排序邏輯維持不變
        Arrays.sort(photoFiles, Comparator.comparingInt(file -> {
            try {
                return Integer.parseInt(file.getName().replaceAll("\\..*$", ""));
            } catch (NumberFormatException e) {
                System.err.println("⚠️ 警告：檔名 " + file.getName() + " 非純數字，可能影響排序。");
                return Integer.MAX_VALUE;
            }
        }));

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = con.prepareStatement(updateSql)) {

            System.out.println("✅ 資料庫連線成功。");
            int currentId = startId;
            for (File file : photoFiles) {
                try {
                    // 1. 將整個圖片檔案讀取到 byte[] 陣列中
                    byte[] originalBytes = Files.readAllBytes(file.toPath());
                    byte[] finalBytes;

                    // 2. 如果需要壓縮，則呼叫 ImageUtil
                    if (shouldCompress) {
                        System.out.print("    -> 正在壓縮圖片 " + file.getName() + "... ");
                        ImageUtil.ShrinkResult result = ImageUtil.shrink(originalBytes, PRODUCT_IMAGE_COMPRESS_SIZE);
                        finalBytes = result.getImageData();
                        System.out.println("尺寸從 " + originalBytes.length / 1024 + " KB 變為 " + finalBytes.length / 1024 + " KB");
                    } else {
                        // 不需要壓縮，直接使用原圖的 byte[]
                        finalBytes = originalBytes;
                    }

                    // 3. 使用 setBytes 將最終的 byte[] 寫入資料庫
                    pstmt.setBytes(1, finalBytes);
                    pstmt.setInt(2, currentId);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("✅ 成功更新 ID = " + currentId + " 的圖片，來源：" + file.getName());
                    } else {
                        System.err.println("⚠️ 警告：更新 ID = " + currentId + " 時，沒有任何資料列被影響。");
                    }

                    currentId++;

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