package com.example.simple_payment_system.controller;

import com.example.simple_payment_system.dto.PaymentOrderCancelRequest;
import com.example.simple_payment_system.dto.PaymentOrderUpdateRequest;
import com.example.simple_payment_system.dto.PaymentWebhookRequest;
import com.example.simple_payment_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;


    // 결제 완료 처리
    @PostMapping("/api/v1/payment/complete")
    public ResponseEntity<Void> complete(@RequestBody PaymentOrderUpdateRequest paymentOrderUpdateRequest) {
        paymentService.complete(paymentOrderUpdateRequest);
        return ResponseEntity.ok().build();
    }

    // 웹훅 처리
    @PostMapping("/api/v1/portone-webhook")
    public ResponseEntity<Void> portoneWebhook(@RequestBody PaymentWebhookRequest webhookRequest) {
        paymentService.portoneWebhook(webhookRequest);
        return ResponseEntity.ok().build();
    }

    // 결제 취소 처리
    @PostMapping("/api/v1/payment/cancel")
    public ResponseEntity<Void> cancel(@RequestBody PaymentOrderCancelRequest orderCancelRequest) {
        paymentService.cancel(orderCancelRequest);
        return ResponseEntity.ok().build();
    }


}
