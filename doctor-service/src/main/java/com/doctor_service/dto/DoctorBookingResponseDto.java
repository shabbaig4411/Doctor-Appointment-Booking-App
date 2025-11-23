package com.doctor_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// plain old java object pojo
@Getter
@Setter
@Builder
public class DoctorBookingResponseDto {

    private String doctorId;
    private long fee;
    private boolean available;
    private String message;

}
