package com.example.coffeeordersystem.global.exception;

import com.example.coffeeordersystem.global.common.dto.ApiResponse;
import com.example.coffeeordersystem.global.common.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<ExceptionResponse>> handleServiceException(
            ServiceException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                exception.getStatus(),
                exception.getErrorCode(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ExceptionResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("입력 값이 올바르지 않습니다.");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_REQUEST,
                errorMessage,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ExceptionResponse>> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception occurred", exception);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
    }

    private ResponseEntity<ApiResponse<ExceptionResponse>> buildErrorResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String path
    ) {
        ExceptionResponse response = ExceptionResponse.from(errorCode, path);

        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(status, response));
    }

    private ResponseEntity<ApiResponse<ExceptionResponse>> buildErrorResponse(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            String path
    ) {
        ExceptionResponse response = ExceptionResponse.from(errorCode, message, path);

        return ResponseEntity
                .status(status)
                .body(ApiResponse.fail(status, response));
    }
}
