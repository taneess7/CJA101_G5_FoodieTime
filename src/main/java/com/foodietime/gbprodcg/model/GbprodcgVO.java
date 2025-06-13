package com.foodietime.gbprodcg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.foodietime.gbprod.model.GbprodVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Table(name = "gb_prod_category")
public class GbprodcgVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GB_CATE_ID")
    private Integer gbCateId;

    @NotBlank(message = "分類名稱不能為空")
    @Size(max = 45, message = "分類名稱長度不能超過 45 字")
    @Column(name = "GB_CATE_NAME", length = 45)
    private String gbCateName;

    // 一對多關聯：一個分類對多個商品
    @OneToMany(mappedBy = "gbprodcgVO", cascade = CascadeType.ALL)
    private List<GbprodVO> gbprods = new ArrayList<>();
}
