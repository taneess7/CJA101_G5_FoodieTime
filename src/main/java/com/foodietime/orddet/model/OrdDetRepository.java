package com.foodietime.orddet.model;

import com.foodietime.orders.model.OrdersVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdDetRepository extends JpaRepository<OrdDetVO, Integer> {
    List<OrdDetVO> findByOrders(OrdersVO orders);
}
