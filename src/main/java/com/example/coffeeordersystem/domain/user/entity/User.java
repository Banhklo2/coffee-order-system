package com.example.coffeeordersystem.domain.user.entity;

import com.example.coffeeordersystem.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal point;

    public static User create() {
        return User.builder()
                .point(BigDecimal.ZERO)
                .build();
    }

    public void charge(BigDecimal amount) {
        this.point = this.point.add(amount);
    }

    public void use(BigDecimal amount) {
        this.point = this.point.subtract(amount);
    }
}
