package com.foodietime.groupbuyingcases.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodietime.grouporders.model.GroupOrdersRepository;

@Service
public class GroupBuyingCasesService {

    @Autowired
    private GroupBuyingCasesRepository groupBuyingCasesRepository;

    @Autowired
    private GroupOrdersRepository groupOrdersRepository;

    // 查詢某會員參加的所有團購案
    public List<GroupBuyingCasesVO> findByMemId(Integer memId) {
        return groupBuyingCasesRepository.findByMember_MemId(memId);
    }
    
    // 查詢某會員開設且是團主的團購案
    public List<GroupBuyingCasesVO> findByMember_MemIdAndLeader(Integer memberId, Byte leader) {
        return groupBuyingCasesRepository.findByMember_MemIdAndLeader(memberId, leader);
    }
    
    // 查詢某會員開設且是團主的團購案，並根據狀態篩選
    public List<GroupBuyingCasesVO> findByMember_MemIdAndLeaderAndGbStatus(Integer memberId, Byte leader, Byte gbStatus) {
        return groupBuyingCasesRepository.findByMember_MemIdAndLeaderAndGbStatus(memberId, leader, gbStatus);
    }
    
    /**
     * 查單筆：同時符合 gbId、member.memId、以及參與者表的 leader 欄位
     * 只有當前會員是該團購的團主 (leader = 0) 時，才會回傳
     */
    public GroupBuyingCasesVO findByGbIdAndMember_MemIdAndLeader(
            Integer gbId, Integer memId, Byte leader) {
        return groupBuyingCasesRepository
           .findDistinctByParticipants_Member_MemIdAndParticipants_Leader(memId, leader)
           .stream()
           .filter(g -> g.getGbId().equals(gbId))
           .findFirst()
           .orElse(null);
    }
    
    
    
    public List<GroupBuyingCasesVO> findLeaderCases(Integer memId) {
        return groupBuyingCasesRepository.findDistinctByParticipants_Member_MemIdAndParticipants_Leader(memId, (byte)0);
    }
    
    
    // 查詢某店家開的所有團購案
    public List<GroupBuyingCasesVO> findByStoreId(Integer storId) {
        return groupBuyingCasesRepository.findByStore_StorId(storId);
    }

    // 查詢某商品(編號或名稱)對應的團購案
    public List<GroupBuyingCasesVO> findByProdIdOrName(Integer gbProdId, String gbProdName) {
        return groupBuyingCasesRepository.findByGbProd_GbProdIdOrGbProd_GbProdNameContaining(gbProdId, gbProdName);
    }


    // 根據團購編號查詢團購案
    public Optional<GroupBuyingCasesVO> findById(Integer gbId) {
        return groupBuyingCasesRepository.findById(gbId);
    }

    // 新增或修改團購案
    public GroupBuyingCasesVO saveGroupBuyingCase(GroupBuyingCasesVO groupBuyingCasesVO) {
        return groupBuyingCasesRepository.save(groupBuyingCasesVO);
    }



    // 修改團購案狀態
    public GroupBuyingCasesVO updateGroupBuyingCaseStatus(Integer gbId, Byte newStatus) {
        Optional<GroupBuyingCasesVO> groupBuyingCaseOpt = groupBuyingCasesRepository.findById(gbId);
        if (groupBuyingCaseOpt.isPresent()) {
            GroupBuyingCasesVO groupBuyingCase = groupBuyingCaseOpt.get();
            groupBuyingCase.setGbStatus(newStatus);
            return groupBuyingCasesRepository.save(groupBuyingCase);
        }
        return null; // 如果找不到對應的團購案
    }
    
    // 根據狀態查詢團購案
    public List<GroupBuyingCasesVO> findByGbStatus(Byte gbStatus) {
        return groupBuyingCasesRepository.findByGbStatus(gbStatus);
    }
    
    // 根據狀態查詢團購案並按銷售量降序排列
    public List<GroupBuyingCasesVO> findByGbStatusOrderByCumulativePurchaseQuantityDesc(Byte gbStatus) {
        return groupBuyingCasesRepository.findByGbStatusOrderByCumulativePurchaseQuantityDesc(gbStatus);
    }
    
    // 根據狀態查詢團購案並按創建時間降序排列
    public List<GroupBuyingCasesVO> findByGbStatusOrderByGbCreateAtDesc(Byte gbStatus) {
        return groupBuyingCasesRepository.findByGbStatusOrderByGbCreateAtDesc(gbStatus);
    }
    
    // 根據標題關鍵字和狀態搜尋團購案
    public List<GroupBuyingCasesVO> findByGbProd_GbProdNameContainingAndGbStatus(String gbProdName, Byte gbStatus) {
        return groupBuyingCasesRepository.findByGbProd_GbProdNameContainingAndGbStatus(gbProdName, gbStatus);
    }


    public List<GroupBuyingCasesVO> findAllEndedAndUnsettled() {
        // 假設 gbStatus=4 表示已結束，且有 settled 欄位
        // 請根據實際欄位調整查詢條件
        return groupBuyingCasesRepository.findByGbStatusAndSettled((byte)4, false);
    }

    @Transactional
    public void cancelGroupBuyingCaseByLeader(Integer gbId, String reason) {
        // 1. 更新團購案狀態為 4（自主取消）
        GroupBuyingCasesVO gbCase = groupBuyingCasesRepository.findById(gbId)
            .orElseThrow(() -> new RuntimeException("找不到團購案"));
        gbCase.setGbStatus((byte)4);
        gbCase.setCancelReason(reason);
        groupBuyingCasesRepository.save(gbCase);

        // 2. 查出所有訂單，設為 4
        java.util.List<com.foodietime.grouporders.model.GroupOrdersVO> orders = groupOrdersRepository.findByGroupBuyingCase_GbId(gbId);
        for (com.foodietime.grouporders.model.GroupOrdersVO order : orders) {
            order.setOrderStatus((byte)4);
            groupOrdersRepository.save(order);
        }
    }

    @Transactional
    public void failGroupBuyingCaseBySystem(Integer gbId, String reason) {
        // 1. 更新團購案狀態為 3（開團失敗/取消）
        GroupBuyingCasesVO gbCase = groupBuyingCasesRepository.findById(gbId)
            .orElseThrow(() -> new RuntimeException("找不到團購案"));
        gbCase.setGbStatus((byte)3);
        gbCase.setCancelReason(reason);
        groupBuyingCasesRepository.save(gbCase);

        // 2. 查出所有訂單，設為 3
        java.util.List<com.foodietime.grouporders.model.GroupOrdersVO> orders = groupOrdersRepository.findByGroupBuyingCase_GbId(gbId);
        for (com.foodietime.grouporders.model.GroupOrdersVO order : orders) {
            order.setOrderStatus((byte)3);
            groupOrdersRepository.save(order);
        }
    }
    
    @Transactional
    public void updateGroupBuyingCaseStatusAndOrders(Integer gbId, Byte newStatus, String reason) {
        // 1. 更新團購案狀態
        GroupBuyingCasesVO gbCase = groupBuyingCasesRepository.findById(gbId)
            .orElseThrow(() -> new RuntimeException("找不到團購案"));
        gbCase.setGbStatus(newStatus);
        if (reason != null) {
            gbCase.setCancelReason(reason);
        }
        groupBuyingCasesRepository.save(gbCase);

        // 2. 查出所有訂單
        java.util.List<com.foodietime.grouporders.model.GroupOrdersVO> orders = groupOrdersRepository.findByGroupBuyingCase_GbId(gbId);

        // 3. 用 switch-case 對應訂單狀態
        Byte orderStatus = null;
        switch (newStatus != null ? newStatus.intValue() : -1) {
            case 5: // 5: 已取消（團主手動取消）
                orderStatus = 3; // 3: 訂單已取消
                break;
            case 6: // 6: 開團失敗（未達標自動失敗）
                orderStatus = 4; // 4: 訂單開團失敗
                break;
            default:
                // 其他狀態不處理
        }
        if (orderStatus != null) {
            for (com.foodietime.grouporders.model.GroupOrdersVO order : orders) {
                order.setOrderStatus(orderStatus);
                groupOrdersRepository.save(order);
            }
        }
    }

   
    //當累積購買數量達到最低成團數量時，自動將狀態從招募中(1)改為已成團(3)
   	@Transactional
      public void checkAndUpdateGroupStatusIfReachedMin(Integer gbId) {
          GroupBuyingCasesVO gbCase = groupBuyingCasesRepository.findById(gbId).orElseThrow(() -> new RuntimeException("找不到團購案"));
          if (gbCase.getGbStatus() == 1 &&
              gbCase.getCumulativePurchaseQuantity() >= gbCase.getGbMinProductQuantity()) {
              gbCase.setGbStatus((byte)3); // 3: 已成團
              groupBuyingCasesRepository.save(gbCase);
              }
          }
    
  //根據結束日期與累積購買數量自動判斷團購狀態*/@Transactional
    @Transactional
    public void autoUpdateGroupStatus(Integer gbId) {
        GroupBuyingCasesVO gbCase = groupBuyingCasesRepository.findById(gbId).orElseThrow(() -> new RuntimeException("找不到團購案"));
        LocalDateTime now = LocalDateTime.now();
        if (gbCase.getGbStatus() == 5) {
            // 已取消不自動變更
            return;
        }
        if (gbCase.getGbEndTime().isAfter(now)) {
            // 結束時間未到，狀態設為招募中
            gbCase.setGbStatus((byte)1); // 1: 招募中
        } else {
            // 結束時間已到
            if (gbCase.getCumulativePurchaseQuantity() >= gbCase.getGbMinProductQuantity()) {
                gbCase.setGbStatus((byte)4); // 4: 已截止（成團）
            } else {
                gbCase.setGbStatus((byte)6); // 6: 開團失敗
                // 將所有訂單設為 4（開團失敗）
                java.util.List<com.foodietime.grouporders.model.GroupOrdersVO> orders = groupOrdersRepository.findByGroupBuyingCase_GbId(gbId);
                for (com.foodietime.grouporders.model.GroupOrdersVO order : orders) {
                    order.setOrderStatus((byte)4); // 4: 訂單開團失敗
                    groupOrdersRepository.save(order);
                }
            }
        }
        groupBuyingCasesRepository.save(gbCase);
    }

    // 將所有已到期且狀態為招募中的團購案 gbStatus=1 改為 gbStatus=4
    @Transactional
    public void updateEndedRecruitingCasesToClosed() {
        List<GroupBuyingCasesVO> endedRecruitingCases = groupBuyingCasesRepository.findAllEndedRecruiting();
        for (GroupBuyingCasesVO gbCase : endedRecruitingCases) {
            gbCase.setGbStatus((byte)4); // 4: 已截止
            groupBuyingCasesRepository.save(gbCase);
        }
    }

}
