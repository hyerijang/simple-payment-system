package com.example.simple_payment_system.service;

import com.example.simple_payment_system.common.PaymentToken;
import com.example.simple_payment_system.dto.Response;
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

    protected Response getPayment(String impUid, String accessToken) {
        String paymentUrl = "https://api.iamport.kr/payments/" + impUid;
        return webClient.get()
            .uri(paymentUrl)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .retrieve()
            .onStatus(
                status -> status.is5xxServerError(),
                clientResponse -> Mono.error(new RuntimeException("PortOne : Server error occurred"))
            )
            .onStatus(
                status -> status.is4xxClientError(),
                clientResponse -> Mono.error(new RuntimeException("PortOne : Client error occurred"))
            )
            .bodyToMono(Response.class)
            .blockOptional()
            .orElseThrow(() -> new RuntimeException("PortOne : valid payment not found"));

    }

    public Response canclePayment(String impUid, BigDecimal cancelRequestAmount, String reason, String accessToken) {
        String cancelUrl = "https://api.iamport.kr/payments/cancel";
        Map<String, Object> requestData = Map.of(
            "reason", reason,
            "imp_uid", impUid,
            "amount", cancelRequestAmount,
            "checksum", cancelRequestAmount // Assuming cancelableAmount is the same as cancelRequestAmount
        );

        webClient.post()
            .uri(cancelUrl)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestData)
            .retrieve()
            .onStatus(
                status -> status.is5xxServerError(),
                clientResponse -> Mono.error(new RuntimeException("PortOne : Server error occurred"))
            )
            .onStatus(
                status -> status.is4xxClientError(),
                clientResponse -> Mono.error(new RuntimeException("PortOne : Client error occurred"))
            )
            .bodyToMono(Response.class)
            .blockOptional()
            .orElseThrow(() -> new RuntimeException("PortOne : cancel payment failed"));

    }
}
