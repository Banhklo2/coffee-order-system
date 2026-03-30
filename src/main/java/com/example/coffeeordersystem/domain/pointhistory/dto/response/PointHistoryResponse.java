package com.example.coffeeordersystem.domain.pointhistory.dto.response;

import com.example.coffeeordersystem.domain.pointhistory.entity.PointHistory;
import com.example.coffeeordersystem.domain.pointhistory.entity.PointHistoryStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryResponse {

    private Long historyId;
    private BigDecimal amount;
    private PointHistoryStatus status;
    private LocalDateTime createdAt;

    public static PointHistoryResponse from(PointHistory history) {
        return PointHistoryResponse.builder()
                .historyId(history.getId())
                .amount(history.getAmount())
                .status(history.getStatus())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
