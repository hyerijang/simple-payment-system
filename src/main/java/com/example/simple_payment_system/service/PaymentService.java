package com.example.simple_payment_system.service;

import com.example.simple_payment_system.PaymentRepository;
import com.example.simple_payment_system.common.PaymentToken;
import com.example.simple_payment_system.domain.payment.order.PaymentOrder;
import com.example.simple_payment_system.domain.payment.order.PaymentOrderStatus;
import com.example.simple_payment_system.dto.PaymentOrderUpdateRequest;
import com.example.simple_payment_system.dto.PaymentWebhookRequest;
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
            verifyPayment(result.getImpUid(), result.getMerchantUid());
        } catch (Exception e) {
            // 결제 검증에 실패했습니다.
            log.error("결제 검증 실패 merchantUid = {} , {}", result.getMerchantUid(), e);
            throw new RuntimeException("Payment verification failed", e);
        }
    }

    private void verifyPayment(String impUid, String merchantUid) {

        // 1. 포트원 API 엑세스 토큰 발급
        String accessToken = getAccessToken().block();

        // 2. 포트원 결제내역 단건조회 API 호출
        Response payment = getPayment(impUid, accessToken);

        // 3. 고객사 내부 주문 데이터의 가격과 실제 지불된 금액을 비교합니다.
        PaymentOrder paymentOrder = paymentOrderService.findByMerchantUid(merchantUid);
        BigDecimal amount = payment.getResponse().getAmount(); // 실제 결제 된 금액
        BigDecimal amountToBePaid = paymentOrder.getAmount(); // 결제 되어야하는 금액
        if (isAmountEqual(amount, amountToBePaid)) {
            handlePaymentStatus(payment, paymentOrder);
        } else {
            log.warn("결제 금액 불일치 merchantUid = {} , amount = {} , amountToBePaid = {}", merchantUid, amount,
                amountToBePaid);
            throw new RuntimeException("위조된 결제 시도");
        }
    }

    private void handlePaymentStatus(Response payment, PaymentOrder paymentOrder) {
        String status = payment.getResponse().getStatus();
        String merchantUid = payment.getResponse().getMerchantUid();
        switch (status) {
            case "ready":
                // 가상 계좌가 발급된 상태입니다.
                // 계좌 정보를 이용해 원하는 로직을 구성하세요.
                log.info("가상 계좌 발급 merchantUid = {}", merchantUid);
                paymentOrder.setStatus(PaymentOrderStatus.READY);
                // DB에 가상계좌 발급 정보 저장
                String vbankName = payment.getResponse().getVbankName();
                String vbankNum = payment.getResponse().getVbankNum();
                String vbankHolder = payment.getResponse().getVbankHolder();
                String vbankCode = payment.getResponse().getVbankCode();
                Integer vbankDate = payment.getResponse().getVbankDate();
                paymentOrder.updateVbank(vbankCode, vbankName, vbankNum, vbankHolder, vbankDate);
                //TODO : 가상 계좌 발급 안내 문자메세지 발송
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
    }

    private boolean isAmountEqual(BigDecimal amount, BigDecimal amountToBePaid) {
        //결제 된 금액 === 결제 되어야 하는 금액
        return amount.compareTo(amountToBePaid) == 0;
    }

    private Response getPayment(String impUid, String accessToken) {
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

    @Transactional
    public void portoneWebhook(PaymentWebhookRequest request) {
        try {
            verifyPayment(request.getImpUid(), request.getMerchantUid());
        } catch (Exception e) {
            // 결제 검증에 실패했습니다.
            log.error("결제 검증 실패 merchantUid = {} , {}", request.getMerchantUid(), e);
            throw new RuntimeException("Payment verification failed", e);
        }
    }
}