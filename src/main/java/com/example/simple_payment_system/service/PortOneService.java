package com.example.simple_payment_system.service;

import com.example.simple_payment_system.common.PaymentToken;
import com.example.simple_payment_system.dto.portone.PortOnePaymentResponse;
import com.example.simple_payment_system.exception.CustomApiException;
import com.example.simple_payment_system.exception.ExceptionEnum;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PortOneService {

    private final WebClient webClient = WebClient.builder().build();


    protected Mono<String> getAccessToken() {
        String tokenUrl = "https://api.iamport.kr/users/getToken";
        PaymentToken paymentToken = new PaymentToken();
        return webClient.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(paymentToken)
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> (String) ((Map) response.get("response")).get("access_token"));
    }

    protected PortOnePaymentResponse getPayment(String impUid, String accessToken) {
        String paymentUrl = "https://api.iamport.kr/payments/" + impUid;
        return webClient.get()
            .uri(paymentUrl)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .retrieve()
            .onStatus(
                status -> status.is5xxServerError(),
                clientResponse -> Mono.error(
                    new CustomApiException(ExceptionEnum.PORT_ONE_API_EXCEPTION, "PortOne : Server error occurred"))
            )
            .onStatus(
                status -> status.is4xxClientError(),
                clientResponse -> Mono.error(
                    new CustomApiException(ExceptionEnum.BAD_REQUEST, "PortOne : Client error occurred"))
            )
            .bodyToMono(PortOnePaymentResponse.class)
            .blockOptional()
            .orElseThrow(
                () -> new CustomApiException(ExceptionEnum.PORT_ONE_NOT_FOUND, "PortOne : valid payment not found"));

    }

    public PortOnePaymentResponse cancelPayment(String impUid, BigDecimal cancelRequestAmount, String reason,
                                                String accessToken) {
        String cancelUrl = "https://api.iamport.kr/payments/cancel";
        Map<String, Object> requestData = Map.of(
            "reason", reason,
            "imp_uid", impUid,
            "amount", cancelRequestAmount,
            "checksum", cancelRequestAmount // Assuming cancelableAmount is the same as cancelRequestAmount
        );

        return webClient.post()
            .uri(cancelUrl)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestData)
            .retrieve()
            .onStatus(
                status -> status.is5xxServerError(),
                clientResponse -> Mono.error(
                    new CustomApiException(ExceptionEnum.PORT_ONE_API_EXCEPTION, "PortOne : Server error occurred"))
            )
            .onStatus(
                status -> status.is4xxClientError(),
                clientResponse -> Mono.error(
                    new CustomApiException(ExceptionEnum.BAD_REQUEST, "PortOne : Client error occurred"))
            )
            .bodyToMono(PortOnePaymentResponse.class)
            .blockOptional()
            .orElseThrow(
                () -> new CustomApiException(ExceptionEnum.PORT_ONE_NOT_FOUND, "PortOne : valid payment not found"));
    }
}
