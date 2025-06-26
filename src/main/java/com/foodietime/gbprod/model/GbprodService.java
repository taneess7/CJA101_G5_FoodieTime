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
    public byte[] getProductPhoto(Integer gbProdId) {
        return gbprodRepository.findById(gbProdId)
                   .map(GbprodVO::getGbProdPhoto)  // 這會呼叫你 VO 裡的 getter
                   .orElse(null);
    }
}
