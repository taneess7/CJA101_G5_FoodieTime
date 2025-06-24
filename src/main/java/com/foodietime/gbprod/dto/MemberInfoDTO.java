package com.foodietime.gbprod.dto;

import com.foodietime.member.model.MemberVO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoDTO {
    private String memName;
    private String memPhone;
    private String memCity;
    private String memCityarea;
    private String memAddress;

    public MemberInfoDTO(MemberVO member) {
        this.memName = member.getMemName();
        this.memPhone = member.getMemPhone();
        this.memCity = member.getMemCity();
        this.memCityarea = member.getMemCityarea();
        this.memAddress = member.getMemAddress();
    }
} 