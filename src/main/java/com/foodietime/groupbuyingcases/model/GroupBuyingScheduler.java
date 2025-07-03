package com.foodietime.groupbuyingcases.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GroupBuyingScheduler {
    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;
    @Autowired
    private GroupBuyingSettlementService groupBuyingSettlementService;

    // 每5分鐘執行一次
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkGroupBuyingEnd() {
        // 先將所有已到期且狀態為招募中的團購案 gbStatus=1 改為 gbStatus=4
        groupBuyingCasesService.updateEndedRecruitingCasesToClosed();
        // 原本的結算流程
        List<GroupBuyingCasesVO> endedCases = groupBuyingCasesService.findAllEndedAndUnsettled();
        for (GroupBuyingCasesVO groupCase : endedCases) {
            groupBuyingSettlementService.settleGroupBuying(groupCase);
        }
    }
} 