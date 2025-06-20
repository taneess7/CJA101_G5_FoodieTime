package com.foodietime.groupbuyingcases.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupBuyingCasesService {

    @Autowired
    private GroupBuyingCasesRepository groupBuyingCasesRepository;

    // 查詢某會員開的所有團購案
    public List<GroupBuyingCasesVO> findByMemId(Integer memId) {
        return groupBuyingCasesRepository.findByMember_MemId(memId);
    }
    
    // 查詢某團主開設的團購案
    public List<GroupBuyingCasesVO> findByMember_MemIdAndLeader(Integer memberId, Boolean leader) {
        return groupBuyingCasesRepository.findByMember_MemIdAndLeader(memberId, leader);
    }
    
//    // 查詢某店家開的所有團購案
//    public List<GroupBuyingCasesVO> findByStoreId(Integer storId) {
//        return groupBuyingCasesRepository.findByStore_StorId(storId);
//    }

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
}
