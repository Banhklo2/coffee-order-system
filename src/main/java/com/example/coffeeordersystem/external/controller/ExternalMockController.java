package com.example.coffeeordersystem.external.controller;

import com.example.coffeeordersystem.external.dto.ExternalOrderRequest;
import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/orders")
@Slf4j
public class ExternalMockController {

    // 외부 플랫폼 주문 데이터 수신 (Mock API)
    // - 주문 완료 후 전송된 데이터 확인용
    @PostMapping
    public ApiResponse<Void> receiveOrder(@RequestBody ExternalOrderRequest request) {
        log.info("외부 플랫폼 주문 데이터 수신 - userId={}, menuId={}, amount={}",
                request.getUserId(), request.getMenuId(), request.getAmount());

        return ApiResponse.noContent();
    }
}
