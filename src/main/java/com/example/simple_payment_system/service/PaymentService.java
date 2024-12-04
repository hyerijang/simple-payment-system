package com.example.simple_payment_system.service;

import com.example.simple_payment_system.PaymentRepository;
import com.example.simple_payment_system.common.PaymentToken;
import com.example.simple_payment_system.domain.payment.order.PaymentOrder;
import com.example.simple_payment_system.domain.payment.order.PaymentOrderStatus;
import com.example.simple_payment_system.dto.PaymentOrderUpdateRequest;
import com.example.simple_payment_system.dto.Response;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentOrderService paymentOrderService;
    private final WebClient webClient = WebClient.builder().build();

    @Transactional
    public void complete(PaymentOrderUpdateRequest result) {
        try {
            String impUid = result.getImpUid();
            String merchantUid = result.getMerchantUid();

            // 1. 포트원 API 엑세스 토큰 발급
            String accessToken = getAccessToken().block();

            // 2. 포트원 결제내역 단건조회 API 호출
            Response payment = getPayment(impUid, accessToken).block();
            if (payment == null || payment.getResponse() == null) {
                log.warn("포트원 결제 내역 조회 실패: merchantUid = {}", merchantUid);
                throw new RuntimeException("포트원 결제 내역 조회 실패");
            }

            // 3. 고객사 내부 주문 데이터의 가격과 실제 지불된 금액을 비교합니다.
            PaymentOrder paymentOrder = paymentOrderService.findByMerchantUid(merchantUid);

            if (isAmountEqual(paymentOrder.getAmount(), payment.getResponse().getAmount())) {
                String status = payment.getResponse().getStatus();
                switch (status) {
                    case "ready":
                        // 가상 계좌가 발급된 상태입니다.
                        // 계좌 정보를 이용해 원하는 로직을 구성하세요.
                        log.info("가상 계좌 발급 merchantUid = {}", merchantUid);
                        break;
                    case "paid":
                        // 모든 금액을 지불했습니다! 완료 시 원하는 로직을 구성하세요.
                        log.info("지불 성공 merchantUid = {}", merchantUid);
                        paymentOrder.setStatus(PaymentOrderStatus.PAID);
                        break;
                    default:
                        // 결제가 실패했습니다. 원인을 파악하여 처리하세요.
                        log.warn("결제 실패 merchantUid = {}", merchantUid);
                        paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                        throw new RuntimeException("Payment failed");
                }
            } else {
                // 결제 금액이 불일치하여 위/변조 시도가 의심됩니다.
                log.warn("결제 금액 불일치 merchantUid = {}, ({}, {})", merchantUid, payment.getResponse().getAmount(),
                    paymentOrder.getAmount());
                throw new RuntimeException("Payment amount mismatch");
            }
        } catch (Exception e) {
            // 결제 검증에 실패했습니다.
            log.error("결제 검증 실패 merchantUid = {} , {}", result.getMerchantUid(), e);
            throw new RuntimeException("Payment verification failed", e);
        }
    }

    private boolean isAmountEqual(BigDecimal orderAmount, BigDecimal responseAmount) {
        return orderAmount.compareTo(responseAmount) == 0;
    }

    private Mono<Response> getPayment(String impUid, String accessToken) {
        String paymentUrl = "https://api.iamport.kr/payments/" + impUid;
        return webClient.get()
            .uri(paymentUrl)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .retrieve()
            .bodyToMono(Response.class);
    }

    public Mono<String> getAccessToken() {
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

}