package com.foodietime.orders.dto;

public record OrderStatusUpdateDTO(
        Integer ordId,
        Integer orderStatus
) {}
