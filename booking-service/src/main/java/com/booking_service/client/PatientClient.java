package com.booking_service.client;

import com.booking_service.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "patient-service", url = "http://localhost:8083/api/v1/patients")
public interface PatientClient {

    @GetMapping("/getById")
    public Patient getPatientById(@RequestParam("id") long patientId);

}
