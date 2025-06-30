package com.foodietime.orders.dto;

import lombok.Data;

@Data
public class LinePayResponseDTO {
    private String returnCode;
    private String returnMessage;
    private Info info;

    @Data
    public static class Info {
        private String paymentUrl;
        private Long transactionId;
        private String paymentAccessToken;
    }
}
