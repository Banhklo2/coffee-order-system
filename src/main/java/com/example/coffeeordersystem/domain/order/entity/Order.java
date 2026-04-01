package com.example.coffeeordersystem.domain.order.entity;

import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "orders", indexes = {@Index(name = "idx_order_created_at", columnList = "created_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderedAt;

    // 주문 생성 = 결제 완료 상태
    public static Order create(User user, BigDecimal totalPrice) {
        return Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .status(OrderStatus.COMPLETED)
                .orderedAt(LocalDateTime.now())
                .build();
    }
}
