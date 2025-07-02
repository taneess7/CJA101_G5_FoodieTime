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

    public void payoutAllPending() {
        List<AccrecVO> accrecList = findAllAccrec();
        String thisMonth = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (AccrecVO acc : accrecList) {
            boolean updated = false;
            if (acc.getPayoutStatus() != null && acc.getPayoutStatus() == 0) {
                acc.setPayoutMonth(thisMonth);
                acc.setPayoutTime(now);
                acc.setPayoutStatus((byte)1);
                updated = true;
            }
            if (acc.getCommissionStatus() != null && acc.getCommissionStatus() == 0) {
                acc.setPayoutMonth(thisMonth);
                acc.setPayoutTime(now);
                acc.setCommissionStatus((byte)1);
                updated = true;
            }
            if (updated) {
                save(acc);
            }
        }
    }

    public void payoutOne(Integer commPayoutID) {
        AccrecVO acc = findById(commPayoutID);
        if (acc != null) {
            String thisMonth = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            boolean updated = false;
            if (acc.getPayoutStatus() != null && acc.getPayoutStatus() == 0) {
                acc.setPayoutMonth(thisMonth);
                acc.setPayoutTime(now);
                acc.setPayoutStatus((byte)1);
                updated = true;
            }
            if (acc.getCommissionStatus() != null && acc.getCommissionStatus() == 0) {
                acc.setPayoutMonth(thisMonth);
                acc.setPayoutTime(now);
                acc.setCommissionStatus((byte)1);
                updated = true;
            }
            if (updated) {
                save(acc);
            }
        }
    }

}
