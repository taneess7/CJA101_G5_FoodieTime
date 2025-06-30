package com.foodietime.orders.dto;

import com.foodietime.orddet.dto.OrderDetailDTO;

import java.sql.Timestamp;
import java.util.List;

public record NewOrderNotificationDTO(

        Integer ordId,
        Timestamp ordDate,
        Integer actualPayment,
        Integer orderStatus, // 傳遞數字狀態碼，方便 JS 做邏輯判斷
        String orderStatusText, // 傳遞文字，方便 JS 直接顯示
        String comment, // 訂單層級的備註

        // === 顧客資訊 ===
        String memName,
        String memPhone,
        String ordAddr, // 外送地址，可能為 null

        // === 訂單項目列表 ===
        List<OrderDetailDTO> items
) {}
