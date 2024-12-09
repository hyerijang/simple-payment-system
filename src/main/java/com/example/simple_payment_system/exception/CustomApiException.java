package com.example.simple_payment_system.exception;

import lombok.Getter;


@Getter
public class CustomApiException extends RuntimeException {
    private ExceptionEnum error;
    private String message;

    public CustomApiException(ExceptionEnum e, String message) {
        super(e.getMessage());
        this.error = e;
        this.message = message;
    }
}