package com.example.coffeeordersystem.domain.user.controller;

import com.example.coffeeordersystem.domain.user.dto.request.UserChargeRequest;
import com.example.coffeeordersystem.domain.user.dto.response.UserPointResponse;
import com.example.coffeeordersystem.domain.user.dto.response.UserResponse;
import com.example.coffeeordersystem.domain.user.service.UserService;
import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 사용자 목록 조회
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.ok(userService.getUsers());
    }

    // 사용자 단건 조회
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        return ApiResponse.ok(userService.getUser(userId));
    }

    // 사용자 포인트 조회
    @GetMapping("/{userId}/points")
    public ApiResponse<UserPointResponse> getUserPoint(@PathVariable Long userId) {
        return ApiResponse.ok(userService.getUserPoint(userId));
    }

    // 포인트 충전
    @PostMapping("/{userId}/points/charge")
    public ApiResponse<UserPointResponse> chargePoint(
            @PathVariable Long userId,
            @RequestBody @Valid UserChargeRequest request
    ) {
        return ApiResponse.ok(userService.chargePoint(userId, request));
    }
}
