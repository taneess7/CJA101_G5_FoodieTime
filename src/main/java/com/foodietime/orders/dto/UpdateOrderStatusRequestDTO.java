package com.foodietime.orders.dto;

import lombok.Data;
// 給後台用的
@Data
public class UpdateOrderStatusRequestDTO {
    private Integer ordId;
    private Integer orderStatus;
    private Integer paymentStatus;
    private String cancelReason;
}
