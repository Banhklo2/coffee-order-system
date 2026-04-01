package com.example.coffeeordersystem.domain.order.service;

import com.example.coffeeordersystem.domain.menu.entity.Menu;
import com.example.coffeeordersystem.domain.menu.repository.MenuRepository;
import com.example.coffeeordersystem.domain.order.dto.request.OrderCreateRequest;
import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    private User user;
    private Menu menu;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 (포인트 5000)
        user = userRepository.save(
                User.builder()
                        .point(BigDecimal.valueOf(5000))
                        .build()
        );

        // 테스트용 메뉴 (가격 3000)
        menu = menuRepository.save(
                Menu.builder()
                        .name("아메리카노")
                        .price(BigDecimal.valueOf(3000))
                        .build()
        );
    }

    @Test
    @DisplayName("동시에 두 번 주문하면 포인트는 한 번만 차감된다")
    void 동시에_두번_주문하면_포인트는_한번만_차감된다() throws InterruptedException {
        // given
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount); // 스레드 준비 대기
        CountDownLatch startLatch = new CountDownLatch(1);           // 동시에 시작
        CountDownLatch doneLatch = new CountDownLatch(threadCount);  // 작업 완료 대기

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();

                try {
                    startLatch.await(); // 모든 스레드 동시에 실행

                    OrderCreateRequest request = OrderCreateRequest.builder()
                            .userId(user.getId())
                            .menuId(menu.getId())
                            .build();

                    orderService.createOrder(request);
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();      // 모든 스레드 준비 완료 대기
        startLatch.countDown();  // 동시에 시작
        doneLatch.await();       // 모든 스레드 종료 대기
        executorService.shutdown();

        // then
        User savedUser = userRepository.findById(user.getId())
                .orElseThrow();

        // 결과 로그 출력 (검증용)
        System.out.println("=== 테스트 결과 ===");
        System.out.println("successCount = " + successCount.get());
        System.out.println("failCount = " + failCount.get());
        System.out.println("final point = " + savedUser.getPoint());

        // 검증
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(savedUser.getPoint()).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }
}
