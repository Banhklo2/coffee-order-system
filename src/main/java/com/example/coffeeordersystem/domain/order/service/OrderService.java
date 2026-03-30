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
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PointHistoryRepository pointHistoryRepository;

    // 주문 생성 + 결제
    // - 메뉴 가격으로 총 금액 계산
    // - 사용자 포인트 차감
    // - 주문/주문상품/결제 정보를 함께 저장
    public OrderResponse createOrder(OrderCreateRequest request) {
        User user = findUser(request.getUserId());
        Menu menu = findMenu(request.getMenuId());

        Order order = Order.create(user, menu.getPrice());
        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = OrderItem.create(savedOrder, menu);
        orderItemRepository.save(orderItem);

        Payment payment;
        try {
            user.use(menu.getPrice());

            pointHistoryRepository.save(PointHistory.use(user, menu.getPrice()));

            payment = Payment.success(order, user, menu.getPrice());
        } catch (ServiceException e) {
            payment = Payment.fail(order, user, menu.getPrice());
            paymentRepository.save(payment);
            throw e;
        }

        paymentRepository.save(payment);

        return OrderResponse.from(order);
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
