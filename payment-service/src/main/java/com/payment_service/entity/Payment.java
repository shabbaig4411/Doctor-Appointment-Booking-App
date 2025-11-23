package com.payment_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;

    private String bookingId;
    private String patientId;
    private long amount;
    private String currency;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentGatewayId;   // Razorpay/Stripe order ID

    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}

