package com.foodietime.gbprod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodietime.gbprod.model.GbprodRepository;
import com.foodietime.gbprod.model.GbprodVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesRepository;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.participants.model.ParticipantsRepository;
import com.foodietime.participants.model.ParticipantsVO;

@Service
public class GbleaderService {
	
	@Autowired
    private GbprodRepository gbprodRepository;

    @Autowired
    private GroupBuyingCasesRepository groupBuyingCasesRepository;

    @Autowired
    private ParticipantsRepository participantsRepository;

    /**
     * 獲取指定店家及其促銷價格
     */
    public List<GbprodVO> getStoresWithPromotionPrices() {
        // 調用 repository 查詢店家及其促銷價格
        return gbprodRepository.findStoresWithOrdersByStatus();
    }

    public List<GbprodVO> searchProducts(String keyword) {
        return gbprodRepository.searchByNameOrProdIdOrStoreId(keyword);
    }

    public void createGroupBuyingCase(Integer gbProdId, String gbTitle, String gbDescription, Integer gbMinQty, java.time.LocalDateTime endTime, MemberVO member) {
        GroupBuyingCasesVO caseVO = new GroupBuyingCasesVO();
        caseVO.setGbTitle(gbTitle);
        caseVO.setGbDescription(gbDescription);
        caseVO.setGbMinProductQuantity(gbMinQty);
        caseVO.setGbStartTime(java.time.LocalDateTime.now());
        caseVO.setGbEndTime(endTime);

        GbprodVO prod = gbprodRepository.findById(gbProdId).orElse(null);
        caseVO.setGbProd(prod);
        if (prod != null) {
            caseVO.setStore(prod.getStore());
        }
        caseVO.setMember(member);
        caseVO.setGbStatus((byte)1); // 1: 招募中
        caseVO.setGbCreateAt(java.time.LocalDateTime.now());
        caseVO.setCumulativePurchaseQuantity(0);
        groupBuyingCasesRepository.save(caseVO);

     // 新增團主參與者，補齊所有必填欄位
        ParticipantsVO leaderParticipant = new ParticipantsVO();
        leaderParticipant.setGroupBuyingCase(caseVO);
        leaderParticipant.setMember(member);
        leaderParticipant.setLeader((byte) 0); // 團主
        leaderParticipant.setPaymentStatus((byte) 0); // 未付款
        leaderParticipant.setParLatitude(java.math.BigDecimal.ZERO); // 預設緯度
        leaderParticipant.setParLongitude(java.math.BigDecimal.ZERO); // 預設經度
        leaderParticipant.setParPurchaseQuantity(0); // 預設購買數量為0
        leaderParticipant.setParPhone(member.getMemPhone()); // 會員手機
        leaderParticipant.setParName(member.getMemName()); // 會員姓名
        leaderParticipant.setParAddress(member.getMemCity() + member.getMemCityarea() + member.getMemAddress()); // 會員完整地址
        participantsRepository.save(leaderParticipant);
    }
}