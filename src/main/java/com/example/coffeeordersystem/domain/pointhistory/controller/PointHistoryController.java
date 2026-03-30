package com.example.coffeeordersystem.domain.pointhistory.controller;

import com.example.coffeeordersystem.domain.pointhistory.dto.response.PointHistoryResponse;
import com.example.coffeeordersystem.domain.pointhistory.service.PointHistoryService;
import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;

    // 포인트 이력 조회
    @GetMapping("/history/{userId}")
    public ApiResponse<List<PointHistoryResponse>> getPointHistories(@PathVariable Long userId) {
        return ApiResponse.ok(pointHistoryService.getPointHistories(userId));
    }
}
