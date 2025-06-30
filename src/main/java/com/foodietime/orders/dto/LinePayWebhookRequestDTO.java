package com.foodietime.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 用於接收 LINE Pay Webhook 確認請求的 DTO。
 * 結構根據 LINE Pay API 文件定義。
 */
@Data
public class LinePayWebhookRequestDTO {

    /**
     * 回應代碼。 "0000" 代表成功。
     */
    @JsonProperty("returnCode")
    private String returnCode;

    /**
     * 回應訊息或錯誤原因。
     */
    @JsonProperty("returnMessage")
    private String returnMessage;

    /**
     * 包含交易詳細資訊的物件。
     */
    @JsonProperty("info")
    private Info info;

    @Data
    public static class Info {
        /**
         * 交易編號，對應我們發起請求時的訂單 ID (orderId)。
         */
        @JsonProperty("transactionId")
        private String transactionId;

        /**
         * 支付資訊列表。
         */
        @JsonProperty("payInfo")
        private List<PayInfo> payInfo;
    }

    @Data
    public static class PayInfo {
        /**
         * 支付方式 (e.g., CREDIT_CARD, BALANCE)。
         */
        @JsonProperty("method")
        private String method;

        /**
         * 支付金額。
         */
        @JsonProperty("amount")
        private Long amount;
    }
}