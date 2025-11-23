package com.payment_service.dto;

import lombok.*;

@Data
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
