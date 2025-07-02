package com.foodietime.orddet.dto;

import lombok.Data;
// 給後台用的
@Data
public class OrderDetailItemDTO {
    private String prodName;
    private String ordDesc; // 規格/備註
    private Integer prodPrice;
    private Integer prodQty;
    private Integer subtotal;
}
