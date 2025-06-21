package com.foodietime.gbprod.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 團購商品詳情頁面 DTO
 * 整合 GroupBuyingCasesVO、GbprodVO、GbpromotionVO、MemberVO、StoreVO 等資料
 */
public class ProductDetailDTO {
    
    // ==================== 團購案基本資訊 ====================
    private Integer gbId;
    private String gbTitle;
    private String gbDescription;
    private LocalDateTime gbStartTime;
    private LocalDateTime gbEndTime;
    private Byte gbStatus;
    
    // ==================== 商品資訊 ====================
    private Integer gbProdId;
    private String gbProdName;
    private String gbProdDescription;
    private String gbProdSpecification;
    private byte[] gbProdPhoto;
    private Integer gbProdQuantity;
    private Byte gbProdStatus;
    
    // ==================== 價格和促銷資訊 ====================
    private Integer originalPrice;      // 原價
    private Integer currentPrice;       // 現價
    private String discountPercent;     // 折扣百分比
    private List<PriceTier> priceTiers; // 階梯價格
    
    // ==================== 進度資訊 ====================
    private Integer gbMinProductQuantity;        // 最低成團數量
    private Integer cumulativePurchaseQuantity;  // 累計購買數量
    private Double progressPercent;              // 進度百分比
    private Integer soldCount;                   // 已售數量
    private Integer targetCount;                 // 目標數量
    
    // ==================== 時間相關 ====================
    private String timeLeft;         // 剩餘時間文字
    private Integer remainingDays;   // 剩餘天數
    private Integer remainingHours;  // 剩餘小時
    private String endDate;          // 結束日期
    
    // ==================== 團長資訊 ====================
    private String organizerName;    // 團長姓名
    private String organizerAvatar;  // 團長頭像
    private Double organizerRating;  // 團長評價
    private Integer reviewCount;     // 評價數量
    
    // ==================== 店家資訊 ====================
    private String storeName;        // 店家名稱
    private String storeDescription; // 店家描述
    private String storeAddress;     // 店家地址
    
    // ==================== 下一階段優惠資訊 ====================
    private Integer nextTierRemaining; // 距離下一階段還需數量
    private Integer nextTierPrice;     // 下一階段價格
    private Integer nextTierTarget;    // 下一階段目標數量
    private String nextTierProgress;   // 下一階段進度文字
    
    // ==================== 計算屬性 ====================
    private String badgeType;          // 徽章類型
    private String badgeText;          // 徽章文字
    private String productImageBase64;  // 商品圖片 Base64
    
    // ==================== 內部類別：價格階梯 ====================
    public static class PriceTier {
        private Integer minQuantity;  // 最小數量
        private Integer price;        // 對應價格
        private String description;   // 描述
        
        public PriceTier() {}
        
        public PriceTier(Integer minQuantity, Integer price, String description) {
            this.minQuantity = minQuantity;
            this.price = price;
            this.description = description;
        }
        
        // Getters and Setters
        public Integer getMinQuantity() { return minQuantity; }
        public void setMinQuantity(Integer minQuantity) { this.minQuantity = minQuantity; }
        
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    // ==================== 建構子 ====================
    public ProductDetailDTO() {}
    
    // ==================== Getters and Setters ====================
    
    // 團購案基本資訊
    public Integer getGbId() { return gbId; }
    public void setGbId(Integer gbId) { this.gbId = gbId; }
    
    public String getGbTitle() { return gbTitle; }
    public void setGbTitle(String gbTitle) { this.gbTitle = gbTitle; }
    
    public String getGbDescription() { return gbDescription; }
    public void setGbDescription(String gbDescription) { this.gbDescription = gbDescription; }
    
    public LocalDateTime getGbStartTime() { return gbStartTime; }
    public void setGbStartTime(LocalDateTime gbStartTime) { this.gbStartTime = gbStartTime; }
    
    public LocalDateTime getGbEndTime() { return gbEndTime; }
    public void setGbEndTime(LocalDateTime gbEndTime) { this.gbEndTime = gbEndTime; }
    
    public Byte getGbStatus() { return gbStatus; }
    public void setGbStatus(Byte gbStatus) { this.gbStatus = gbStatus; }
    
    // 商品資訊
    public Integer getGbProdId() { return gbProdId; }
    public void setGbProdId(Integer gbProdId) { this.gbProdId = gbProdId; }
    
    public String getGbProdName() { return gbProdName; }
    public void setGbProdName(String gbProdName) { this.gbProdName = gbProdName; }
    
    public String getGbProdDescription() { return gbProdDescription; }
    public void setGbProdDescription(String gbProdDescription) { this.gbProdDescription = gbProdDescription; }
    
    public String getGbProdSpecification() { return gbProdSpecification; }
    public void setGbProdSpecification(String gbProdSpecification) { this.gbProdSpecification = gbProdSpecification; }
    
    public byte[] getGbProdPhoto() { return gbProdPhoto; }
    public void setGbProdPhoto(byte[] gbProdPhoto) { this.gbProdPhoto = gbProdPhoto; }
    
    public Integer getGbProdQuantity() { return gbProdQuantity; }
    public void setGbProdQuantity(Integer gbProdQuantity) { this.gbProdQuantity = gbProdQuantity; }
    
    public Byte getGbProdStatus() { return gbProdStatus; }
    public void setGbProdStatus(Byte gbProdStatus) { this.gbProdStatus = gbProdStatus; }
    
    // 價格和促銷資訊
    public Integer getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Integer originalPrice) { this.originalPrice = originalPrice; }
    
    public Integer getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Integer currentPrice) { this.currentPrice = currentPrice; }
    
    public String getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(String discountPercent) { this.discountPercent = discountPercent; }
    
    public List<PriceTier> getPriceTiers() { return priceTiers; }
    public void setPriceTiers(List<PriceTier> priceTiers) { this.priceTiers = priceTiers; }
    
    // 進度資訊
    public Integer getGbMinProductQuantity() { return gbMinProductQuantity; }
    public void setGbMinProductQuantity(Integer gbMinProductQuantity) { this.gbMinProductQuantity = gbMinProductQuantity; }
    
    public Integer getCumulativePurchaseQuantity() { return cumulativePurchaseQuantity; }
    public void setCumulativePurchaseQuantity(Integer cumulativePurchaseQuantity) { this.cumulativePurchaseQuantity = cumulativePurchaseQuantity; }
    
    public Double getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Double progressPercent) { this.progressPercent = progressPercent; }
    
    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
    
    public Integer getTargetCount() { return targetCount; }
    public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }
    
    // 時間相關
    public String getTimeLeft() { return timeLeft; }
    public void setTimeLeft(String timeLeft) { this.timeLeft = timeLeft; }
    
    public Integer getRemainingDays() { return remainingDays; }
    public void setRemainingDays(Integer remainingDays) { this.remainingDays = remainingDays; }
    
    public Integer getRemainingHours() { return remainingHours; }
    public void setRemainingHours(Integer remainingHours) { this.remainingHours = remainingHours; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    // 團長資訊
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
    
    public String getOrganizerAvatar() { return organizerAvatar; }
    public void setOrganizerAvatar(String organizerAvatar) { this.organizerAvatar = organizerAvatar; }
    
    public Double getOrganizerRating() { return organizerRating; }
    public void setOrganizerRating(Double organizerRating) { this.organizerRating = organizerRating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    
    // 店家資訊
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    
    public String getStoreDescription() { return storeDescription; }
    public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }
    
    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
    
    // 下一階段優惠資訊
    public Integer getNextTierRemaining() { return nextTierRemaining; }
    public void setNextTierRemaining(Integer nextTierRemaining) { this.nextTierRemaining = nextTierRemaining; }
    
    public Integer getNextTierPrice() { return nextTierPrice; }
    public void setNextTierPrice(Integer nextTierPrice) { this.nextTierPrice = nextTierPrice; }
    
    public Integer getNextTierTarget() { return nextTierTarget; }
    public void setNextTierTarget(Integer nextTierTarget) { this.nextTierTarget = nextTierTarget; }
    
    public String getNextTierProgress() { return nextTierProgress; }
    public void setNextTierProgress(String nextTierProgress) { this.nextTierProgress = nextTierProgress; }
    
    // 計算屬性
    public String getBadgeType() { return badgeType; }
    public void setBadgeType(String badgeType) { this.badgeType = badgeType; }
    
    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }
    
    public String getProductImageBase64() { return productImageBase64; }
    public void setProductImageBase64(String productImageBase64) { this.productImageBase64 = productImageBase64; }
    
    // ==================== 計算方法 ====================
    
    /**
     * 計算進度百分比
     */
    public void calculateProgressPercent() {
        if (gbMinProductQuantity != null && gbMinProductQuantity > 0 && cumulativePurchaseQuantity != null) {
            this.progressPercent = (double) cumulativePurchaseQuantity / gbMinProductQuantity * 100;
            // 限制最大值為 100%
            if (this.progressPercent > 100) {
                this.progressPercent = 100.0;
            }
        } else {
            this.progressPercent = 0.0;
        }
    }
    
    /**
     * 計算折扣百分比
     */
    public void calculateDiscountPercent() {
        if (originalPrice != null && currentPrice != null && originalPrice > 0) {
            double discount = (double) (originalPrice - currentPrice) / originalPrice * 100;
            this.discountPercent = String.format("%.0f%%", discount);
        } else {
            this.discountPercent = "0%";
        }
    }
    
    /**
     * 計算剩餘時間
     */
    public void calculateTimeLeft() {
        if (gbEndTime != null) {
            LocalDateTime now = LocalDateTime.now();
            if (gbEndTime.isAfter(now)) {
                java.time.Duration duration = java.time.Duration.between(now, gbEndTime);
                long days = duration.toDays();
                long hours = duration.toHours() % 24;
                
                this.remainingDays = (int) days;
                this.remainingHours = (int) hours;
                
                if (days > 0) {
                    this.timeLeft = days + "天" + hours + "小時";
                } else if (hours > 0) {
                    this.timeLeft = hours + "小時";
                } else {
                    long minutes = duration.toMinutes() % 60;
                    this.timeLeft = minutes + "分鐘";
                }
            } else {
                this.timeLeft = "已結束";
                this.remainingDays = 0;
                this.remainingHours = 0;
            }
        }
    }
    
    /**
     * 計算徽章類型和文字
     */
    public void calculateBadge() {
        if (cumulativePurchaseQuantity != null) {
            if (cumulativePurchaseQuantity >= 100) {
                this.badgeType = "hot";
                this.badgeText = "熱銷";
            } else if (cumulativePurchaseQuantity >= 50) {
                this.badgeType = "popular";
                this.badgeText = "人氣";
            } else {
                this.badgeType = "new";
                this.badgeText = "新品";
            }
        } else {
            this.badgeType = "new";
            this.badgeText = "新品";
        }
    }
    
    @Override
    public String toString() {
        return "ProductDetailDTO{" +
                "gbId=" + gbId +
                ", gbTitle='" + gbTitle + '\'' +
                ", gbProdName='" + gbProdName + '\'' +
                ", currentPrice=" + currentPrice +
                ", progressPercent=" + progressPercent +
                ", timeLeft='" + timeLeft + '\'' +
                '}';
    }
}