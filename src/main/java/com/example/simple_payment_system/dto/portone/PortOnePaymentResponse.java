package com.example.simple_payment_system.dto.portone;

import lombok.Data;

@Data
public class PortOnePaymentResponse {
    private Integer code;
    private String message;
    private PaymentAnnotation response;
}
