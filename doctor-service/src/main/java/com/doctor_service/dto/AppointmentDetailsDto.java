package com.doctor_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class AppointmentDetailsDto {

    private LocalDate date;
    private long fee;
    private List<LocalTime> timeSlots;

}
