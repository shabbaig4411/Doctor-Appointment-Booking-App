package com.payment_service.clients;

import com.payment_service.clients.fallback.BookingClientFallback;
import com.payment_service.config.FeignClientConfig;
import com.payment_service.entity.PaymentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BOOKING-SERVICE", configuration = FeignClientConfig.class, fallback = BookingClientFallback.class)
public interface BookingClient {

    @PutMapping("/api/v1/bookings/updatePaymentStatus")
    String updatePaymentStatus(
            @RequestParam String bookingId,
            @RequestParam PaymentStatus paymentStatus
    );
}
