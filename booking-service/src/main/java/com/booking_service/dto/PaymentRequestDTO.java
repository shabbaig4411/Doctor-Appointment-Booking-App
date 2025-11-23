package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {
    private String bookingId;
    private String patientId;
    private long amount;
    private String currency;

}
