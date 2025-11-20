package com.patient_service.clients;

import com.patient_service.clients.fallback.DoctorClientFallback;
import com.patient_service.config.FeignClientConfig;
import com.patient_service.dto.DoctorResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "DOCTOR-SERVICE",configuration = FeignClientConfig.class, fallback = DoctorClientFallback.class)
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/searchDoctors")
    List<DoctorResponseDTO> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String city
    );
}
