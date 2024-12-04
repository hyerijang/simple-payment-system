package com.example.simple_payment_system.controller;

import com.example.simple_payment_system.dto.PaymentOrderUpdateRequest;
import com.example.simple_payment_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 페이지
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    // 결제 페이지
    @GetMapping("/api/v1/payment")
    public String payment() {
        return "payment";
    }

    // 결제 완료 처리
    @PostMapping("/api/v1/payment/complete")
    public void complete(@RequestBody PaymentOrderUpdateRequest paymentOrderUpdateRequest) {
        paymentService.complete(paymentOrderUpdateRequest);
    }

    // Access Token 발급
    @GetMapping("/api/v1/token")
    public ResponseEntity<Void> getAccessToken() {
        String accessToken = paymentService.getAccessToken().block();
        log.info("Access Token = {}", accessToken);
        return ResponseEntity.ok().build();
    }

}
