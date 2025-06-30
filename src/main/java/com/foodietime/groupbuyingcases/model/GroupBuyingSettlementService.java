package com.foodietime.groupbuyingcases.model;

import com.foodietime.accrec.model.AccrecService;
import com.foodietime.accrec.model.AccrecVO;
import com.foodietime.grouporders.model.GroupOrdersRepository;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.smg.model.SmgService;
import com.foodietime.smg.model.SmgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupBuyingSettlementService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private GroupOrdersRepository groupOrdersRepository;
    @Autowired
    private AccrecService accrecService;
    @Autowired
    private SmgService smgService;
    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;
    
    
    
    @Transactional
    public void settleGroupBuying(GroupBuyingCasesVO groupCase) {
        System.out.println("[結算] 開始結算團購案 gbId=" + groupCase.getGbId());
        // 1. 判斷是否達標
        int totalQty = groupCase.getCumulativePurchaseQuantity() != null ? groupCase.getCumulativePurchaseQuantity() : 0;
        int minQty = groupCase.getGbMinProductQuantity();
        System.out.println("[結算] 累計購買數量=" + totalQty + ", 最低成團數量=" + minQty);
        if (totalQty >= minQty) {
            System.out.println("[結算] 達標，開始推播與撥款...");
            // 查詢所有訂單
            List<GroupOrdersVO> orders = groupOrdersRepository.findByGroupBuyingCase_GbId(groupCase.getGbId());
            // 推播給所有參與會員
            for (GroupOrdersVO order : orders) {
                Integer memberId = order.getGroupBuyingCase().getMember().getMemId();
                System.out.println("[推播] 給會員 memberId=" + memberId);
                messagingTemplate.convertAndSend("/topic/member/" + memberId, "您的團購已達標，請留意出貨通知！");
            }
            // 推播給商家
            System.out.println("[推播] 給商家 storeId=" + groupCase.getStore().getStorId());
            messagingTemplate.convertAndSend("/topic/store/" + groupCase.getStore().getStorId(), "您的團購案已達標，請準備出貨");

            // 新增 AccrecVO
            for (GroupOrdersVO order : orders) {
                // 商家撥款
                System.out.println("[撥款] 商家 storeId=" + order.getStore().getStorId() + ", 訂單編號=" + order.getGbOrId());
                AccrecVO storeAccrec = new AccrecVO();
                storeAccrec.setOrderType((byte)1); // 1=團購
                storeAccrec.setOrderRefId(order.getGbOrId());
                storeAccrec.setStore(order.getStore());
                storeAccrec.setPayoutRole((byte)0); // 0=商家
                storeAccrec.setPayoutAmount(BigDecimal.valueOf(order.getAmount() * 0.8));
                storeAccrec.setCommissionAmount(null);
                storeAccrec.setPayoutStatus((byte)0); // 0=待撥款
                storeAccrec.setCommissionStatus(null);
                storeAccrec.setCreateAt(LocalDateTime.now());
                storeAccrec.setUpdateAt(LocalDateTime.now());
                storeAccrec.setPayoutTime(null);
                storeAccrec.setPayoutMonth(null);
                SmgVO admin = smgService.findById(1); // 查詢管理員ID=1的資料
                storeAccrec.setServerManager(admin);
                accrecService.save(storeAccrec);

                // 團主傭金
                System.out.println("[傭金] 團主 memberId=" + groupCase.getMember().getMemId() + ", 訂單編號=" + order.getGbOrId());
                AccrecVO leaderAccrec = new AccrecVO();
                leaderAccrec.setOrderType((byte)1);
                leaderAccrec.setOrderRefId(order.getGbOrId());
                leaderAccrec.setMember(groupCase.getMember());
                leaderAccrec.setPayoutRole((byte)1); // 1=團主
                leaderAccrec.setPayoutAmount(null);
                leaderAccrec.setCommissionAmount(BigDecimal.valueOf(order.getAmount() * 0.2));
                leaderAccrec.setPayoutStatus(null);
                leaderAccrec.setCommissionStatus((byte)0);
                leaderAccrec.setCreateAt(LocalDateTime.now());
                leaderAccrec.setUpdateAt(LocalDateTime.now());
                leaderAccrec.setPayoutTime(null);
                leaderAccrec.setPayoutMonth(null);
                leaderAccrec.setServerManager(admin);
                accrecService.save(leaderAccrec);
            }
        } else {
            System.out.println("[結算] 未達標，開始推播退款...");
            List<GroupOrdersVO> orders = groupOrdersRepository.findByGroupBuyingCase_GbId(groupCase.getGbId());
            for (GroupOrdersVO order : orders) {
                Integer memberId = order.getGroupBuyingCase().getMember().getMemId();
                System.out.println("[推播] 退款通知給會員 memberId=" + memberId);
                messagingTemplate.convertAndSend("/topic/member/" + memberId, "團購未達標，已自動退款");
            }
        }
        // ...更新團購案狀態為已結算...
        groupCase.setSettled(true);
        System.out.println("[結算] 設定 settled=true 並儲存 gbId=" + groupCase.getGbId());
        groupBuyingCasesService.saveGroupBuyingCase(groupCase);
        System.out.println("[結算] 結算完成 gbId=" + groupCase.getGbId());
    }
} 