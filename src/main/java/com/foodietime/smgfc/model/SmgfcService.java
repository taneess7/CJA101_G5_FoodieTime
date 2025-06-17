package com.foodietime.smgfc.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmgfcService {

    @Autowired
    private SmgfcRepository smgfcRepository;

    public List<SmgfcVO> findAllSmgs() {
        return smgfcRepository.findAll();
    }

    public SmgfcVO findById(Integer smgFuncId) {
        return smgfcRepository.findById(smgFuncId).orElse(null);
    }

    public SmgfcVO save(SmgfcVO smgfcVO) {
        return smgfcRepository.save(smgfcVO);
    }

    public void deleteById(Integer smgFuncId) {
        smgfcRepository.deleteById(smgFuncId);
    }
    
    // 根據功能名稱查找功能
    public SmgfcVO findByFunctionName(String functionName) {
        List<SmgfcVO> allFunctions = smgfcRepository.findAll();
        return allFunctions.stream()
                .filter(func -> func.getSmgFunc().equals(functionName))
                .findFirst()
                .orElse(null);
    }

}
