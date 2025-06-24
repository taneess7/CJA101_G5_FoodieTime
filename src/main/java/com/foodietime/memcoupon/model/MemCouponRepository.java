package com.foodietime.memcoupon.model;

import com.foodietime.member.model.MemberVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemCouponRepository extends JpaRepository<MemCouponVO, Integer> {

    @Query("SELECT m FROM MemCouponVO m JOIN FETCH m.coupon c LEFT JOIN FETCH c.store s WHERE m.member = :member")
    List<MemCouponVO> findByMemberIdWithDetails(@Param("member") MemberVO member);
}

