package com.example.simple_payment_system.domain.payment.order;

public enum PaymentOrderStatus {
    // 예약중, 결제완료, 배송중, 취소됨
    RESERVED("예약중"),
    READY("가상계좌 발급"),
    PAID("결제완료"),
    DELIVERING("배송중"),
    CANCELLED("관리자 콘솔에서 결제취소"),
    FAILED("결제실패");

    PaymentOrderStatus(String description) {
        this.description = description;
    }

    String description;
}
