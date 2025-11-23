package com.booking_service.clients;

import com.booking_service.clients.fallback.DoctorClientFallback;
import com.booking_service.config.FeignClientConfig;
import com.booking_service.dto.DoctorBookingResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "DOCTOR-SERVICE", configuration = FeignClientConfig.class, fallback = DoctorClientFallback.class)
public interface DoctorClient {
    @GetMapping("/api/v1/doctors/getDoctorDetailsForBooking")
    public DoctorBookingResponseDto validateSchedule(
            @RequestParam String doctorId,
            @RequestParam String appointmentDateId,
            @RequestParam long fee,
            @RequestParam String timeSlotId
    );

}
