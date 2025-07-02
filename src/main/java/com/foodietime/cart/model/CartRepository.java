package com.foodietime.cart.model;

import com.foodietime.member.model.MemberVO;
import com.foodietime.product.model.ProductVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartVO, Integer> {
    // 查詢某會員的所有購物車商品
    List<CartVO> findByMember(MemberVO member);

    // 計算某會員購物車內商品數量
    Integer countByMember(MemberVO member);

    // 查詢某會員某商品是否已存在於購物車
    CartVO findByMemberAndProduct(MemberVO member, ProductVO product);

    // 查詢某會員的某特定購物車編號，確保存取權限
    Optional<CartVO> findByShopIdAndMember(Integer shopId, MemberVO member);

    @Query("SELECT c FROM CartVO c JOIN FETCH c.product p JOIN FETCH p.store WHERE c.member = :member")
    List<CartVO> findAllByMemberIdWithDetails(@Param("member") MemberVO member);

    @Query("SELECT c FROM CartVO c JOIN FETCH c.product p JOIN FETCH p.store s WHERE c.shopId IN :shopIds")
    List<CartVO> findAllByIdsWithDetails(@Param("shopIds") List<Integer> shopIds);

    /**
     * 【新增】根據會員和一個商品列表，刪除所有匹配的購物車記錄。
     */
    @Modifying
    @Query("DELETE FROM CartVO c WHERE c.member = :member AND c.product IN :products")
    void deleteByMemberAndProducts(@Param("member") MemberVO member, @Param("products") List<ProductVO> products);

}
