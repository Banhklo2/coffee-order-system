package com.example.coffeeordersystem.domain.pointhistory.service;

import com.example.coffeeordersystem.domain.pointhistory.dto.response.PointHistoryResponse;
import com.example.coffeeordersystem.domain.pointhistory.entity.PointHistory;
import com.example.coffeeordersystem.domain.pointhistory.entity.PointHistoryStatus;
import com.example.coffeeordersystem.domain.pointhistory.repository.PointHistoryRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("포인트 이력 조회 성공")
    void getPointHistories_success() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .point(BigDecimal.valueOf(10000))
                .build();

        PointHistory history1 = PointHistory.builder()
                .id(1L)
                .user(user)
                .amount(BigDecimal.valueOf(5000))
                .status(PointHistoryStatus.CHARGE)
                .build();

        PointHistory history2 = PointHistory.builder()
                .id(2L)
                .user(user)
                .amount(BigDecimal.valueOf(-4500))
                .status(PointHistoryStatus.USE)
                .build();

        ReflectionTestUtils.setField(history1, "createdAt", LocalDateTime.of(2026, 3, 30, 10, 0));
        ReflectionTestUtils.setField(history2, "createdAt", LocalDateTime.of(2026, 3, 31, 12, 0));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(history2, history1));

        // when
        List<PointHistoryResponse> result = pointHistoryService.getPointHistories(userId);

        // then
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getHistoryId()).isEqualTo(2L);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(-4500));
        assertThat(result.get(0).getStatus()).isEqualTo(PointHistoryStatus.USE);
        assertThat(result.get(0).getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 3, 31, 12, 0));

        assertThat(result.get(1).getHistoryId()).isEqualTo(1L);
        assertThat(result.get(1).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        assertThat(result.get(1).getStatus()).isEqualTo(PointHistoryStatus.CHARGE);
        assertThat(result.get(1).getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 3, 30, 10, 0));

        verify(userRepository, times(1)).findById(userId);
        verify(pointHistoryRepository, times(1)).findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    @DisplayName("포인트 이력 조회 실패 - 존재하지 않는 사용자")
    void getPointHistories_fail_userNotFound() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        ServiceException exception = assertThrows(ServiceException.class,
                () -> pointHistoryService.getPointHistories(userId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(userRepository, times(1)).findById(userId);
        verify(pointHistoryRepository, never()).findAllByUserIdOrderByCreatedAtDesc(anyLong());
    }
}
