package com.booking_service.clients.fallback;

import com.booking_service.clients.DoctorClient;
import com.booking_service.clients.PaymentClient;
import com.booking_service.dto.DoctorBookingResponseDto;
import com.booking_service.dto.PaymentRequestDTO;
import com.booking_service.dto.PaymentResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallback implements PaymentClient {

    @Override
    public PaymentResponseDTO getCheckOutUrlToPay(PaymentRequestDTO request) {
        System.out.println("⚠ Payment-Service DOWN — Returning fallback response");

        return PaymentResponseDTO.builder()
                .bookingId(request.getBookingId())
                .paymentId(request.getPatientId())
                .sessionId("Session Id Creation Failed!!!")
                .build();
    }

}
