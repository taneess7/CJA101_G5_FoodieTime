package com.foodietime.gbprod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.gbprod.model.GbprodRepository;
import com.foodietime.gbprod.model.GbprodVO;

@Service
public class GbleaderService {
	
	@Autowired
    private GbprodRepository gbprodRepository;

    /**
     * 獲取指定店家及其促銷價格
     */
    public List<GbprodVO> getStoresWithPromotionPrices() {
        // 調用 repository 查詢店家及其促銷價格
        return gbprodRepository.findStoresWithOrdersByStatus();
    }

    public List<GbprodVO> searchProducts(String keyword) {
        return gbprodRepository.searchByNameOrProdIdOrStoreId(keyword);
    }
}
