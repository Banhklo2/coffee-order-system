package com.example.coffeeordersystem.domain.order.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCreateRequest {

    private Long userId;
    private Long menuId;
}
