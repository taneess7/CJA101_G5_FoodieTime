package com.foodietime.directmessage.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DirectMessageDTO {

    private Integer dmId;
    private Integer memId;
    private String memNickname;
    private String messContent;
    private LocalDateTime messTime;
    private Integer messDirection;
}
