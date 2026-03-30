package com.example.coffeeordersystem.domain.payment.controller;

import com.example.coffeeordersystem.domain.payment.dto.response.PaymentResponse;
import com.example.coffeeordersystem.domain.payment.service.PaymentService;
import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 주문에 대한 결제 정보 조회
    @GetMapping("/{orderId}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable Long orderId) {
        return ApiResponse.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
