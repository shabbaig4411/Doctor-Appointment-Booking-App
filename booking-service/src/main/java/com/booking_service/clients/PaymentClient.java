package com.booking_service.clients;

import com.booking_service.clients.fallback.PaymentClientFallback;
import com.booking_service.config.FeignClientConfig;
import com.booking_service.dto.PaymentRequestDTO;
import com.booking_service.dto.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE",configuration = FeignClientConfig.class, fallback = PaymentClientFallback.class)
public interface PaymentClient {

    @PostMapping("/api/v1/payments/generatePaymentUrl")
    public PaymentResponseDTO getCheckOutUrlToPay(@RequestBody PaymentRequestDTO request);
}
