package com.example.simple_payment_system.controller;

import com.example.simple_payment_system.dto.PaymentOrderSaveRequest;
import com.example.simple_payment_system.service.PaymentOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class PaymentOrderController {

    private  final PaymentOrderService paymentOrderService;

    @PostMapping("/api/v1/payment-order")
    public ResponseEntity<String> save(@RequestBody PaymentOrderSaveRequest request) {
        String merchantUid = paymentOrderService.create(request);
        return ResponseEntity.ok(merchantUid);
    }
}
