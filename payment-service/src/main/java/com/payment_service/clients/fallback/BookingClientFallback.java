package com.payment_service.clients.fallback;

import com.payment_service.clients.BookingClient;
import com.payment_service.entity.PaymentStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class BookingClientFallback implements BookingClient {

    @Override
    public String updatePaymentStatus(String bookingId, PaymentStatus paymentStatus) {
        System.out.println("⚠ BOOKING-SERVICE DOWN — Returning fallback response"+ LocalDateTime.now().toString());
    return "Payment Failed!!!";
    }
}
