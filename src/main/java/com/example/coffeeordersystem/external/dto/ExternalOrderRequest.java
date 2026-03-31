package com.example.coffeeordersystem.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalOrderRequest {

    private Long userId;
    private Long menuId;
    private BigDecimal amount;
}
