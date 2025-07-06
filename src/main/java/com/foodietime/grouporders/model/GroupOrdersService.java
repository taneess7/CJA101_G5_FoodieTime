package com.foodietime.grouporders.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.groupbuyingcases.model.GroupBuyingCasesRepository;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesService;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupOrdersService {

    @Autowired
    private GroupOrdersRepository groupOrdersRepository;
    @Autowired
    private GroupBuyingCasesRepository groupBuyingCasesRepository;
    @Autowired
    private GroupBuyingCasesService groupBuyingCasesService;


    //取得當前會員（memId）擔任團長 (leader = 0) 的所有場次訂單
    public List<GroupOrdersVO> getOrdersForLeader(Integer memId) {
        // 1) 撈出此會員擔任團長的所有場次 GB ID
        List<Integer> gbIds = groupBuyingCasesRepository
            .findDistinctByParticipants_Member_MemIdAndParticipants_Leader(memId, (byte)0)
            .stream()
            .map(GroupBuyingCasesVO::getGbId)
            .collect(Collectors.toList());

        if (gbIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) 批次撈取這些場次的所有訂單
        return groupOrdersRepository.findByGroupBuyingCase_GbIdIn(gbIds);
    }
    
    //取得團長 (leader = 0) 開團的單筆訂單
    public GroupOrdersVO getLeaderOrder(Integer memId, Integer orderId) {
        return groupOrdersRepository
            .findLeaderOrder(orderId, memId)
            .orElseThrow(() -> 
                new IllegalArgumentException("找不到此筆訂單或您無權查看")
            );
    }
    


    // 根據訂單狀態查詢團購訂單
    public List<GroupOrdersVO> getOrdersByStatus(Byte orderStatus) {
        return groupOrdersRepository.findByOrderStatus(orderStatus);
    }

    // 根據付款狀態查詢團購訂單
    public List<GroupOrdersVO> getOrdersByPaymentStatus(Byte paymentStatus) {
        return groupOrdersRepository.findByPaymentStatus(paymentStatus);
    }

    // 根據出貨狀態查詢團購訂單
    public List<GroupOrdersVO> getOrdersByShippingStatus(Byte shippingStatus) {
        return groupOrdersRepository.findByShippingStatus(shippingStatus);
    }

    // 查詢某一團購的所有團購訂單
    public List<GroupOrdersVO> getOrdersByGroupBuyingCaseId(Integer gbId) {
        return groupOrdersRepository.findByGroupBuyingCase_GbId(gbId);
    }

    // 根據商品編號查詢訂單
    public List<GroupOrdersVO> getOrdersByProductId(Integer gbProdId) {
        return groupOrdersRepository.findByGbprod_GbProdId(gbProdId);
    }

    // 查詢某一店家的所有團購訂單
    public List<GroupOrdersVO> getOrdersByStoreId(Integer storId) {
        return groupOrdersRepository.findByStore_StorId(storId);
    }

    // 根據訂單編號查詢訂單詳情
    public Optional<GroupOrdersVO> getOrderById(Integer gbOrId) {
        return groupOrdersRepository.findById(gbOrId);
    }

    // 新增或修改團購訂單
    public GroupOrdersVO save(GroupOrdersVO groupOrdersVO) {
        // 若是新訂單，預設狀態設為 1（接單）
        if (groupOrdersVO.getGbOrId() == null) {
            groupOrdersVO.setOrderStatus((byte)1);
        }
        GroupOrdersVO savedOrder = groupOrdersRepository.save(groupOrdersVO);
        return savedOrder;
    }

    // 更新訂單狀態（包括訂單狀態、付款狀態和出貨狀態）
    public GroupOrdersVO updateOrderField(Integer gbOrId, String field, Byte newStatus) {
        // 查找訂單
        Optional<GroupOrdersVO> orderOpt = groupOrdersRepository.findById(gbOrId);
        if (orderOpt.isPresent()) {
            GroupOrdersVO order = orderOpt.get();
            
            // 根據傳入的字段名稱更新相應的狀態
            switch (field) {
                case "orderStatus":
                    order.setOrderStatus(newStatus); // 更新訂單狀態
                    break;
                case "paymentStatus":
                    order.setPaymentStatus(newStatus); // 更新付款狀態
                    break;
                case "shippingStatus":
                    order.setShippingStatus(newStatus); // 更新出貨狀態
                    break;
                default:
                    throw new IllegalArgumentException("無效的字段名稱: " + field);
            }
            
            // 保存更新後的訂單
            return groupOrdersRepository.save(order);
        } else {
            throw new RuntimeException("訂單不存在");
        }
    }
    
    // 查詢全部團購訂單
    public List<GroupOrdersVO> findAll() {
        return groupOrdersRepository.findAll();
    }
    
    //查詢會員參加的所有團購訂單（通過訂單的參與者關聯）
    public List<GroupOrdersVO> getOrdersByMemberParticipation(Integer memId) {
        return groupOrdersRepository.findOrdersByMemberParticipation(memId);
    }
    
    // 根據參與者ID查詢團購訂單
    public List<GroupOrdersVO> getOrdersByParId(Integer parId) {
        return groupOrdersRepository.findOrdersByParId(parId);
    }
    

}