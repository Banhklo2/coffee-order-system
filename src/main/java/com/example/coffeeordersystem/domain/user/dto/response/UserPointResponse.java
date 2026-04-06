package com.example.coffeeordersystem.domain.user.dto.response;

import com.example.coffeeordersystem.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UserPointResponse {

    private Long userId;
    private BigDecimal point;

    public static UserPointResponse from(User user) {
        return UserPointResponse.builder()
                .userId(user.getId())
                .point(user.getPoint())
                .build();
    }
}
