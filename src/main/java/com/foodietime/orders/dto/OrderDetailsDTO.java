package com.foodietime.orders.dto;

import com.foodietime.orddet.dto.OrderDetailItemDTO;
import lombok.Data;

import java.util.List;

// 給後台用的
@Data
public class OrderDetailsDTO {
    // 訂單基本資訊
    private Integer ordId;
    private Integer orderStatus;
    private Integer paymentStatus;
    private String ordDate;
    private Integer payMethod;
    private Integer deliver;

    // 會員與店家資訊
    private String memberName;
    private Integer memberId;
    private String storeName;
    private Integer storeId;

    // 金額資訊
    private Integer ordSum;
    private Integer promoDiscount;
    private Integer couponDiscount;
    private Integer actualPayment;

    // 其他資訊
    private String ordAddr;
    private Integer rating;
    private String comment;
    private String cancelReason;

    // 訂單明細列表
    private List<OrderDetailItemDTO> ordDet;
}