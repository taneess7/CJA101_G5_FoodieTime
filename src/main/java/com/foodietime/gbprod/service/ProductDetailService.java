package com.foodietime.gbprod.service;

import java.util.Optional;
import java.util.Base64;
import java.util.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.gbprod.dto.ProductDetailDTO;
import com.foodietime.gbpromotion.model.GbpromotionVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesService;

@Service
public class ProductDetailService {
    
    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;
    
    /**
     * 根據團購編號獲取商品詳情
     * @param gbId 團購編號
     * @return 商品詳情DTO
     */
    public ProductDetailDTO getProductDetail(Integer gbId) {
        Optional<GroupBuyingCasesVO> gbCaseOpt = groupBuyingCasesService.findById(gbId);
        
        if (!gbCaseOpt.isPresent()) {
            return null;
        }
        
        GroupBuyingCasesVO gbCase = gbCaseOpt.get();
        return convertToProductDetailDTO(gbCase);
    }
    
    /**
     * 將GroupBuyingCasesVO轉換為ProductDetailDTO
     * @param gbCase 團購案VO
     * @return 商品詳情DTO
     */
    private ProductDetailDTO convertToProductDetailDTO(GroupBuyingCasesVO gbCase) {
        ProductDetailDTO dto = new ProductDetailDTO();
        
        // ==================== 團購案基本資訊 ====================
        dto.setGbId(gbCase.getGbId());
        dto.setGbTitle(gbCase.getGbTitle());
        dto.setGbDescription(gbCase.getGbDescription());
        dto.setGbStartTime(gbCase.getGbStartTime());
        dto.setGbEndTime(gbCase.getGbEndTime());
        dto.setGbStatus(gbCase.getGbStatus());
        
        // ==================== 商品資訊 ====================
        if (gbCase.getGbProd() != null) {
            dto.setGbProdId(gbCase.getGbProd().getGbProdId());
            dto.setGbProdName(gbCase.getGbProd().getGbProdName());
            dto.setGbProdDescription(gbCase.getGbProd().getGbProdDescription());
            dto.setGbProdSpecification(gbCase.getGbProd().getGbProdSpecification());
            
            Byte[] photoWrapper = gbCase.getGbProd().getGbProdPhoto(); // 你的 Byte[] 來源
            if (photoWrapper != null) {
                byte[] photo = new byte[photoWrapper.length];
                for (int i = 0; i < photoWrapper.length; i++) {
                    photo[i] = photoWrapper[i]; // 自動 unbox
                }
                dto.setGbProdPhoto(photo);
            }
            
            dto.setGbProdQuantity(gbCase.getGbProd().getGbProdQuantity());
            dto.setGbProdStatus(gbCase.getGbProd().getGbProdStatus());
            
            // 設置商品圖片的Base64編碼
            if (gbCase.getGbProd().getGbProdPhoto() != null) {
                // 將 Byte[] 轉為 byte[]
                Byte[] wrapperBytes = gbCase.getGbProd().getGbProdPhoto();
                byte[] primitiveBytes = new byte[wrapperBytes.length];
                for (int i = 0; i < wrapperBytes.length; i++) {
                    primitiveBytes[i] = wrapperBytes[i]; // Auto-unboxing
                }

                // Base64 編碼
                String base64Image = Base64.getEncoder().encodeToString(primitiveBytes);
                dto.setProductImageBase64(base64Image);
            }
            
            // 設置促銷資訊
            if (gbCase.getGbProd().getGbpromotionList() != null && !gbCase.getGbProd().getGbpromotionList().isEmpty()) {
                GbpromotionVO promotion = gbCase.getGbProd().getGbpromotionList().get(0);
                dto.setOriginalPrice(promotion.getGbProdSpe());
                dto.setCurrentPrice(promotion.getGbProdSales());
                
                // 計算折扣百分比
                if (promotion.getGbProdSpe() != null && promotion.getGbProdSales() != null && promotion.getGbProdSpe() > 0) {
                    double discount = ((promotion.getGbProdSpe() - promotion.getGbProdSales()) * 100.0) / promotion.getGbProdSpe();
                    dto.setDiscountPercent(String.format("%.0f%%", Math.max(0, discount)));
                } else {
                    dto.setDiscountPercent("0%");
                }
            } else {
                // 預設價格
                dto.setOriginalPrice(399);
                dto.setCurrentPrice(299);
                dto.setDiscountPercent("25%");
            }
        }
        
        // ==================== 進度資訊 ====================
        dto.setGbMinProductQuantity(gbCase.getGbMinProductQuantity());
        dto.setCumulativePurchaseQuantity(gbCase.getCumulativePurchaseQuantity());
        dto.setSoldCount(gbCase.getCumulativePurchaseQuantity());
        dto.setTargetCount(gbCase.getGbMinProductQuantity());
        
        // 計算進度百分比
        if (gbCase.getCumulativePurchaseQuantity() != null && gbCase.getGbMinProductQuantity() != null && gbCase.getGbMinProductQuantity() > 0) {
            double progress = (gbCase.getCumulativePurchaseQuantity().doubleValue() / gbCase.getGbMinProductQuantity().doubleValue()) * 100;
            dto.setProgressPercent(Math.min(100.0, progress));
        } else {
            dto.setProgressPercent(0.0);
        }
        
        // ==================== 時間資訊 ====================
        if (gbCase.getGbEndTime() != null) {
        	LocalDateTime now = LocalDateTime.now();
        	Date promotEnd = gbCase.getGbProd().getGbpromotionList().get(0).getPromotEnd();
        	LocalDateTime end = promotEnd.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

		//計算時間差
		Duration duration = Duration.between(now, end);
            
            dto.setRemainingDays((int) duration.toDays());
            dto.setRemainingHours((int) (duration.toHours() % 24));
            dto.setRemainingMinutes((int) (duration.toMinutes() % 60));
            dto.setEndDate(gbCase.getGbEndTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        }
        
        // ==================== 會員資訊（團長） ====================
        if (gbCase.getMember() != null) {
            dto.setLeaderNickname(gbCase.getMember().getMemName());
            if (gbCase.getMember().getMemAvatar() != null) {
                String base64Avatar = Base64.getEncoder().encodeToString(gbCase.getMember().getMemAvatar());
                dto.setLeaderAvatar(base64Avatar);
            }
        }
        
        // ==================== 店家資訊 ====================
        if (gbCase.getStore() != null) {
            dto.setStoreName(gbCase.getStore().getStorName());
            dto.setStoreAddress(gbCase.getStore().getStorAddr());
            dto.setStoreDescription(gbCase.getStore().getStorDesc());
        }
        
        // ==================== 階梯價格資訊（暫時設為固定值） ====================
        dto.setNextTierPrice(dto.getCurrentPrice() != null ? dto.getCurrentPrice() - 30 : 269);
        dto.setNextTierRemaining(5);
        dto.setCanSaveAmount(30);
        
        // ==================== 商品規格（暫時設為固定值） ====================
        dto.setPackageOptions("精美禮盒裝、簡約包裝、加購提袋");
        dto.setQuantity("12個/盒");
        
        return dto;
    }
}