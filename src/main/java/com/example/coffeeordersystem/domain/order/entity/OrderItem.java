package com.example.coffeeordersystem.domain.order.entity;

import com.example.coffeeordersystem.domain.menu.entity.Menu;
import com.example.coffeeordersystem.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@Builder
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private String name;
    private BigDecimal price;

    public static OrderItem create(Order order, Menu menu) {
        return OrderItem.builder()
                .order(order)
                .menu(menu)
                .name(menu.getName())
                .price(menu.getPrice())
                .build();
    }
}
