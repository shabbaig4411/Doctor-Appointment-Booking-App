package com.booking_service.client;

import com.booking_service.dto.Doctor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "doctor-service", url = "http://localhost:8082/api/v1/doctors")
public interface DoctorClient {
    @GetMapping("/getDoctorById")
    public Doctor getDoctorById(
            @RequestParam("id") long doctorId
    );

}
