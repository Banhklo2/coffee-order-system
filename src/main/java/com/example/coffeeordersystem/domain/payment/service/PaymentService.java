package com.example.coffeeordersystem.domain.payment.service;

import com.example.coffeeordersystem.domain.payment.dto.response.PaymentResponse;
import com.example.coffeeordersystem.domain.payment.entity.Payment;
import com.example.coffeeordersystem.domain.payment.repository.PaymentRepository;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // 주문에 대한 결제 정보 조회
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = findPaymentByOrderId(orderId);
        return PaymentResponse.from(payment);
    }

    // 결제 정보 조회 공통 메서드
    // - orderId로 조회 후 없으면 예외 발생
    private Payment findPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}
