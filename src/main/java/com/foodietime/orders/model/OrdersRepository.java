package com.foodietime.orders.model;

import com.foodietime.member.model.MemberVO;
import com.foodietime.store.model.StoreVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersVO, Integer> {
    List<OrdersVO> findByMember(MemberVO member);
    List<OrdersVO> findByStore(StoreVO store);
}
