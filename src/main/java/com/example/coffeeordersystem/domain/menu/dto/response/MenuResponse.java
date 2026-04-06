package com.example.coffeeordersystem.domain.menu.dto.response;

import com.example.coffeeordersystem.domain.menu.entity.Menu;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MenuResponse {

    private Long menuId;
    private String name;
    private BigDecimal price;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .build();
    }
}
