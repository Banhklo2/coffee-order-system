package com.example.coffeeordersystem.domain.user.service;

import com.example.coffeeordersystem.domain.pointhistory.entity.PointHistory;
import com.example.coffeeordersystem.domain.pointhistory.repository.PointHistoryRepository;
import com.example.coffeeordersystem.domain.user.dto.request.UserChargeRequest;
import com.example.coffeeordersystem.domain.user.dto.response.UserPointResponse;
import com.example.coffeeordersystem.domain.user.dto.response.UserResponse;
import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.domain.user.repository.UserRepository;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("사용자 목록 조회 성공")
    void getUsers_success() {
        // given
        User user1 = User.builder()
                .id(1L)
                .point(BigDecimal.valueOf(5000))
                .build();

        User user2 = User.builder()
                .id(2L)
                .point(BigDecimal.valueOf(10000))
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // when
        List<UserResponse> result = userService.getUsers();

        // then
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(5000));

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(10000));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("사용자 단건 조회 성공")
    void getUser_success() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(5000))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserResponse result = userService.getUser(userId);

        // then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getPoint()).isEqualByComparingTo(BigDecimal.valueOf(5000));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 단건 조회 실패 - 존재하지 않는 사용자")
    void getUser_fail_notFound() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.getUser(userId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 포인트 조회 성공")
    void getUserPoint_success() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(5000))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserPointResponse result = userService.getUserPoint(userId);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getPoint()).isEqualByComparingTo(BigDecimal.valueOf(5000));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void chargePoint_success() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(5000))
                .build();

        UserChargeRequest request = new UserChargeRequest();
        ReflectionTestUtils.setField(request, "amount", BigDecimal.valueOf(3000));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(null);

        // when
        UserPointResponse result = userService.chargePoint(userId, request);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getPoint()).isEqualByComparingTo(BigDecimal.valueOf(8000));

        verify(userRepository, times(1)).findById(userId);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 충전 실패 - 존재하지 않는 사용자")
    void chargePoint_fail_userNotFound() {
        // given
        Long userId = 999L;

        UserChargeRequest request = new UserChargeRequest();
        ReflectionTestUtils.setField(request, "amount", BigDecimal.valueOf(3000));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.chargePoint(userId, request));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository, times(1)).findById(userId);
        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }

    @Test
    @DisplayName("포인트 충전 실패 - 0 이하 금액")
    void chargePoint_fail_invalidAmount() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(5000))
                .build();

        UserChargeRequest request = new UserChargeRequest();
        ReflectionTestUtils.setField(request, "amount", BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> userService.chargePoint(userId, request));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CHARGE_AMOUNT);
        verify(userRepository, times(1)).findById(userId);
        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }
}
