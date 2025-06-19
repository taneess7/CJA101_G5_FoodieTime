package com.foodietime.directmessage.model;

import lombok.Data;
import java.util.List;

@Data
public class BatchMessageRequestDTO {
    private List<Integer> memIds;
    private String messageContent;
}
