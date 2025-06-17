package com.foodietime.smg.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.smgauth.model.SmgauthId;
import com.foodietime.smgauth.model.SmgauthRepository;
import com.foodietime.smgauth.model.SmgauthVO;
import com.foodietime.smgfc.model.SmgfcRepository;
import com.foodietime.smgfc.model.SmgfcVO;
@Service
public class SmgService {

    @Autowired
    private SmgRepository smgRepository;
    @Autowired
    private SmgfcRepository smgfcRepository;
    @Autowired
    private SmgauthRepository smgauthRepository;
    
    
    public List<SmgVO> findAllSmgs() {
        return smgRepository.findAll();
    }

    public SmgVO findById(Integer id) {
        SmgVO smg = smgRepository.findById(id).orElse(null);
        if (smg != null) {
            // 手動初始化權限關聯資料，避免 LazyInitializationException
            smg.getSmgauths().size(); // 觸發載入
        }
        return smg;
    }

    @Transactional
    public SmgVO save(SmgVO smg) {
        return smgRepository.save(smg);
    }

    public void deleteById(Integer id) {
        smgRepository.deleteById(id);
    }
    public SmgVO login(String account, String password) {
        SmgVO smg = smgRepository.findBySmgrAccount(account);
        if (smg != null && smg.getSmgrPassword().equals(password)) {
            return smg;
        }
        return null;
    }

	public SmgVO findBySmgrAccount(String smgrAccount) {
		return smgRepository.findBySmgrAccount(smgrAccount);
	}
	public List<SmgVO> searchByConditions(String account, String name, String email) {
	    return smgRepository.findByConditions(account, name, email);
	}
	
	public void createSmgWithAuth(SmgVO smgVO, List<Integer> funcIdList) {
	    // 1. 儲存管理員
	    SmgVO savedSmg = smgRepository.save(smgVO);

	    // 2. 根據功能 ID 清單建立授權資料
	    for (Integer funcId : funcIdList) {
	        SmgauthId authId = new SmgauthId(funcId, savedSmg.getSmgrId());

	        SmgauthVO auth = new SmgauthVO();
	        auth.setId(authId);
	        auth.setSmg(savedSmg);

	        SmgfcVO smgfc = smgfcRepository.findById(funcId).orElseThrow(() -> new RuntimeException("功能不存在"));
	        auth.setSmgfc(smgfc);

	        smgauthRepository.save(auth);
	    }
	}

}
