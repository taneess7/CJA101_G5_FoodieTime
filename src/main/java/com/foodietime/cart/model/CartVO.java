package com.foodietime.cart.model;

import jakarta.persistence.*;
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
    private Integer memId;    // 會員編號

    @Column(name = "prod_id", nullable = false)
//    @ManyToOne
    private Integer prodId;   // 商品編號
    @Column(name = "prod_n", nullable = false)
    private Integer prodN;    // 商品數量


//    //取得or設置_購物車商品編號
//    public Integer getShopId() {
//        return shopId;
//    }
//    public void setShopId(Integer shopId) {
//        this.shopId = shopId;
//    }
//
//    //取得or設置_會員編號
//    public Integer getMemId() {
//        return memId;
//    }
//    public void setMemId(Integer memId) {
//        this.memId = memId;
//    }
//
//    //取得or設置商品編號
//    public Integer getProdId() {
//        return prodId;
//    }
//    public void setProdId(Integer prodId) {
//        this.prodId = prodId;
//    }
//
//    //取得or設置商品數量
//    public Integer getProdN() {
//        return prodN;
//    }
//    public void setProdN(Integer prodN) {
//        this.prodN = prodN;
//    }
}
