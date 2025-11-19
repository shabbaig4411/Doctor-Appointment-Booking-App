package com.booking_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class DoctorAppointmentSchedule {
    private Long id;

    private LocalDate date;

    private Set<TimeSlots> timeSlots = new LinkedHashSet<>();

    private Doctor doctor;



}