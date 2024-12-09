package com.example.simple_payment_system.controller;

import com.example.simple_payment_system.dto.PaymentOrderResponse;
import com.example.simple_payment_system.service.PaymentOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class AdminContloler {

    private final PaymentOrderService paymentOrderService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 결제 페이지
    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }

    @GetMapping("/payment-order")
    public String paymentOrder(Model model) {
        List<PaymentOrderResponse> orders = paymentOrderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "payment_order";
    }


}
