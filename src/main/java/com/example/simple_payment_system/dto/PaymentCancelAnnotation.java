package com.example.simple_payment_system.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentCancelAnnotation {
    private String impUid;
    private BigDecimal amount;
    private String reason;
    private Integer cancelledAt;
}