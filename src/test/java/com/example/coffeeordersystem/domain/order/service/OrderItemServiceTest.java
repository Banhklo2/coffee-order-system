package com.example.coffeeordersystem.domain.order.service;

import com.example.coffeeordersystem.domain.menu.entity.Menu;
import com.example.coffeeordersystem.domain.order.dto.response.OrderItemResponse;
import com.example.coffeeordersystem.domain.order.entity.Order;
import com.example.coffeeordersystem.domain.order.entity.OrderItem;
import com.example.coffeeordersystem.domain.order.entity.OrderStatus;
import com.example.coffeeordersystem.domain.order.repository.OrderItemRepository;
import com.example.coffeeordersystem.domain.order.repository.OrderRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("주문 상품 조회 성공")
    void getOrderItems_success() {
        // given
        Long orderId = 1L;

        User user = User.builder()
                .id(1L)
                .point(BigDecimal.valueOf(10000))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .totalPrice(BigDecimal.valueOf(9500))
                .status(OrderStatus.COMPLETED)
                .orderedAt(LocalDateTime.now())
                .build();

        Menu menu1 = Menu.builder()
                .id(1L)
                .name("아메리카노")
                .price(BigDecimal.valueOf(4500))
                .build();

        Menu menu2 = Menu.builder()
                .id(2L)
                .name("카페라떼")
                .price(BigDecimal.valueOf(5000))
                .build();

        OrderItem orderItem1 = OrderItem.builder()
                .id(1L)
                .order(order)
                .menu(menu1)
                .name("아메리카노")
                .price(BigDecimal.valueOf(4500))
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .id(2L)
                .order(order)
                .menu(menu2)
                .name("카페라떼")
                .price(BigDecimal.valueOf(5000))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(orderId)).thenReturn(List.of(orderItem1, orderItem2));

        // when
        List<OrderItemResponse> result = orderItemService.getOrderItems(orderId);

        // then
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getOrderItemId()).isEqualTo(1L);
        assertThat(result.get(0).getMenuId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("아메리카노");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(4500));

        assertThat(result.get(1).getOrderItemId()).isEqualTo(2L);
        assertThat(result.get(1).getMenuId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("카페라떼");
        assertThat(result.get(1).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(5000));

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, times(1)).findAllByOrderId(orderId);
    }

    @Test
    @DisplayName("주문 상품 조회 실패 - 존재하지 않는 주문")
    void getOrderItems_fail_notFound() {
        // given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> orderItemService.getOrderItems(orderId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderItemRepository, never()).findAllByOrderId(anyLong());
    }
}
