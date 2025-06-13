package com.foodietime.gbpromotion.model;

import java.io.Serializable;
import java.util.Date;

import com.foodietime.gbprod.model.GbprodVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Table(name = "gb_promotion")
public class GbpromotionVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GB_PROMO_ID")
    private Integer gbPromoId;

    @NotNull(message = "關聯的商品不可為空")
    @ManyToOne
    @JoinColumn(name = "GB_PROD_ID", nullable = false) // 這是外鍵
    private GbprodVO gbprod;

    @NotNull(message = "最小成團數不可為空")
    @Min(value = 1, message = "最小成團數必須 ≥ 1")
    @Column(name = "GB_MIN_QTY")
    private Integer gbMinQty;

    @NotNull(message = "開始時間不可為空")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PROMT_START")
    private Date promotStart;

    @NotNull(message = "結束時間不可為空")
    @Future(message = "結束時間必須為未來時間")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PROMT_END")
    private Date promotEnd;

    @Min(value = 0, message = "銷售數量不可為負")
    @Column(name = "GB_PROD_SALES")
    private Integer gbProdSales;

    @Min(value = 0, message = "優惠價格不可為負")
    @Column(name = "GB_PROD_SPE")
    private Integer gbProdSpe;
}
