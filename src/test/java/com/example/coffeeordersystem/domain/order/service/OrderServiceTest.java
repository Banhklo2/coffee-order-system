package com.example.coffeeordersystem.domain.order.service;

import com.example.coffeeordersystem.domain.menu.entity.Menu;
import com.example.coffeeordersystem.domain.menu.repository.MenuRepository;
import com.example.coffeeordersystem.domain.order.dto.request.OrderCreateRequest;
import com.example.coffeeordersystem.domain.order.dto.response.OrderResponse;
import com.example.coffeeordersystem.domain.order.entity.Order;
import com.example.coffeeordersystem.domain.order.entity.OrderItem;
import com.example.coffeeordersystem.domain.order.entity.OrderStatus;
import com.example.coffeeordersystem.domain.order.repository.OrderItemRepository;
import com.example.coffeeordersystem.domain.order.repository.OrderRepository;
import com.example.coffeeordersystem.domain.payment.entity.Payment;
import com.example.coffeeordersystem.domain.payment.repository.PaymentRepository;
import com.example.coffeeordersystem.domain.pointhistory.entity.PointHistory;
import com.example.coffeeordersystem.domain.pointhistory.repository.PointHistoryRepository;
import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.domain.user.repository.UserRepository;
import com.example.coffeeordersystem.external.event.OrderCompletedEvent;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        // given
        Long userId = 1L;
        Long menuId = 1L;

        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(10000))
                .build();

        Menu menu = Menu.builder()
                .id(menuId)
                .name("아메리카노")
                .price(BigDecimal.valueOf(4500))
                .build();

        OrderCreateRequest request = new OrderCreateRequest(userId, menuId);

        when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return Order.builder()
                    .id(1L)
                    .user(order.getUser())
                    .totalPrice(order.getTotalPrice())
                    .status(order.getStatus())
                    .orderedAt(order.getOrderedAt())
                    .build();
        });

        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem orderItem = invocation.getArgument(0);
            return OrderItem.builder()
                    .id(2L)
                    .order(orderItem.getOrder())
                    .menu(orderItem.getMenu())
                    .name(orderItem.getName())
                    .price(orderItem.getPrice())
                    .build();
        });

        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(null);
        when(paymentRepository.save(any(Payment.class))).thenReturn(null);

        // when
        OrderResponse result = orderService.createOrder(request);

        // then
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(4500));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getOrderedAt()).isNotNull();

        verify(userRepository, times(1)).findByIdWithPessimisticLock(userId);
        verify(menuRepository, times(1)).findById(menuId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(eventPublisher, times(1)).publishEvent(any(OrderCompletedEvent.class));
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 사용자")
    void createOrder_fail_userNotFound() {
        // given
        Long userId = 1L;
        Long menuId = 1L;

        OrderCreateRequest request = new OrderCreateRequest(userId, menuId);

        when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> orderService.createOrder(request));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

        verify(userRepository, times(1)).findByIdWithPessimisticLock(userId);
        verify(menuRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 메뉴")
    void createOrder_fail_menuNotFound() {
        // given
        Long userId = 1L;
        Long menuId = 1L;

        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(10000))
                .build();

        OrderCreateRequest request = new OrderCreateRequest(userId, menuId);

        when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user));
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> orderService.createOrder(request));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MENU_NOT_FOUND);

        verify(userRepository, times(1)).findByIdWithPessimisticLock(userId);
        verify(menuRepository, times(1)).findById(menuId);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("주문 단건 조회 성공")
    void getOrder_success() {
        // given
        Long orderId = 1L;

        User user = User.builder()
                .id(1L)
                .point(BigDecimal.valueOf(10000))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .totalPrice(BigDecimal.valueOf(4500))
                .status(OrderStatus.COMPLETED)
                .orderedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        OrderResponse result = orderService.getOrder(orderId);

        // then
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(4500));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getOrderedAt()).isNotNull();

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("주문 단건 조회 실패 - 존재하지 않는 주문")
    void getOrder_fail_notFound() {
        // given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> orderService.getOrder(orderId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
        verify(orderRepository, times(1)).findById(orderId);
    }
}
