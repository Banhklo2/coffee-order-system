package com.example.coffeeordersystem.domain.payment.service;

import com.example.coffeeordersystem.domain.order.entity.Order;
import com.example.coffeeordersystem.domain.order.entity.OrderStatus;
import com.example.coffeeordersystem.domain.payment.dto.response.PaymentResponse;
import com.example.coffeeordersystem.domain.payment.entity.Payment;
import com.example.coffeeordersystem.domain.payment.entity.PaymentStatus;
import com.example.coffeeordersystem.domain.payment.repository.PaymentRepository;
import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("주문에 대한 결제 정보 조회 성공")
    void getPaymentByOrderId_success() {
        // given
        Long orderId = 1L;
        Long userId = 1L;
        Long paymentId = 1L;

        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(10000))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .totalPrice(BigDecimal.valueOf(4500))
                .status(OrderStatus.COMPLETED)
                .orderedAt(LocalDateTime.now())
                .build();

        Payment payment = Payment.builder()
                .id(paymentId)
                .order(order)
                .user(user)
                .amount(BigDecimal.valueOf(4500))
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        // when
        PaymentResponse result = paymentService.getPaymentByOrderId(orderId);

        // then
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(4500));
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.getPaidAt()).isNotNull();

        verify(paymentRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    @DisplayName("주문에 대한 결제 정보 조회 실패 - 결제 정보 없음")
    void getPaymentByOrderId_fail_notFound() {
        // given
        Long orderId = 999L;
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> paymentService.getPaymentByOrderId(orderId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
        verify(paymentRepository, times(1)).findByOrderId(orderId);
    }
}
