package com.foodietime.smgfc.model;

import jakarta.persistence.*;
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

    @Column(name = "SMGEFUNC", length = 45)
    private String smgFunc;

}
