package com.example.coffeeordersystem.domain.payment.entity;

import com.example.coffeeordersystem.domain.order.entity.Order;
import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    public static Payment success(Order order, User user, BigDecimal amount) {
        return Payment.builder()
                .order(order)
                .user(user)
                .amount(amount)
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .build();
    }

    public static Payment fail(Order order, User user, BigDecimal amount) {
        return Payment.builder()
                .order(order)
                .user(user)
                .amount(amount)
                .status(PaymentStatus.FAILED)
                .paidAt(LocalDateTime.now())
                .build();
    }
}
