package com.foodietime.gbprodcg.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GbprodcgService {

    @Autowired
    private GbprodcgRepository gbprodcgRepository;

    public List<GbprodcgVO> findAllGbprodcg() {
        return gbprodcgRepository.findAll();
    }

    public GbprodcgVO findById(Integer gbProdId) {
        return gbprodcgRepository.findById(gbProdId).orElse(null);
    }

    public GbprodcgVO save(GbprodcgVO gbprodcgVO) {
        return gbprodcgRepository.save(gbprodcgVO);
    }

    public void deleteById(Integer gbProdId) {
    	gbprodcgRepository.deleteById(gbProdId);
    }
    
}
