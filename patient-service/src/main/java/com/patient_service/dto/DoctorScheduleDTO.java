package com.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class DoctorScheduleDTO {
    private String appointmentDateId;
    private LocalDate date;
    private long fee;
    private List<TimeSlotDTO> timeSlots;

    public DoctorScheduleDTO(String appointmentDateId, LocalDate date, long fee, List<TimeSlotDTO> timeSlots) {
        this.appointmentDateId = appointmentDateId;
        this.date = date;
        this.fee = fee;
        this.timeSlots = timeSlots;
    }
}
