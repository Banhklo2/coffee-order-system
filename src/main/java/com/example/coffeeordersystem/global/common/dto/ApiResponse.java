package com.example.coffeeordersystem.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int code;
    private final T data;

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, 200, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, 200, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, data);
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(true, 204, null);
    }

    public static <T> ApiResponse<T> fail(HttpStatus status) {
        return new ApiResponse<>(false, status.value(), null);
    }

    public static <T> ApiResponse<T> fail(HttpStatus status, T data) {
        return new ApiResponse<>(false, status.value(), data);
    }
}
