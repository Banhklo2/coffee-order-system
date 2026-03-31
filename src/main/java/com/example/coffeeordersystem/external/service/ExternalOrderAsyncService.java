package com.example.coffeeordersystem.external.service;

import com.example.coffeeordersystem.external.client.ExternalOrderClient;
import com.example.coffeeordersystem.external.dto.ExternalOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalOrderAsyncService {

    private final ExternalOrderClient externalOrderClient;

    @Async
    public void sendOrderAsync(ExternalOrderRequest request) {
        try {
            log.info("비동기 외부 플랫폼 전송 시작 - thread={}", Thread.currentThread().getName());

            externalOrderClient.sendOrder(request);

            log.info("비동기 외부 플랫폼 전송 완료");
        } catch (Exception e) {
            log.error("비동기 외부 플랫폼 전송 실패", e);
        }
    }
}
