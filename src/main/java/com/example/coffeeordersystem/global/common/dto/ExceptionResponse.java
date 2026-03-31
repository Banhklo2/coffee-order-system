package com.example.coffeeordersystem.global.common.dto;

import com.example.coffeeordersystem.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExceptionResponse {

    private final ErrorCode errorCode;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;

    public static ExceptionResponse from(ErrorCode errorCode, String path) {
        return ExceptionResponse.builder()
                .errorCode(errorCode)
                .message(errorCode.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ExceptionResponse from(ErrorCode errorCode, String message, String path) {
        return ExceptionResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
