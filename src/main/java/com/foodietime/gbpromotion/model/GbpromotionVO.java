package com.foodietime.gbpromotion.model;

import java.io.Serializable;
import java.util.Date;

import com.foodietime.gbprod.model.GbprodVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table(name = "gb_promotion")
public class GbpromotionVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GB_PROMO_ID")
    private Integer gbPromoId;

    @OneToOne
    @JoinColumn(name = "GB_PROD_ID")
    private GbprodVO gbprodVO;

    @Column(name = "GB_MIN_QTY")
    private Integer gbMinQty;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PROMT_START")
    private Date promotStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PROMT_END")
    private Date promotEnd;

    @Column(name = "GB_PROD_SALES")
    private Integer gbProdSales;

    @Column(name = "GB_PROD_SPE")
    private Integer gbProdSpe;

}
