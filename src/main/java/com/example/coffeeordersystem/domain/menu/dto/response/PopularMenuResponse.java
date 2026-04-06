package com.example.coffeeordersystem.domain.menu.dto.response;

import lombok.Getter;

@Getter
public class PopularMenuResponse {

    private Long menuId;
    private String name;
    private Long orderCount;

    public PopularMenuResponse(Long menuId, String name, Long orderCount) {
        this.menuId = menuId;
        this.name = name;
        this.orderCount = orderCount;
    }
}
