package com.patient_service.rest;

import com.patient_service.clients.DoctorClient;
import com.patient_service.dto.DoctorResponseDTO;
import com.patient_service.dto.PatientDto;
import com.patient_service.entity.Patient;
import com.patient_service.repository.PatientRepository;
import com.patient_service.security.JwtService;
import com.patient_service.service.PatientService;
//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    // http://localhost:5555/patients/api/v1/patients

    private final PatientRepository patientRepository;
    private final DoctorClient doctorClient;
    private final PatientService patientService;
    private final JwtService jwtService;

    public PatientController(DoctorClient doctorClient,
                             PatientRepository patientRepository,
                             PatientService patientService,
                             JwtService jwtService
    ) {
        this.doctorClient = doctorClient;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.jwtService = jwtService;
    }


    // http://localhost:5555/patients/api/v1/patients/saveProfile
    @PostMapping("/saveProfile")
    public ResponseEntity<?> savePatientProfile(
            @Valid @RequestBody PatientDto dto,
//            @RequestHeader("Authorization") String token
            @RequestHeader("X-User-Id") String patientId,
            @RequestHeader("X-User-Role") String role
    ) {
        /**   System.out.println(token);
         String tokenExtracted = token.startsWith("Bearer ")
         ? token.substring(7)
         : token;
         String userId = jwtService.extractUserId(tokenExtracted);
         String role = jwtService.extractRole(tokenExtracted);
         */

        // Check ONLY patient can update profile
        if (!role.equalsIgnoreCase("PATIENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied – Only patient can create profile");
        }

        Patient saved = patientService.savePatient(dto, patientId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Patient profile saved successfully",
                "data", saved
        ));
    }


    // http://localhost:5555/patients/api/v1/patients/searchDoctors?specialization=cardiologist&city=bengaluru
    @GetMapping("/searchDoctors")
    public ResponseEntity<List<?>> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String city,
            @RequestHeader("X-User-Id") String patientId,
            @RequestHeader("X-User-Role") String role
    ) {
        // 1️⃣ Verify patient role
        if (!role.equalsIgnoreCase("PATIENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
        List<DoctorResponseDTO> result = doctorClient.searchDoctors(specialization, city);
        return ResponseEntity.ok(result);
    }


    // http://localhost:5555/patients/api/v1/patients/getById?id=1
    @GetMapping("/getById")
    public Patient getPatientById(@RequestParam long id) {
        // use service layers to interact with repository layer
        return patientRepository.findById(id).get();
    }


}
