# 全專案 AOP 異常處理使用說明

## 概述

本專案已實現 AOP (Aspect-Oriented Programming) 異常處理機制，可以自動攔截和處理整個專案中所有 Controller 的異常，包括前台和後台功能，無需在每個方法中手動編寫 try-catch 代碼。

## 實現的檔案

### 1. ExceptionAspect.java
位置：`src/main/java/com/foodietime/aspect/ExceptionAspect.java`

**主要功能：**
- 攔截 `com.foodietime` 包下所有 controller 的方法執行
- 攔截 `com.foodietime.smg.model.SmgService` 服務層方法
- 根據 Controller 類別名稱和方法名稱智能路由到適當的錯誤頁面
- 支援前台和後台的異常處理
- 記錄詳細的錯誤日誌

### 2. AopConfig.java
位置：`src/main/java/com/foodietime/config/AopConfig.java`

**功能：**
- 啟用 AspectJ 自動代理功能
- 確保 AOP 切面正常運作

## 使用方式

### 原本的寫法（需要手動處理異常）
```java
@PostMapping("/update")
public String updateSmg(@Valid @ModelAttribute("smg") SmgVO smgVO,
                        BindingResult result,
                        Model model) {
    try {
        // 業務邏輯
        SmgVO existingSmg = smgSvc.findById(smgVO.getSmgrId());
        if (existingSmg == null) {
            model.addAttribute("error", "找不到指定的管理員");
            return "admin/smg/admin-users-edit";
        }
        
        // 更新資料
        existingSmg.setSmgrName(smgVO.getSmgrName());
        smgSvc.save(existingSmg);
        
    } catch (Exception e) {
        model.addAttribute("error", "儲存失敗: " + e.getMessage());
        return "admin/smg/admin-users-edit";
    }
    
    return "redirect:/smg/admin-users-permissions";
}
```

### 現在的寫法（AOP 自動處理異常）
```java
@PostMapping("/update")
public String updateSmg(@Valid @ModelAttribute("smg") SmgVO smgVO,
                        BindingResult result,
                        Model model) {
    
    // 直接編寫業務邏輯，不需要 try-catch
    SmgVO existingSmg = smgSvc.findById(smgVO.getSmgrId());
    if (existingSmg == null) {
        model.addAttribute("error", "找不到指定的管理員");
        return "admin/smg/admin-users-edit";
    }
    
    // 更新資料 - 如果發生異常，AOP 會自動處理
    existingSmg.setSmgrName(smgVO.getSmgrName());
    smgSvc.save(existingSmg);
    
    return "redirect:/smg/admin-users-permissions";
}
```

## 異常處理機制

### 1. 異常類型處理
- **IllegalArgumentException**: 輸入參數錯誤
- **NullPointerException**: 空指標異常
- **RuntimeException**: 執行時錯誤
- **其他異常**: 系統未知錯誤

### 2. 錯誤頁面路由
根據 Controller 類別名稱和方法名稱自動決定錯誤後要返回的頁面：

**SMG 後台管理 (SmgController)：**
- `login` 相關方法 → `admin/smg/admin-login`
- `edit/update` 相關方法 → `admin/smg/admin-users-edit`
- `add/create` 相關方法 → `admin/smg/admin-users-add`
- `permissions` 相關方法 → `admin/smg/admin-users-permissions`
- `dashboard` 相關方法 → `admin/smg/admin-dashboard`
- 其他方法 → `admin/smg/admin-dashboard` (預設)

**前台會員 (MemberController)：**
- `login` 相關方法 → `front/member/login`
- `register` 相關方法 → `front/member/register`
- 其他方法 → `front/member/home` (預設)

**商品相關 (ProductController, ProductCategoryController)：**
- 所有方法 → `front/product/error`

**首頁相關 (IndexController)：**
- 所有方法 → `index`

**全域預設：**
- 其他所有 Controller → `error/500`

### 3. 錯誤訊息處理
- 自動將錯誤訊息加入到 `Model` 的 `error` 屬性
- 支援 `RedirectAttributes` 的 Flash 屬性
- 記錄詳細的錯誤日誌供除錯使用

## 日誌記錄

### 正常執行日誌
```
INFO  - 執行方法: SmgController.updateSmg
INFO  - 方法執行成功: SmgController.updateSmg
```

### 異常執行日誌
```
ERROR - 方法執行異常: SmgController.updateSmg, 錯誤訊息: 資料庫連線失敗
INFO  - 異常處理完成，類別: SmgController, 方法: updateSmg, 返回錯誤頁面: admin/smg/admin-users-edit
```

## 優點

1. **程式碼簡潔**: 移除重複的 try-catch 代碼
2. **統一處理**: 所有異常都經過統一的處理邏輯
3. **易於維護**: 異常處理邏輯集中在一個地方
4. **詳細日誌**: 自動記錄異常資訊供除錯
5. **靈活配置**: 可以根據方法名稱自訂錯誤頁面

## 注意事項

1. **AOP 只對 Spring 管理的 Bean 有效**，確保 Controller 有 `@Controller` 註解
2. **方法必須是 public**，AOP 無法攔截 private 方法
3. **如果需要特殊的異常處理邏輯**，可以在 `ExceptionAspect.java` 中擴展
4. **確保 pom.xml 中有 AOP 依賴**：
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-aop</artifactId>
   </dependency>
   ```

## 支援的 Controller 類型

目前 AOP 異常處理已支援以下 Controller：

1. **SmgController** - 後台管理員功能
2. **MemberController** - 前台會員功能
3. **ProductController** - 商品管理功能
4. **ProductCategoryController** - 商品分類功能
5. **IndexController** - 首頁功能

## 新增 Controller 支援

如果需要為新的 Controller 添加特定的錯誤頁面路由，可以在 `ExceptionAspect.java` 的 `getErrorView` 方法中添加相應的邏輯：

```java
// 在 getErrorView 方法中添加新的 Controller 處理
if (className.contains("YourNewController")) {
    if (methodName.contains("specificMethod")) {
        return "your/error/page";
    }
    return "your/default/error/page";
}
```

## 前後台登入頁面區分

系統能夠智能區分前台和後台的登入錯誤：
- **SmgController.login()** → 後台登入錯誤頁面 `admin/smg/admin-login`
- **MemberController.login()** → 前台登入錯誤頁面 `front/member/login`

這是透過檢查 Controller 類別名稱來實現的，確保錯誤處理的精確性。