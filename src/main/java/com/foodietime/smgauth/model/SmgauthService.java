package com.foodietime.smgauth.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmgauthService {

    @Autowired
    private SmgauthRepository smgauthRepository;

    public List<SmgauthVO> findAllSmgauth() {
        return smgauthRepository.findAll();
    }

    public SmgauthVO findById(SmgauthId SmgauthId) {
        return smgauthRepository.findById(SmgauthId).orElse(null);
    }

    public SmgauthVO save(SmgauthVO smgauthVO) {
        return smgauthRepository.save(smgauthVO);
    }

    public void deleteById(SmgauthId SmgauthId) {
    	smgauthRepository.deleteById(SmgauthId);
    }
    
    // 根據管理員ID刪除所有權限
    @Transactional
    public void deleteBySmgrId(Integer smgId) {
        smgauthRepository.deleteByIdSmgId(smgId);
    }
    
    // 在新事務中創建權限記錄，避免級聯衝突
    @Transactional
    public void createPermissions(Integer smgrId, java.util.List<String> permissions, com.foodietime.smgfc.model.SmgfcService smgfcService) {
        // 獲取一個分離的 SmgVO 引用，避免級聯衝突
        com.foodietime.smg.model.SmgVO smgRef = new com.foodietime.smg.model.SmgVO();
        smgRef.setSmgrId(smgrId);
        
        for (String permission : permissions) {
            com.foodietime.smgfc.model.SmgfcVO smgfc = smgfcService.findByFunctionName(permission);
            if (smgfc != null) {
                SmgauthVO smgauth = new SmgauthVO();
                SmgauthId id = new SmgauthId(smgfc.getSmgFuncId(), smgrId);
                smgauth.setId(id);
                smgauth.setSmg(smgRef); // 設置分離的引用
                smgauth.setSmgfc(smgfc);
                smgauthRepository.save(smgauth);
            }
        }
    }

}
