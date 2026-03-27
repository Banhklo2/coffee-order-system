package com.example.coffeeordersystem.domain.pointhistory.entity;

import lombok.Getter;

@Getter
public enum PointHistoryStatus {
    CHARGE("포인트 충전"),
    USE("포인트 사용");

    private final String description;

    PointHistoryStatus(String description) {
        this.description = description;
    }
}
