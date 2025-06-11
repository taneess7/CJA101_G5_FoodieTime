package com.foodietime.accrec.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccrecService {

    @Autowired
    private AccrecRepository accrecRepository;

    public List<AccrecVO> findAllAccrec() {
        return accrecRepository.findAll();
    }

    public AccrecVO findById(Integer commPayoutID) {
        return accrecRepository.findById(commPayoutID).orElse(null);
    }

    public AccrecVO save(AccrecVO accrecVO) {
        return accrecRepository.save(accrecVO);
    }

    public void deleteById(Integer commPayoutID) {
    	accrecRepository.deleteById(commPayoutID);
    }

}
