package com.example.coffeeordersystem.external.event;

import com.example.coffeeordersystem.external.client.ExternalOrderClient;
import com.example.coffeeordersystem.external.dto.ExternalOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompletedEventListener {
    private final ExternalOrderClient externalOrderClient;

    // 주문 완료 이벤트 처리
    // - 외부 플랫폼으로 주문 데이터 전송
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCompletedEvent event) {
        try {
            log.info("주문 완료 이벤트 수신 - 비동기 외부 전송 시작");

            externalOrderClient.sendOrder(
                    ExternalOrderRequest.builder()
                            .userId(event.getUserId())
                            .menuId(event.getMenuId())
                            .amount(event.getAmount())
                            .build()
            );

            log.info("주문 완료 이벤트 기반 외부 전송 완료");
        } catch (Exception e) {
            log.error("주문 완료 이벤트 기반 외부 전송 실패", e);
        }
    }
}
