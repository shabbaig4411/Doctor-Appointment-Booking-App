package com.patient_service.clients;

import com.patient_service.clients.fallback.DoctorClientFallback;
import com.patient_service.dto.DoctorResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "DOCTOR-SERVICE", fallback = DoctorClientFallback.class, url = "http://localhost:8082/api/v1/doctors")
public interface DoctorClient {

    @GetMapping("/searchDoctors")
    List<DoctorResponseDTO> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String city
    );
}
