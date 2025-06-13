package com.foodietime.cart.model;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "shopping_cart")
public class CartVO implements Serializable {
    //寫上所有欄位
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Integer shopId;   // 購物車商品編號

    @ManyToOne
    @JoinColumn(name = "mem_id",referencedColumnName = "mem_id")
    @NotNull(message="不可為空")
    private MemberVO member;    // 會員編號

    @ManyToOne
    @JoinColumn(name = "prod_id",referencedColumnName = "prod_id")
    @NotNull(message="不可為空")
    private ProductVO product;   // 商品編號

    @Column(name = "prod_n", nullable = false)
    @NotNull(message="不可為空")
    @Min(value=1, message="最小1")
    @Max(value=99, message="最大99")
    private Integer prodN;    // 商品數量

}
