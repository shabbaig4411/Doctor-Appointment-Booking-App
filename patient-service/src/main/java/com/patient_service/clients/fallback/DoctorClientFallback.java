package com.patient_service.clients.fallback;

import com.patient_service.clients.DoctorClient;
import com.patient_service.dto.DoctorResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DoctorClientFallback implements DoctorClient {

    @Override
    public List<DoctorResponseDTO> searchDoctors(String specialization, String city) {
        System.out.println("⚠ Doctor-Service DOWN — Returning fallback response");

        return Collections.emptyList();
    }
}
