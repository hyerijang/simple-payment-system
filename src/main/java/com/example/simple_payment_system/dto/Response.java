package com.example.simple_payment_system.dto;

import lombok.Data;

@Data
public class Response {
    private Integer code;
    private String message;
    private PaymentAnnotation response;
}
