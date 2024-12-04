package com.example.simple_payment_system.domain.payment;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CARD("card", "신용카드"),
    VBANKS("vbanks", "가상계좌"),
    ;

    String code;
    String description;

    PaymentMethod(String code, String description) {
        this.description = description;
        this.code = code;
    }

}
