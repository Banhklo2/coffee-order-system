package com.example.coffeeordersystem.domain.user.dto.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UserChargeRequest {

    private BigDecimal amount;
}
