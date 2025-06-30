package com.foodietime.orders.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder // 使用 Builder 模式方便建立物件
public class LinePayRequestDTO {
    private Long amount;
    private String currency;
    private String orderId;
    private List<Package> packages;
    private RedirectUrls redirectUrls;

    @Data
    @Builder
    public static class Package {
        private String id;
        private Long amount;
        private List<Product> products;
    }

    @Data
    @Builder
    public static class Product {
        private String name;
        private Integer quantity;
        private Long price;
    }

    @Data
    @Builder
    public static class RedirectUrls {
        private String confirmUrl;
        private String cancelUrl;
    }
}