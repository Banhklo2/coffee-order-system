package com.example.coffeeordersystem.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotNull(message = "menuId는 필수입니다.")
    private Long menuId;
}
