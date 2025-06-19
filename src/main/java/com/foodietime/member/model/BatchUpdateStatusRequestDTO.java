package com.foodietime.member.model;

import lombok.Data;
import java.util.List;

@Data
public class BatchUpdateStatusRequestDTO {
    private List<Integer> memIds;
    private Integer targetStatus;
    private String reason;
}
