package com.example.coffeeordersystem.domain.user.entity;

import com.example.coffeeordersystem.global.common.entity.BaseEntity;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
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

    public void charge(BigDecimal amount) {
        validateAmount(amount);
        this.point = this.point.add(amount);
    }

    public void use(BigDecimal amount) {
        validateAmount(amount);

        if (this.point.compareTo(amount) < 0) {
            throw new ServiceException(ErrorCode.INSUFFICIENT_POINT);
        }

        this.point = this.point.subtract(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }
    }
}
