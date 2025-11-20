package com.doctor_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "appointment_date")
public class AppointmentDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "appointmentDate",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<TimeSlots> timeSlots = new LinkedHashSet<>();

    @Column(name="fee", nullable = false)
    private float fee;



}