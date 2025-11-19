package com.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class DoctorScheduleDTO {
    private LocalDate date;
    private List<LocalTime> timeSlots;

    public DoctorScheduleDTO(LocalDate date, List<LocalTime> timeSlots) {
        this.date = date;
        this.timeSlots = timeSlots;
    }
}
