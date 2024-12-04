package com.example.simple_payment_system.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@NoArgsConstructor
public class PaymentOrderSaveRequest {
    private String merchantUid;
    private String name;
    private BigDecimal amount;
    private Long buyerId;
}