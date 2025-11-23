package com.payment_service.repository;

import com.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Payment findByPaymentGatewayId(String sessionId);


}