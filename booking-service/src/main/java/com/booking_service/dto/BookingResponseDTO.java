package com.booking_service.dto;

import com.booking_service.entity.BookingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponseDTO {

    private String bookingId;
    private String patientId;
    private String doctorId;
    private String appointmentDateId;
    private String timeSlotId;
    private long fee;
    private BookingStatus status;
    private String paymentUrl;
}
