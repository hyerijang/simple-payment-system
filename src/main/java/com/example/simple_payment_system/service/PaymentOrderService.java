package com.example.simple_payment_system.service;

import com.example.simple_payment_system.PaymentOrderRepository;
import com.example.simple_payment_system.domain.order.PaymentOrder;
import com.example.simple_payment_system.domain.order.PaymentOrderStatus;
import com.example.simple_payment_system.dto.PaymentOrderSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class PaymentOrderService {
    private final PaymentOrderRepository paymentOrderRepository;

    public PaymentOrder findByMerchantUid(String merchantUid) {
        return paymentOrderRepository.findByMerchantUid(merchantUid)
            .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. merchantUid = " + merchantUid));
    }

    public String create(PaymentOrderSaveRequest request) {

        PaymentOrder paymentOrder = PaymentOrder.builder()
            .name(request.getName())
            .amount(request.getAmount())
            .merchantUid(request.getMerchantUid())
            .buyerId(request.getBuyerId())
            .status(PaymentOrderStatus.RESERVED)
            .build();

        paymentOrderRepository.save(paymentOrder);

        return paymentOrder.getMerchantUid();
    }
}
