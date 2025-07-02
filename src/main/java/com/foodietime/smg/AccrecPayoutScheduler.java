package com.foodietime.smg;

import com.foodietime.accrec.model.AccrecService;
import com.foodietime.accrec.model.AccrecVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AccrecPayoutScheduler {
    @Autowired
    private AccrecService accrecService;

    // 每月1號凌晨1點執行
    @Scheduled(cron = "0 0 1 1 * ?")
    public void autoPayout() {
        accrecService.payoutAllPending();
    }

    // 可供API呼叫
    public void payoutAllPending() {
        accrecService.payoutAllPending();
    }

    public void payoutOne(Integer commPayoutID) {
        accrecService.payoutOne(commPayoutID);
    }
} 