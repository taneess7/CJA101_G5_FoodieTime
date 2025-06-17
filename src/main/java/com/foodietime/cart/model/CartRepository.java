package com.foodietime.cart.model;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartVO, Integer> {
    // 查詢某會員的所有購物車商品
    List<CartVO> findByMember(MemberVO member);

    // 查詢某會員某商品是否已存在於購物車
    CartVO findByMemberAndProduct(MemberVO member, ProductVO product);

    // 查詢某會員的某特定購物車編號，確保存取權限
    Optional<CartVO> findByShopIdAndMember(Integer shopId, MemberVO member);
}
