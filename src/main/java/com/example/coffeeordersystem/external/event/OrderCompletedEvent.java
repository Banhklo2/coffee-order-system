package com.example.coffeeordersystem.external.event;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderCompletedEvent {

    private Long userId;
    private Long menuId;
    private BigDecimal amount;
}
