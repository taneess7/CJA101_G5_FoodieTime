package com.foodietime.gbprod.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.foodietime.gbprodcg.model.GbprodcgVO;
import com.foodietime.gbpromotion.model.GbpromotionVO;
import com.foodietime.groupbuyingcases.model.GroupBuyingCasesVO;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.store.model.StoreVO;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"groupOrdersList","gbpromotionList","groupbuyingcasesList"})
@Table(name = "group_products")
public class GbprodVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GB_PROD_ID")
    @EqualsAndHashCode.Include
    private Integer gbProdId;
    
  
    @OneToMany(mappedBy = "gbprod", cascade = CascadeType.ALL)
    private List<GroupOrdersVO> groupOrdersList = new ArrayList<>();

    @OneToMany(mappedBy = "gbprod", cascade = CascadeType.ALL)
    private List<GbpromotionVO> gbpromotionList = new ArrayList<>();
    
    @OneToMany(mappedBy = "gbProd", cascade = CascadeType.ALL)
    private List<GroupBuyingCasesVO> groupbuyingcasesList = new ArrayList<>();
    
    
    @ManyToOne
    @JoinColumn(name = "STOR_ID")
    private StoreVO store;

    @NotNull(message = "團購分類不能為空")
    @ManyToOne
    @JoinColumn(name = "GB_CATE_ID")
    private GbprodcgVO gbprodcgVO;

    @NotBlank(message = "商品名稱不能為空")
    @Size(max = 45, message = "商品名稱長度不能超過 45 字")
    @Column(name = "GB_PROD_NAME", length = 45)
    private String gbProdName;

    @Size(max = 45, message = "商品描述長度不能超過 45 字")
    @Column(name = "GB_PROD_DESCRIPTION", length = 45)
    private String gbProdDescription;

    @NotNull(message = "商品數量不能為空")
    @Min(value = 0, message = "商品數量不能小於 0")
    @Column(name = "GB_PROD_QUANTITY")
    private Integer gbProdQuantity;

    @NotNull(message = "商品狀態不能為空")
    @Column(name = "GB_PROD_STATUS")
    private Byte gbProdStatus;

    @PastOrPresent(message = "更新時間不能是未來")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_AT")
    private Date updateAt;

    @Size(max = 45, message = "商品規格長度不能超過 45 字")
    @Column(name = "GB_PROD_SPECIFICATION", length = 45)
    private String gbProdSpecification;

    @Lob
    @Column(name = "PROD_PHOTO")
    private Byte[] gbProdPhoto;

    @Size(max = 45, message = "評價長度不能超過 45 字")
    @Column(name = "GB_PROD_EVALUATE", length = 45)
    private String gbProdEvaluate;

    @Min(value = 0, message = "檢舉次數不能小於 0")
    @Column(name = "GB_PROD_REPORT_COUNT")
    private Byte gbProdReportCount;
     
    public byte[] getGbProdPhotoBytes() {
        if (gbProdPhoto == null) return null;
        byte[] bytes = new byte[gbProdPhoto.length];
        for (int i = 0; i < gbProdPhoto.length; i++) {
            bytes[i] = gbProdPhoto[i];
        }
        return bytes;
    }
    
    public String getImgUrl(){
    	return "/gb/gbproduct/image/" + gbProdId;
    }
  
    @Override
    public String toString() {
        return "GbprodVO{" +
                "gbProdId=" + gbProdId +
                ", gbProdName='" + gbProdName + '\'' +
                ", gbprodcgVO=" + (gbprodcgVO != null ? gbprodcgVO.getGbCateId() : null) +
                // 其它欄位可視需要加
                '}';
    }
}
