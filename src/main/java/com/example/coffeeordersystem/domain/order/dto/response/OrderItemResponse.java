package com.example.coffeeordersystem.domain.order.dto.response;

import com.example.coffeeordersystem.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItemResponse {

    private Long orderItemId;
    private Long menuId;
    private String name;
    private BigDecimal price;

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .menuId(orderItem.getMenu().getId())
                .name(orderItem.getName())
                .price(orderItem.getPrice())
                .build();
    }
}
