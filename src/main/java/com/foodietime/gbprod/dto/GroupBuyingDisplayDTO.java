package com.foodietime.gbprod.dto;

import java.time.LocalDateTime;
import java.util.Base64;

import lombok.Data;

@Data
public class GroupBuyingDisplayDTO {
    
    // 團購案基本信息
    private Integer gbId;                    // 團購編號
    private String gbTitle;                  // 團購標題
    private String gbDescription;            // 團購簡介
    private LocalDateTime gbEndTime;         // 結束時間
    private Byte gbStatus;                   // 團購狀態
    private Integer gbMinProductQuantity;    // 最低成團商品數量
    private Integer cumulativePurchaseQuantity; // 累計購買數量
    
    // 商品信息
    private Integer gbProdId;                // 商品編號
    private String gbProdName;               // 商品名稱
    private String gbProdDescription;        // 商品描述
    private Byte[] gbProdPhoto;              // 商品圖片
    private String gbProdSpecification;      // 商品規格
    
    // 促銷信息
    private Integer gbProdSpe;               // 優惠價格
    private Integer gbProdSales;             // 銷售數量
    private Integer gbMinQty;                // 最小成團數
    
    // 店家信息
    private String storeName;                // 店家名稱
    
    // 計算屬性
    private Double progressPercentage;       // 進度百分比
    private String timeLeft;                 // 剩餘時間
    private String badgeType;                // 徽章類型 (熱門、新品、即將結束等)
    private String badgeText;                // 徽章文字
    private String productImageBase64;       // Base64編碼的圖片
    
    // 構造函數
    public GroupBuyingDisplayDTO() {}
    
    // 計算進度百分比
    public Double getProgressPercentage() {
        if (gbMinProductQuantity != null && gbMinProductQuantity > 0 && cumulativePurchaseQuantity != null) {
            return Math.min(100.0, (cumulativePurchaseQuantity.doubleValue() / gbMinProductQuantity.doubleValue()) * 100);
        }
        return 0.0;
    }
    
    // 獲取Base64編碼的圖片
    public String getProductImageBase64() {
        if (gbProdPhoto != null && gbProdPhoto.length > 0) {
            // 將Byte[]轉換為byte[]
            byte[] bytes = new byte[gbProdPhoto.length];
            for (int i = 0; i < gbProdPhoto.length; i++) {
                bytes[i] = gbProdPhoto[i];
            }
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
    
    // 計算剩餘時間
    public String getTimeLeft() {
        if (gbEndTime != null) {
            LocalDateTime now = LocalDateTime.now();
            if (gbEndTime.isAfter(now)) {
                long days = java.time.Duration.between(now, gbEndTime).toDays();
                long hours = java.time.Duration.between(now, gbEndTime).toHours() % 24;
                return days + " 天 " + hours + " 小時";
            } else {
                return "已結束";
            }
        }
        return "未知";
    }
    
    // 獲取徽章類型和文字
    public String getBadgeType() {
        if (gbStatus != null) {
            switch (gbStatus) {
                case 1: // 招募中
                    if (getProgressPercentage() >= 80) {
                        return "bg-warning text-dark";
                    } else if (gbProdSales != null && gbProdSales > 50) {
                        return "bg-danger";
                    } else {
                        return "bg-success";
                    }
                case 2: // 即將截止
                    return "bg-warning text-dark";
                case 3: // 已開團
                    return "bg-success";
                default:
                    return "bg-secondary";
            }
        }
        return "bg-secondary";
    }
    
    public String getBadgeText() {
        if (gbStatus != null) {
            switch (gbStatus) {
                case 1: // 招募中
                    if (getProgressPercentage() >= 80) {
                        return "即將結束";
                    } else if (gbProdSales != null && gbProdSales > 50) {
                        return "熱門";
                    } else {
                        return "新品";
                    }
                case 2: // 即將截止
                    return "即將結束";
                case 3: // 已開團
                    return "已開團";
                case 4: // 已截止
                    return "已截止";
                case 5: // 已取消
                    return "已取消";
                case 6: // 開團失敗
                    return "開團失敗";
                default:
                    return "待開團";
            }
        }
        return "待開團";
    }
}