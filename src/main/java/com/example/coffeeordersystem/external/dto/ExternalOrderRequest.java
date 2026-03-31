package com.example.coffeeordersystem.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalOrderRequest {

    private Long userId;
    private Long menuId;
    private BigDecimal amount;
}
