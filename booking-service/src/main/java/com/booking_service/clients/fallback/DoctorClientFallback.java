package com.booking_service.clients.fallback;

import com.booking_service.clients.DoctorClient;
import com.booking_service.dto.DoctorBookingResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DoctorClientFallback implements DoctorClient {

    @Override
    public DoctorBookingResponseDto validateSchedule(String doctorId, String appointmentDateId, long fee, String timeSlotId) {
        System.out.println("⚠ Doctor-Service DOWN — Returning fallback response");

        return DoctorBookingResponseDto.builder()
                .available(false)
                .build();
    }
}
