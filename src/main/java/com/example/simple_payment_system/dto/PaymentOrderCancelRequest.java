package com.example.simple_payment_system.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@NoArgsConstructor
public class PaymentOrderCancelRequest {
    String impUid;
    String merchantUid;
    BigDecimal cancelRequestAmount; // 환불금액
    String reason;// 환불사유
    String refundHolder; // [가상계좌 환불시 필수입력] 환불 수령계좌 예금주
    String refundBank; // [가상계좌 환불시 필수입력] 환불 수령계좌 은행코드(예: KG이니시스의 경우 신한은행은 88번)
    String refundAccount; // [가상계좌 환불시 필수입력]  환불 수령 계좌
}
