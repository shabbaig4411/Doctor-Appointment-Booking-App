package com.doctor_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "time_slots")
public class TimeSlots {
    @Id
    @Column(name = "timeslot_id", nullable = false, unique = true)
    private String timeslotId;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_date_id")
    @JsonBackReference
    private AppointmentDate appointmentDate;
}