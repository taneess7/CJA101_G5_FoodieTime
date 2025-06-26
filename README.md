# FoodieTime 美食社群平台

> 為生活步調快、對價格敏感的用戶打造的整合式美食社群生態系

## 專案簡介

**FoodieTime** 是一個專為學生、上班族、旅客設計的綜合性美食社群平台，致力於提供「省錢、省時、便利」與「多元選擇」的用餐體驗。本平台不僅是個訂餐工具，更是一個結合美食地圖、社群分享、評論、團購與個人化收藏的完整生態系統，讓每一次用餐都成為獨特的美好體驗。

## 核心功能特色

- **🗺️ 美食地圖** - 智慧定位周邊美食資訊
- **👥 社群分享** - 用戶美食體驗分享與互動  
- **⭐ 評論系統** - 真實用戶評價與評分機制
- **💰 團購功能** - 集體採購享受優惠價格
- **❤️ 個人收藏** - 客製化美食清單管理
- **🎯 個人化推薦** - 基於用戶偏好的智慧推薦

## 技術架構

### 後端技術棧
- **語言**: Java
- **框架**: Spring Boot
- **核心模組**: Spring Web (MVC), Spring Data JPA, Spring Mail,Spring Security
- **資料庫互動**: Hibernate (JPA Provider)
- **資料庫**: MySQL
- **建置工具**: Maven

### 前端技術棧
- **模板引擎**: Thymeleaf
- **UI 框架**: Bootstrap
- **樣式與腳本**: CSS, JavaScript, Font Awesome

### 系統架構
- **分層架構** (Layered Architecture)
  - Controller 層：處理 HTTP 請求與回應
  - Service 層：核心業務邏輯封裝
  - Repository 層：資料庫存取操作
  - VO (Entity) 層：資料庫實體映射

## 開發規範

### 版本控制 (Git Flow)
本專案採用 Git 分組-個人 分支模型進行版本管理[1]：

```
master          # 穩定發布版本
Tan             # 組長的分支
諸如此類...
```

### Commit 訊息規範
遵循 Conventional Commits 格式：
```
(): 

範例：
feat(member): 實作使用者註冊API
fix(groupbuy): 修正團購數量計算錯誤
```

### 程式碼規範
- **DTO 模式**: Controller 層嚴格使用 DTO 進行資料傳輸
- **交易管理**: Service 層方法使用 `@Transactional` 註解
- **安全性**: 密碼使用 BCrypt 加密，敏感資訊使用環境變數管理
- **註解要求**: 所有方法必須包含詳細的功能說明與參數描述

## 快速開始

### 環境需求
- Java 11+
- MySQL 8.0+
- Maven 3.6+
- Node.js 16+ (前端開發)

### 安裝步驟

1. **複製專案**
```bash
git clone 
cd FoodieTime
```

2. **設定資料庫**
```sql
CREATE DATABASE foodietime CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **環境變數設定**
建立 `application-dev.properties` 檔案：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foodietime
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.mail.password=${MAIL_PASSWORD}
```

4. **執行應用程式**
```bash
mvn spring-boot:run
```

5. **存取應用程式**
開啟瀏覽器前往: `http://localhost:8080`

## 專案結構

```
src/main/java/com/foodietime/
├── member/          # 會員管理模組
├── post/           # 貼文分享模組  
├── groupbuy/       # 團購功能模組
├── restaurant/     # 餐廳資訊模組
├── review/         # 評論系統模組
└── common/         # 共用元件模組
```

## 測試

### 執行單元測試
```bash
mvn test
```

### 執行整合測試  
```bash
mvn integration-test
```

## 貢獻指南

1. Fork 此專案
2. 建立功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交變更 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 開啟 Pull Request

## 授權聲明

本專案採用 MIT 授權條款 - 詳見 [LICENSE](LICENSE) 檔案

## 聯絡資訊

- 專案維護者：FoodieTime 開發團隊
- 電子郵件：contact@foodietime.com
- 專案網址：[GitHub Repository](https://github.com/username/FoodieTime)

⭐ 如果這個專案對您有幫助，請給我們一個 Star！
