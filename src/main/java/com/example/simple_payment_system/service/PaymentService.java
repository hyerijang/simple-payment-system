package com.example.simple_payment_system.service;

import com.example.simple_payment_system.PaymentRepository;
import com.example.simple_payment_system.domain.payment.order.PaymentOrder;
import com.example.simple_payment_system.domain.payment.order.PaymentOrderStatus;
import com.example.simple_payment_system.dto.PaymentOrderUpdateRequest;
import com.example.simple_payment_system.dto.PaymentWebhookRequest;
import com.example.simple_payment_system.dto.Response;
import com.example.simple_payment_system.dto.VbankInfo;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentOrderService paymentOrderService;
    private final PortOneService portOneService;

    @Transactional
    public void complete(PaymentOrderUpdateRequest result) {
        try {
            verifyPayment(result.getImpUid(), result.getMerchantUid());
        } catch (Exception e) {
            // 결제 검증에 실패했습니다.
            log.error("결제 검증 실패 merchantUid = {} , {}", result.getMerchantUid(), e);
            throw new RuntimeException("Payment verification failed");
        }
    }

    private void verifyPayment(String impUid, String merchantUid) {

        // 1. 포트원 API 엑세스 토큰 발급
        String accessToken = portOneService.getAccessToken().block();

        // 2. 포트원 결제내역 단건조회 API 호출
        Response payment = portOneService.getPayment(impUid, accessToken);

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
                paymentOrder.setStatus(PaymentOrderStatus.READY);
                // DB에 가상계좌 발급 정보 저장
                updateVbank(payment, paymentOrder);
                //가상 계좌 발급 안내 문자메세지 발송
                sendSMS();
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

    private void sendSMS() {
        // TODO : 가상계좌 발급 안내 문자메세지 발송
    }

    private void updateVbank(Response payment, PaymentOrder paymentOrder) {
        VbankInfo vbankInfo = VbankInfo.builder()
            .vbankCode(payment.getResponse().getVbankCode())
            .vbankName(payment.getResponse().getVbankName())
            .vbankNum(payment.getResponse().getVbankNum())
            .vbankHolder(payment.getResponse().getVbankHolder())
            .vbankDate(payment.getResponse().getVbankDate())
            .build();
        paymentOrder.updateVbank(vbankInfo);
    }

    private boolean isAmountEqual(BigDecimal amount, BigDecimal amountToBePaid) {
        //결제 된 금액 === 결제 되어야 하는 금액
        return amount.compareTo(amountToBePaid) == 0;
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