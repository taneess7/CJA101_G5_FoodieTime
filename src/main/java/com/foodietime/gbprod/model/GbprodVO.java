package com.foodietime.gbprod.model;

import java.io.Serializable;
import java.util.Date;

import com.foodietime.gbprodcg.model.GbprodcgVO;
import com.foodietime.gbpromotion.model.GbpromotionVO;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table(name = "group_products")
public class GbprodVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GB_PROD_ID")
    private Integer gbProdId;

    @ManyToOne
    @JoinColumn(name = "STOR_ID")
    private StoreVO storeVO;  // 原本是 Integer storId


    @ManyToOne
    @JoinColumn(name = "GB_CATE_ID")
    private GbprodcgVO gbprodcgVO;

    @Column(name = "GB_PROD_NAME", length = 45)
    private String gbProdName;

    @Column(name = "GB_PROD_DESCRIPTION", length = 45)
    private String gbProdDescription;

    @Column(name = "GB_PROD_QUANTITY")
    private Integer gbProdQuantity;

    @Column(name = "GB_PROD_STATUS")
    private Byte gbProdStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_AT")
    private Date updateAt;

    @Column(name = "GB_PROD_SPECIFICATION", length = 45)
    private String gbProdSpecification;

    @Lob
    @Column(name = "PROD_PHOTO")
    private Byte[] gbProdPhoto;

    @Column(name = "GB_PROD_EVALUATE", length = 45)
    private String gbProdEvaluate;

    @Column(name = "GB_PROD_REPORT_COUNT")
    private Byte gbProdReportCount;

    @OneToOne(mappedBy = "gbprodVO", cascade = CascadeType.ALL)
    private GbpromotionVO gbpromotionVO;

   
}
