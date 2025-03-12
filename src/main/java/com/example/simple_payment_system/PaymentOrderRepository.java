package com.example.simple_payment_system;

import com.example.simple_payment_system.domain.payment.order.PaymentOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByMerchantUid(String merchantUid);
}
