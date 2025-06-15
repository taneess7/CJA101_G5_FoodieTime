package com.foodietime.cart.model;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartVO, Integer> {
    // 查詢某會員的所有購物車商品
    List<CartVO> findByMember(MemberVO member);

    // 查詢某會員某商品是否已存在於購物車
    CartVO findByMemberAndProduct(MemberVO member, ProductVO product);

}
