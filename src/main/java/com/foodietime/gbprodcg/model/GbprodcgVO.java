package com.foodietime.gbprodcg.model;

import java.io.Serializable;
import java.util.List;

import com.foodietime.gbprod.model.GbprodVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "gb_prod_category")
public class GbprodcgVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GB_CATE_ID")
    private Integer gbCateId;

    @Column(name = "GB_CATE_NAME", length = 45)
    private String gbCateName;

    // 一對多關聯：一個分類對多個商品
    @OneToMany(mappedBy = "gbprodcgVO", cascade = CascadeType.ALL)
    private List<GbprodVO> gbprods;

    
}
