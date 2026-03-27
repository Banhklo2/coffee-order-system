package com.example.coffeeordersystem.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final HttpStatus status;

    public ServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
