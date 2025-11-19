package com.patient_service.rest;

import com.patient_service.clients.DoctorClient;
import com.patient_service.dto.DoctorResponseDTO;
import com.patient_service.entity.Patient;
import com.patient_service.repository.PatientRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    // http://localhost:8083/api/v1/patients

    @Autowired
    private PatientRepository patientRepository;

    private final DoctorClient doctorClient;

    public PatientController(DoctorClient doctorClient) {
        this.doctorClient = doctorClient;
    }

    @PostMapping("/register")
    public ResponseEntity<Patient> register(@Valid @RequestBody Patient patient) {
        Patient saved = patientRepository.save(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }


    // http://localhost:8083/api/v1/patients/searchDoctors?specialization=cardiologist&city=bengaluru
    @GetMapping("/searchDoctors")
    @CircuitBreaker(name = "doctorServiceCB", fallbackMethod = "doctorFallback")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String city) {
        List<DoctorResponseDTO> result = doctorClient.searchDoctors(specialization, city);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> doctorFallback(String specialization, String city, Throwable t) {
        return ResponseEntity.status(503).body(
                Map.of(
                        "message", "Doctor service unavailable. Please try later.",
                        "data", List.of()
                )
        );
    }

    // http://localhost:8083/api/v1/patients/getById?id=1
    @GetMapping("/getById")
    public Patient getPatientById(@RequestParam long id) {
        // use service layers to interact with repository layer
        return patientRepository.findById(id).get();
    }


}
