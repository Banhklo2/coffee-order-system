package com.example.coffeeordersystem.domain.order.service;

import com.example.coffeeordersystem.domain.menu.entity.Menu;
import com.example.coffeeordersystem.domain.menu.repository.MenuRepository;
import com.example.coffeeordersystem.domain.order.dto.request.OrderCreateRequest;
import com.example.coffeeordersystem.domain.order.dto.response.OrderResponse;
import com.example.coffeeordersystem.domain.order.entity.Order;
import com.example.coffeeordersystem.domain.order.entity.OrderItem;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 주문 생성 + 결제
    // - 사용자 / 메뉴 조회
    // - 주문 및 주문상품 생성
    // - 포인트 차감 및 포인트 이력 저장
    // - 결제 성공 / 실패 정보 저장
    // - 외부 플랫폼 이벤트 발행 (비동기)
    public OrderResponse createOrder(OrderCreateRequest request) {
        log.info("주문 생성 시작 - userId={}, menuId={}", request.getUserId(), request.getMenuId());

        User user = findUser(request.getUserId());
        Menu menu = findMenu(request.getMenuId());

        Order order = Order.create(user, menu.getPrice());
        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = OrderItem.create(savedOrder, menu);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        Payment payment;
        try {
            user.use(menu.getPrice());
            pointHistoryRepository.save(PointHistory.use(user, menu.getPrice()));
            payment = Payment.success(savedOrder, user, menu.getPrice());
        } catch (ServiceException e) {
            payment = Payment.fail(order, user, menu.getPrice());
            paymentRepository.save(payment);
            throw e;
        }

        paymentRepository.save(payment);

        log.info("외부 플랫폼 비동기 전송 요청");
        eventPublisher.publishEvent(
                OrderCompletedEvent.builder()
                        .userId(user.getId())
                        .menuId(savedOrderItem.getMenu().getId())
                        .amount(savedOrder.getTotalPrice())
                        .build()
        );
        log.info("외부 플랫폼 전송 직후");

        return OrderResponse.from(savedOrder);
    }

    // 주문 단건 조회
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        Order order = findOrder(orderId);
        return OrderResponse.from(order);
    }

    // 사용자 조회 공통 메서드
    // - userId로 조회 후 없으면 예외 발생
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }

    // 메뉴 조회 공통 메서드
    // - menuId로 조회 후 없으면 예외 발생
    private Menu findMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new ServiceException(ErrorCode.MENU_NOT_FOUND));
    }

    // 주문 조회 공통 메서드
    // - orderId로 조회 후 없으면 예외 발생
    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));
    }
}
