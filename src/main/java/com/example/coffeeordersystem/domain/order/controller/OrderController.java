package com.example.coffeeordersystem.domain.order.controller;

import com.example.coffeeordersystem.domain.order.dto.request.OrderCreateRequest;
import com.example.coffeeordersystem.domain.order.dto.response.OrderItemResponse;
import com.example.coffeeordersystem.domain.order.dto.response.OrderResponse;
import com.example.coffeeordersystem.domain.order.service.OrderItemService;
import com.example.coffeeordersystem.domain.order.service.OrderService;
import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    // 주문 생성 + 결제
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        return ApiResponse.created(orderService.createOrder(request));
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.getOrder(orderId));
    }

    // 주문 상품 조회
    @GetMapping("/{orderId}/items")
    public ApiResponse<List<OrderItemResponse>> getOrderItems(@PathVariable Long orderId) {
        return ApiResponse.ok(orderItemService.getOrderItems(orderId));
    }
}
