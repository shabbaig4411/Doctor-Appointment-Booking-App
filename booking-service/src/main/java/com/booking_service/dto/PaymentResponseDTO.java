package com.booking_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentResponseDTO {
    private String paymentId;
    private String bookingId;
    private long amount;
    private String sessionId;
    private String paymentLink;
}
