package com.foodietime.orders.dto;

import java.sql.Timestamp;

// 使用 Record 語法可以更簡潔地建立不可變的 DTO
public record OrderNotificationDTO(
        Integer ordId,
        String memName,
        Timestamp ordDate,
        Integer actualPayment,
        String orderStatusText
) {
    // Record 會自動產生建構子、getter、equals()、hashCode() 和 toString()
}
