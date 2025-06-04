package com.foodietime.orders.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<OrdersVO, Integer> {
}
