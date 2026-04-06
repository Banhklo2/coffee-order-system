package com.example.coffeeordersystem.domain.order.service;

import com.example.coffeeordersystem.domain.order.dto.response.OrderItemResponse;
import com.example.coffeeordersystem.domain.order.entity.Order;
import com.example.coffeeordersystem.domain.order.repository.OrderItemRepository;
import com.example.coffeeordersystem.domain.order.repository.OrderRepository;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문 상품 조회
    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderItems(Long orderId) {
        findOrder(orderId);

        return orderItemRepository.findAllByOrderId(orderId).stream()
                .map(OrderItemResponse::from)
                .toList();
    }

    // 주문 상품 조회 공통 메서드
    // - orderId로 조회 후 없으면 예외 발생
    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));
    }
}
