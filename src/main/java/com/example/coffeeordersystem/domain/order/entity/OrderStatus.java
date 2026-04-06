package com.example.coffeeordersystem.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    COMPLETED("주문 완료"),
    FAILED("주문 실패");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
