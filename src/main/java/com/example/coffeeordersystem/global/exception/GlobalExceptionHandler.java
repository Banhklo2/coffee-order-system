package com.example.coffeeordersystem.global.exception;

import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import com.example.coffeeordersystem.global.common.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<ExceptionResponse>> handleServiceException(ServiceException exception, HttpServletRequest request) {
        ExceptionResponse response = ExceptionResponse.from(exception.getStatus().value(), exception.getMessage(), request.getRequestURI());
        return ResponseEntity.status(exception.getStatus()).body(ApiResponse.fail(exception.getStatus(), response));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ExceptionResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("입력 값이 올바르지 않습니다.");

        ExceptionResponse response = ExceptionResponse.from(exception.getStatusCode().value(), errorMessage, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(HttpStatus.BAD_REQUEST, response));
    }
}
