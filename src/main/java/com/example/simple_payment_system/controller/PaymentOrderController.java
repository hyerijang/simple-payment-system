package com.example.simple_payment_system.controller;

import com.example.simple_payment_system.dto.PaymentOrderResponse;
import com.example.simple_payment_system.dto.PaymentOrderSaveRequest;
import com.example.simple_payment_system.service.PaymentOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    @PostMapping("/api/v1/payment-order")
    public ResponseEntity<String> save(@RequestBody PaymentOrderSaveRequest request) {
        String merchantUid = paymentOrderService.create(request);
        return ResponseEntity.ok(merchantUid);
    }

    @GetMapping("/api/v1/payment-orders")
    public ResponseEntity<List<PaymentOrderResponse>> getAllOrders() {
        List<PaymentOrderResponse> orders = paymentOrderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

}
