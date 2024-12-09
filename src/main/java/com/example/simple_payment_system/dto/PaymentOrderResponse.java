package com.example.simple_payment_system.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PaymentOrderResponse {
    private String merchantUid;
    private String name;
    private BigDecimal amount;
    private Long buyerId;
    private String payMethod;
}