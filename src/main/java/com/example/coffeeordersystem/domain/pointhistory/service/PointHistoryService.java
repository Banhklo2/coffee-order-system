package com.example.coffeeordersystem.domain.pointhistory.service;

import com.example.coffeeordersystem.domain.pointhistory.dto.response.PointHistoryResponse;
import com.example.coffeeordersystem.domain.pointhistory.repository.PointHistoryRepository;
import com.example.coffeeordersystem.domain.user.entity.User;
import com.example.coffeeordersystem.domain.user.repository.UserRepository;
import com.example.coffeeordersystem.global.exception.ErrorCode;
import com.example.coffeeordersystem.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

    // 포인트 이력 조회
    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointHistories(Long userId) {
        findUser(userId);

        return pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }

    // 포인트 이력 조회 공통 메서드
    // - userId로 조회 후 없으면 예외 발생
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }
}
