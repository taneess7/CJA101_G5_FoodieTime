package com.foodietime.gbprod.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GbprodService {

    @Autowired
    private GbprodRepository gbprodRepository;

    public List<GbprodVO> findAllGbprod() {
        return gbprodRepository.findAll();
    }

    public GbprodVO findById(Integer gbProdId) {
        return gbprodRepository.findById(gbProdId).orElse(null);
    }

    public GbprodVO save(GbprodVO gbprodVO) {
        return gbprodRepository.save(gbprodVO);
    }

    public void deleteById(Integer gbProdId) {
        gbprodRepository.deleteById(gbProdId);
    }

//     遠端加的功能：取得產品圖片
    public byte[] getProductPhoto(Integer gbProdId) {
        return gbprodRepository.findById(gbProdId)
                   .map(GbprodVO::getGbProdPhotoBytes)
                   .orElse(null);
    }

    // 本地加的功能：查詢某店家的所有團購商品
    public List<GbprodVO> findByStore(com.foodietime.store.model.StoreVO store) {
        return gbprodRepository.findByStore(store);
    }

}
