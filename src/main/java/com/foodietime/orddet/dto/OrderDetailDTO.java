package com.foodietime.orddet.dto;

public record OrderDetailDTO(
        String productName,
        Integer quantity,
        Integer price,
        String note // 商品備註
) {}
