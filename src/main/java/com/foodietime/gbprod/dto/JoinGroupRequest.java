package com.foodietime.gbprod.dto;

import lombok.Data;

@Data
public class JoinGroupRequest {
    private Integer gbId;
    private Integer quantity;
    private Integer amount;
    private String parName;
    private String parAddress;
    private Double parLongitude;
    private Double parLatitude;
    private String parPhone;
    private Byte deliveryMethod;
    private Byte payMethod;
} 