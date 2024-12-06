package com.example.simple_payment_system.domain.payment.order;

import com.example.simple_payment_system.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "payment_order",
    indexes = {
        @Index(name = "idx_merchant_uid", columnList = "merchantUid")
    })
public class PaymentOrder extends BaseEntity {  // Payment Order(지급지시서)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String merchantUid; // 주문번호

    @Column(nullable = false)
    private String name; // 제품명

    @Column(nullable = false)
    private String payMethod; // 결제수단

    @Column(nullable = false)
    private BigDecimal amount; // 결제금액

    private BigDecimal cancelAmount; // 취소금액

    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentOrderStatus status; // 지급지시서 상태

    // 가상계좌
    private String vbankCode; // 가상 계좌 은행 표쥰코드
    private String vbankName; // 가상 계좌 은행명
    private String vbankNum; // 고정 가상 계좌 번호
    private String vbankHolder; // 가상 계좌 예금주
    private Integer vbankDate; // 가상 계좌 입금 기한

    // 가상계좌 업데이트 하는 함수
    public void updateVbank(String vbankCode, String vbankName, String vbankNum, String vbankHolder,
                            Integer vbankDate) {
        this.vbankCode = vbankCode;
        this.vbankName = vbankName;
        this.vbankNum = vbankNum;
        this.vbankHolder = vbankHolder;
        this.vbankDate = vbankDate;
    }

}
