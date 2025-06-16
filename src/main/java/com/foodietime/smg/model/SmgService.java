package com.foodietime.smg.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmgService {

    @Autowired
    private SmgRepository smgRepository;

    public List<SmgVO> findAllSmgs() {
        return smgRepository.findAll();
    }

    public SmgVO findById(Integer id) {
        return smgRepository.findById(id).orElse(null);
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
}
