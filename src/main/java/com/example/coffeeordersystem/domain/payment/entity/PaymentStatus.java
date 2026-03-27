package com.example.coffeeordersystem.domain.payment.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCESS("결제 성공"),
    FAILED("결제 실패");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
