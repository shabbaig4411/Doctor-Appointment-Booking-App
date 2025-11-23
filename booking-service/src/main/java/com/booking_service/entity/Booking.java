package com.booking_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(name = "booking_id", nullable = false, unique = true)
    private String bookingId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "doctor_id", nullable = false)
    private String doctorId;

    @Column(nullable = false)
    private String appointmentDateId;

    @Column(nullable = false)
    private String timeSlotId;

    @Column(nullable = false)
    private long fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name="payment_id", nullable = false,unique = true)
    private String paymentId;

    @Column(name="payment_url", nullable = false,unique = true,columnDefinition = "TEXT")
    private String paymentUrl;

    @Column(nullable = false)
    private LocalDate createdDate = LocalDate.now();



}