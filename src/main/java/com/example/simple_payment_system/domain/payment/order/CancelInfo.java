package com.example.simple_payment_system.domain.payment.order;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CancelInfo {
    private String cancelReason;
    private BigDecimal cancelAmount; // 취소금액
    private Integer cancelledAt; // 취소일시
}