package com.foodietime.groupbuyingcases.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupBuyingCasesService {

    @Autowired
    private GroupBuyingCasesRepository groupBuyingCasesRepository;

    // 查詢某會員參加的所有團購案
    public List<GroupBuyingCasesVO> findByMemId(Integer memId) {
        return groupBuyingCasesRepository.findByMember_MemId(memId);
    }
    
    // 查詢某會員開設且是團主的團購案
    public List<GroupBuyingCasesVO> findByMember_MemIdAndLeader(Integer memberId, Boolean leader) {
        return groupBuyingCasesRepository.findByMember_MemIdAndLeader(memberId, leader);
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

//    // 刪除某個團購案
//    public void deleteGroupBuyingCase(Integer gbId) {
//        groupBuyingCasesRepository.deleteById(gbId);
//    }

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
    public List<GroupBuyingCasesVO> findByGbTitleContainingAndGbStatus(String keyword, Byte gbStatus) {
        return groupBuyingCasesRepository.findByGbTitleContainingAndGbStatus(keyword, gbStatus);
    }
}
