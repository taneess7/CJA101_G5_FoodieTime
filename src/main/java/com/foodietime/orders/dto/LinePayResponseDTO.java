package com.foodietime.orders.dto;

import lombok.Data;

@Data
public class LinePayResponseDTO {
    private String returnCode;
    private String returnMessage;
    private Info info;

    @Data
    public static class Info {
        // 將 paymentUrl 的型別從 String 改為下面定義的 PaymentUrl 物件
        private PaymentUrl paymentUrl;
        private Long transactionId;
        private String paymentAccessToken;
    }

    //  內部類別來對應 paymentUrl 的真實結構
    @Data
    public static class PaymentUrl {
        private String web; // 對應 "web" 這個 key
        private String app; // 對應 "app" 這個 key
    }
}
