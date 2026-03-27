package com.example.coffeeordersystem.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    WAITING("주문 확인"),
    PREPARING("조리 중"),
    COMPLETED( "조리 완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
