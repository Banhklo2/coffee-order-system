package com.example.coffeeordersystem.domain.order.dto.request;

import lombok.Getter;

@Getter
public class OrderCreateRequest {

    private Long userId;
    private Long menuId;
}
