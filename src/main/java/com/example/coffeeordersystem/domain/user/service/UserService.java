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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    // 사용자 목록 조회
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    // 사용자 단건 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        User user = findUser(userId);
        return UserResponse.from(user);
    }

    // 사용자 포인트 조회
    @Transactional(readOnly = true)
    public UserPointResponse getUserPoint(Long userId) {
        User user = findUser(userId);
        return UserPointResponse.from(user);
    }

    // 포인트 충전
    public UserPointResponse chargePoint(Long userId, UserChargeRequest request) {
        User user = findUser(userId);
        user.charge(request.getAmount());

        pointHistoryRepository.save(PointHistory.charge(user, request.getAmount()));

        return UserPointResponse.from(user);
    }

    // 사용자 조회 공통 메서드
    // -userId로 조회 후 없으면 예외 발생
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }
}
