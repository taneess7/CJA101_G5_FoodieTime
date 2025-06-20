package com.foodietime.gbprod.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.gbprod.dto.GroupBuyingDisplayDTO;
import com.foodietime.gbpromotion.model.GbpromotionVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcases.service.GroupBuyingCasesService;

@Service
public class GroupBuyingDisplayService {
    
    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;
    
    /**
     * 獲取熱門團購商品列表
     * @return 熱門團購DTO列表
     */
    public List<GroupBuyingDisplayDTO> getPopularProducts() {
        // 獲取狀態為招募中(1)且銷售量較高的團購案
        List<GroupBuyingCasesVO> popularCases = groupBuyingCasesService.findByGbStatusOrderByGbProdSalesDesc((byte) 1);
        
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
    private GroupBuyingDisplayDTO convertToDTO(GroupBuyingCasesVO gbCase) {
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
            dto.setGbProdId(gbCase.getGbProd().getGbProdId());
            dto.setGbProdName(gbCase.getGbProd().getGbProdName());
            dto.setGbProdDescription(gbCase.getGbProd().getGbProdDescription());
            dto.setGbProdPhoto(gbCase.getGbProd().getGbProdPhoto());
            dto.setGbProdSpecification(gbCase.getGbProd().getGbProdSpecification());
            
            // 設置促銷信息（從商品的促銷列表中獲取最新的促銷信息）
            if (gbCase.getGbProd().getGbpromotionList() != null && !gbCase.getGbProd().getGbpromotionList().isEmpty()) {
                // 獲取最新的促銷信息
                GbpromotionVO latestPromotion = gbCase.getGbProd().getGbpromotionList().get(0);
                dto.setGbProdSpe(latestPromotion.getGbProdSpe());
                dto.setGbProdSales(latestPromotion.getGbProdSales());
                dto.setGbMinQty(latestPromotion.getGbMinQty());
            }
        }
        
        // 設置店家信息
        if (gbCase.getStore() != null) {
            dto.setStoreName(gbCase.getStore().getStorName());
        }
        
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