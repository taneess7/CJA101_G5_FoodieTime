package com.foodietime.cart.dto;

import com.foodietime.product.model.ProductVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private ProductVO product; // 商品完整資訊
    private Integer quantity;  // 購買數量

}
