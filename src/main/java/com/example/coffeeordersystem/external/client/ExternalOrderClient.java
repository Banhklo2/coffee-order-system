package com.example.coffeeordersystem.external.client;

import com.example.coffeeordersystem.external.dto.ExternalOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalOrderClient {

    private final RestTemplate restTemplate;

    public void sendOrder(ExternalOrderRequest request) {
        try {
            log.info("ExternalOrderClient 진입");
            restTemplate.postForEntity(
                    "http://localhost:8080/external/orders",
                    request,
                    Void.class
            );

            log.info("외부 플랫폼 전송 완료 - userId={}, menuId={}, amount={}",
                    request.getUserId(), request.getMenuId(), request.getAmount());
        } catch (Exception e) {
            log.error("외부 플랫폼 전송 실패", e);
            throw e;
        }
    }
}
