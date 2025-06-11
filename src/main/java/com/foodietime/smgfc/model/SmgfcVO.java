package com.foodietime.smgfc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;

@Entity
@Data
@Table(name = "servermanagefunction")
public class SmgfcVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMGEFUNC_ID")
    private Integer smgFuncId;

    @NotBlank(message = "功能名稱不可為空")
    @Size(max = 45, message = "功能名稱長度不可超過45個字元")
    @Column(name = "SMGEFUNC", length = 45)
    private String smgFunc;
}
