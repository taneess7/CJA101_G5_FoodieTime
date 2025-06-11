package com.foodietime.smgfc.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmgfcService {

    @Autowired
    private SmgfcRepository SmgfcRepository;

    public List<SmgfcVO> findAllSmgs() {
        return SmgfcRepository.findAll();
    }

    public SmgfcVO findById(Integer smgFuncId) {
        return SmgfcRepository.findById(smgFuncId).orElse(null);
    }

    public SmgfcVO save(SmgfcVO smgfcVO) {
        return SmgfcRepository.save(smgfcVO);
    }

    public void deleteById(Integer smgFuncId) {
    	SmgfcRepository.deleteById(smgFuncId);
    }
    
}
