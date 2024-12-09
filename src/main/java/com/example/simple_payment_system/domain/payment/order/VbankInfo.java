package com.example.simple_payment_system.domain.payment.order;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class VbankInfo {
    private String vbankCode; // 가상 계좌 은행 표준코드
    private String vbankName; // 가상 계좌 은행명
    private String vbankNum; // 고정 가상 계좌 번호
    private String vbankHolder; // 가상 계좌 예금주
    private Integer vbankDate; // 가상 계좌 입금 기한
}