package com.foodietime.smgauth.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
