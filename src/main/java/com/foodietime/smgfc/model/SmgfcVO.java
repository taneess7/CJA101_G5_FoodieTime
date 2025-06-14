package com.foodietime.smgfc.model;

import java.io.Serializable;
import java.util.List;

import com.foodietime.smgauth.model.SmgauthVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"smgauthList"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "servermanagefunction")
public class SmgfcVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGEFUNC_ID")
    @EqualsAndHashCode.Include
    private Integer smgFuncId;

    @NotBlank(message = "功能名稱不可為空")
    @Size(max = 45, message = "功能名稱長度不可超過45個字元")
    @Column(name = "SMGEFUNC", length = 45)
    private String smgFunc;
    
    @OneToMany(mappedBy = "smgfc", fetch = FetchType.LAZY)
    private List<SmgauthVO> smgauthList;
}
