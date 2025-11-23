package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDTO {

    private String doctorId;
    private String appointmentDateId;
    private String timeSlotId;
    private long fee;
    private String currency;
}
