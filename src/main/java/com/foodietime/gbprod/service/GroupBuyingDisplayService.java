package com.foodietime.gbprod.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.gbprod.dto.GroupBuyingDisplayDTO;
import com.foodietime.gbpromotion.model.GbpromotionVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesService;

@Service
public class GroupBuyingDisplayService {
    
    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;
    
    /**
     * 獲取熱門團購商品列表
     * @return 熱門團購DTO列表
     */
    public List<GroupBuyingDisplayDTO> getPopularProducts() {
        // 獲取狀態為招募中(1)且累計購買數量較高的團購案
        List<GroupBuyingCasesVO> popularCases = groupBuyingCasesService.findByGbStatusOrderByCumulativePurchaseQuantityDesc((byte) 1);
        
        return popularCases.stream()
                .limit(4) // 限制顯示4個
                .map(this::convertToDTO)  
                .collect(Collectors.toList());
    }
    
    /**
     * 獲取最新上架商品列表
     * @return 最新上架DTO列表
     */
    public List<GroupBuyingDisplayDTO> getLatestProducts() {
        // 獲取最新建立的團購案
        List<GroupBuyingCasesVO> latestCases = groupBuyingCasesService.findByGbStatusOrderByGbCreateAtDesc((byte) 1);
        
        return latestCases.stream()
                .limit(4) // 限制顯示4個
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 將GroupBuyingCasesVO轉換為GroupBuyingDisplayDTO
     * @param gbCase 團購案VO
     * @return 團購顯示DTO
     */
    private GroupBuyingDisplayDTO  convertToDTO(GroupBuyingCasesVO gbCase) {
        GroupBuyingDisplayDTO dto = new GroupBuyingDisplayDTO();
        
        // 設置團購案基本信息
        dto.setGbId(gbCase.getGbId());
        dto.setGbTitle(gbCase.getGbTitle());
        dto.setGbDescription(gbCase.getGbDescription());
        dto.setGbEndTime(gbCase.getGbEndTime());
        dto.setGbStatus(gbCase.getGbStatus());
        dto.setGbMinProductQuantity(gbCase.getGbMinProductQuantity());
        dto.setCumulativePurchaseQuantity(gbCase.getCumulativePurchaseQuantity());
        
        // 設置商品信息
        if (gbCase.getGbProd() != null) {
            // dto.setGbProdId(gbCase.getGbProd().getGbProdId()); // 未使用
            dto.setGbProdName(gbCase.getGbProd().getGbProdName());
            // dto.setGbProdDescription(gbCase.getGbProd().getGbProdDescription()); // 未使用
            dto.setGbProdPhoto(gbCase.getGbProd().getGbProdPhoto());
            // dto.setGbProdSpecification(gbCase.getGbProd().getGbProdSpecification()); // 未使用
            
            // 設置促銷信息（從商品的促銷列表中獲取最新的促銷信息）
            if (gbCase.getGbProd().getGbpromotionList() != null && !gbCase.getGbProd().getGbpromotionList().isEmpty()) {
                // 獲取最新的促銷信息
                GbpromotionVO latestPromotion = gbCase.getGbProd().getGbpromotionList().get(0);
                dto.setGbProdSpe(latestPromotion.getGbProdSpe());
                // dto.setGbProdSales(latestPromotion.getGbProdSales()); // 未使用
                // dto.setGbMinQty(latestPromotion.getGbMinQty()); // 未使用
            }
        } else {
            // 如果沒有商品資訊，使用團購案標題作為商品名稱
            dto.setGbProdName(gbCase.getGbTitle());
        }
        
        // 設置店家信息
        // if (gbCase.getStore() != null) {
        //     dto.setStoreName(gbCase.getStore().getStorName()); // 未使用
        // }
        
        // 設置價格相關屬性
        dto.setOriginalPrice(dto.getGbProdSpe() != null ? dto.getGbProdSpe() : 399);
        // dto.setCurrentPrice(dto.getGbProdSales() != null ? dto.getGbProdSales() : 299); // 未使用
        
        // 計算折扣百分比 - 暫時設為固定值，因為currentPrice未使用
        // if (dto.getOriginalPrice() != null && dto.getCurrentPrice() != null && dto.getOriginalPrice() > 0) {
        //     double discount = ((dto.getOriginalPrice() - dto.getCurrentPrice()) * 100.0) / dto.getOriginalPrice();
        //     dto.setDiscountPercent(String.format("%.0f%%", Math.max(0, discount)));
        // } else {
        //     dto.setDiscountPercent("0%");
        // }
        dto.setDiscountPercent("33%"); // 暫時設為固定值
        
        // 設置階梯相關屬性
        dto.setCurrentTier(1); // 預設為第1階段
        
        // 設置時間相關屬性
        if (gbCase.getGbEndTime() != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.Duration duration = java.time.Duration.between(now, gbCase.getGbEndTime());
            dto.setRemainingDays((int) duration.toDays());
            dto.setRemainingHours((int) (duration.toHours() % 24));
            dto.setRemainingMinutes((int) (duration.toMinutes() % 60));
            dto.setEndDate(gbCase.getGbEndTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        }
        
        // 設置進度相關屬性
        if (gbCase.getCumulativePurchaseQuantity() != null && gbCase.getGbMinProductQuantity() != null) {
            double progress = (gbCase.getCumulativePurchaseQuantity().doubleValue() / gbCase.getGbMinProductQuantity().doubleValue()) * 100;
            dto.setProgressPercent((int) Math.min(100, progress));
            dto.setSoldCount(gbCase.getCumulativePurchaseQuantity());
            dto.setTargetCount(gbCase.getGbMinProductQuantity());
        }
        
        // 設置階梯價格相關屬性（暫時設為固定值，實際應根據業務邏輯計算）
        // dto.setNextTierPrice(dto.getCurrentPrice() - 30); // currentPrice未使用，改為固定值
        dto.setNextTierPrice(269); // 暫時設為固定值
        dto.setNextTierRemaining(5);
        dto.setNextTierTarget(30);
        dto.setNextTierProgress("83");
        
        // 設置商品規格相關屬性（暫時設為固定值）
        dto.setPackageOptions("精美禮盒裝、簡約包裝、加購提袋");
        dto.setQuantity("12個/盒");
        dto.setUnitWeight("約50公克");
        dto.setTotalWeight("約600公克");
        
        return dto;
    }
    
    /**
     * 獲取所有活躍的團購商品
     * @return 所有活躍團購DTO列表
     */
    public List<GroupBuyingDisplayDTO> getAllActiveProducts() {
        List<GroupBuyingCasesVO> activeCases = groupBuyingCasesService.findByGbStatus((byte) 1);
        
        return activeCases.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根據關鍵字搜尋團購商品
     * @param keyword 搜尋關鍵字
     * @return 搜尋結果DTO列表
     */
    public List<GroupBuyingDisplayDTO> searchProducts(String keyword) {
        List<GroupBuyingCasesVO> searchResults = groupBuyingCasesService.findByGbTitleContainingAndGbStatus(keyword, (byte) 1);
        
        return searchResults.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}