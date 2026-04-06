package com.example.coffeeordersystem.domain.user.dto.response;

import com.example.coffeeordersystem.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private BigDecimal point;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .point(user.getPoint())
                .build();
    }
}
