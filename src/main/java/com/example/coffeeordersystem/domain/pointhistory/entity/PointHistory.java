package com.example.coffeeordersystem.domain.pointhistory.entity;

import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@Builder
@Table(name = "point_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PointHistoryStatus status;

    public static PointHistory charge(User user, BigDecimal amount) {
        return PointHistory.builder()
                .user(user)
                .amount(amount)
                .status(PointHistoryStatus.CHARGE)
                .build();
    }

    public static PointHistory use(User user, BigDecimal amount) {
        return PointHistory.builder()
                .user(user)
                .amount(amount.negate())
                .status(PointHistoryStatus.USE)
                .build();
    }
}
