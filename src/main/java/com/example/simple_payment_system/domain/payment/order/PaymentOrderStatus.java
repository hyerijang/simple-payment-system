package com.example.simple_payment_system.domain.payment.order;

public enum PaymentOrderStatus {
    // 예약중, 결제완료, 배송중, 취소됨
    RESERVED, PAID, DELIVERING, CANCELED, FAILED
}
