package com.example.coffeeordersystem.domain.order.repository;

import com.example.coffeeordersystem.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
