package com.example.simple_payment_system.common;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Component
public class PaymentToken {

    // 토큰 생성에 필요한 값들
    private final String impKey;
    private final String impSecret;

    public PaymentToken() {
        // FIXME
        impKey = "0386646187556031";
        impSecret = "YrJagqpwDVJpMZ01HcBjtTH6jd7eWggfaSVPhrpDyykatyEVstbkLBzljxe5VBmGAYFvp3NM5NvxavNY";
    }
}
