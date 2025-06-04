package com.foodietime.cart.model;

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

    @Column(name = "mem_id", nullable = false)
    @NotNull(message="不可為空")
    private Integer memId;    // 會員編號

    @Column(name = "prod_id", nullable = false)
    @NotNull(message="不可為空")
    private Integer prodId;   // 商品編號

    @Column(name = "prod_n", nullable = false)
    @NotNull(message="不可為空")
    @Min(value=1, message="最小1")
    @Max(value=99, message="最大99")
    private Integer prodN;    // 商品數量

}
