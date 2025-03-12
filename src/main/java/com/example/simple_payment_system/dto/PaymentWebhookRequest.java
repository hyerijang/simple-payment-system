package com.example.simple_payment_system.dto;

import com.example.simple_payment_system.domain.payment.PaymentStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;


@JsonNaming(SnakeCaseStrategy.class)
@Getter
@NoArgsConstructor
public class PaymentWebhookRequest {
    String impUid;
    String merchantUid;
    PaymentStatus status;
    String cancellationId;
}
