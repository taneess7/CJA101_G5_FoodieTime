package com.foodietime.cart.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartVO, Integer> {
    // 查詢某會員的所有購物車商品
    List<CartVO> findByMemId(Integer memId);

    // 查詢某會員某商品是否已存在於購物車
    CartVO findByMemIdAndProdId(Integer memId, Integer prodId);

}
