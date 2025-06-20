package com.foodietime.cart.model;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
import com.foodietime.store.model.StoreVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.util.Objects;

// ==================== 1. 移除 @Data，使用更精確的 Lombok 註解 ====================
@Getter
@Setter
@Entity

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

    // ==================== 2. 手動實作 equals 和 hashCode ====================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartVO cartVO = (CartVO) o;
        // 關鍵：只比較主鍵 (ID)，並且只有當 ID 不是 null 時才比較
        return shopId != null && Objects.equals(shopId, cartVO.shopId);
    }

    @Override
    public int hashCode() {
        // 關鍵：返回一個固定的值，這個值對於同一個類的所有實例都是一樣的。
        // 這可以確保在物件被持久化前後（ID從null變為有值），雜湊碼保持不變。
        // 這避免了在 HashMap 或 HashSet 中找不到物件的問題。
        return getClass().hashCode();
    }
}
