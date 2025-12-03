package com.doctor_service.rest;

import com.doctor_service.dto.AppointmentDetailsDto;
import com.doctor_service.dto.DoctorBookingResponseDto;
import com.doctor_service.dto.DoctorDto;
import com.doctor_service.dto.DoctorResponseDTO;
import com.doctor_service.entity.AppointmentDate;
import com.doctor_service.entity.Doctor;
import com.doctor_service.repository.DoctorRepository;
import com.doctor_service.service.DoctorService;
import com.doctor_service.security.JwtService;
import jakarta.validation.Valid;
import jdk.jfr.Frequency;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/v1/doctors")
@Validated
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;
    private JwtService jwtService;

    public DoctorController(DoctorService doctorService, DoctorRepository doctorRepository, JwtService jwtService) {
        this.doctorService = doctorService;
        this.doctorRepository = doctorRepository;
    }

    // http://localhost:5555/doctors/api/v1/doctors/saveDoctorProfile
    @PostMapping("/saveDoctorProfile")
    public ResponseEntity<Map<String, Object>> saveDoctorProfile(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") String doctorId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody DoctorDto doctorDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "Bad Request!!");
            response.put("message", "Doctor Failed to save!!!");
            response.put("data", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!role.equalsIgnoreCase("DOCTOR")) {
            throw new RuntimeException("Access Denied – Only doctor can create profile");
        }
        Doctor savedDoctor = doctorService.saveDoctor(doctorDto, doctorId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Doctor saved successfully");
        response.put("data", savedDoctor);
        return ResponseEntity.ok(response);
    }

    // http://localhost:5555/doctors/api/v1/doctors/saveDoctorImage
    @PostMapping("/saveDoctorImage")
    public ResponseEntity<Map<String, Object>> saveDoctorImage(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") String doctorId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam("file") MultipartFile file
    ) {

//        System.out.println("🎉🎉🎉🎉 THis is Image Uploader");
        String doctorImageUrl = doctorService.saveDoctorImage(doctorId, file);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Doctor Image saved successfully");
        response.put("data", doctorImageUrl);
        return ResponseEntity.ok(response);


    }

    // http://localhost:5555/doctors/api/v1/doctors/saveAppointmentDetails
    @PostMapping("/saveAppointmentDetails")
    public ResponseEntity<?> saveAppointmentDetails(
            @RequestBody @Valid AppointmentDetailsDto request,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") String doctorId,
            @RequestHeader("X-User-Role") String role,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "Bad Request!!");
            response.put("message", "Appointment Schedule Failed to save!!!");
            response.put("data", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // Check ONLY doctors can update data
        if (!role.equalsIgnoreCase("DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied – Only DOCTORS can create Schedules.");
        }
        AppointmentDate saved = doctorService.saveAppointmentDetails(request, doctorId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Appointment saved successfully");
        response.put("data", saved);
        return ResponseEntity.ok(response);
    }


    // http://localhost:5555/doctors/api/v1/doctors/searchDoctors?specialization=cardiologist&city=bengaluru
    @GetMapping("/searchDoctors")
    public ResponseEntity<List<?>> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String city,
            @RequestHeader("Authorization") String patientToken,
            @RequestHeader("X-User-Id") String patientId,
            @RequestHeader("X-User-Role") String role
    ) {

        // 1. Use JPQL Query(1) to get only future dates and timeSlots with dto's and service layer.
        List<DoctorResponseDTO> result =
                doctorService.searchDoctorWithAvailability(specialization, city);

        // 2. Write logic Here to filter future dates and timeSlots & use Query(2).
        /**       List<Doctor> allDoctors = doctorRepository.findBySpecializationAndArea(specialization, areaName);
         //
         //         List<Doctor> result = new ArrayList<>();
         //         // instead of sending List<Doctor>Entity Response like this,
         //         //we recommend to create ResponseDTO then send List<ResponseDTO> as Response.
         //
         //         for (Doctor doctor : allDoctors) {
         //         List<AppointmentDate> resultSchedule = new ArrayList<>();
         //
         //         List<AppointmentDate> appointmentDates = doctor.getAppointmentSchedules();
         //         // if appointmentsDates future date get timeSlots or if today get future timeSlots
         //
         //         for (AppointmentDate appointmentDate : appointmentDates) {
         //         Set<TimeSlots> resultTimeSlots = new HashSet<>();
         //
         //         Set<TimeSlots> timeSlots = appointmentDate.getTimeSlots();
         //         LocalDate date1 = appointmentDate.getDate();
         //         LocalDate today = LocalDate.now();
         //         if (date1.isAfter(today)) {
         //         // add all timeSlots to show Response
         //         resultTimeSlots.addAll(timeSlots);
         //         } else if (date1.isEqual(today)) {
         //         for (TimeSlots ts : timeSlots) {
         //         LocalTime now = LocalTime.now();
         //         if (ts.getTime().isAfter(now)) {
         //         // add timeSlot to show for response
         //         resultTimeSlots.add(ts);
         //         }
         //         }
         //         }
         //         if (!resultTimeSlots.isEmpty()) {
         //         AppointmentDate newSchedule = new AppointmentDate();
         //         newSchedule.setId(appointmentDate.getId());
         //         newSchedule.setDate(date1);
         //         newSchedule.setTimeSlots(resultTimeSlots);
         //         resultSchedule.add(newSchedule);
         //         }
         //         }
         //         // Add doctor ONLY if they have at least 1 valid timeslot
         //         if (!resultSchedule.isEmpty()) {
         //         doctor.setAppointmentSchedules(resultSchedule);
         //         result.add(doctor);
         //         }
         //         }
         //         System.out.println("Doctor is Not available: " + result.isEmpty());
         //
         //         // It is bad way of sending the response like Below List<Doctor> Entity, Use DTO then send
         //         // Don't Send Entities Directly Use DTOs
         //
         //         */

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // http://localhost:5555/doctors/api/v1/doctors/getDoctorDetailsForBooking
    @GetMapping("/getDoctorDetailsForBooking")
    public DoctorBookingResponseDto validateSchedule(
            @RequestHeader("Authorization") String patientToken,
            @RequestHeader("X-User-Id") String patientId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam String doctorId,
            @RequestParam String appointmentDateId,
            @RequestParam long fee,
            @RequestParam String timeSlotId
    ) {
        if (!role.equalsIgnoreCase("PATIENT")) {
            return DoctorBookingResponseDto.builder()
                    .message("INVALID PATIENT!!!").build();
        }
        return doctorService.verifyDoctorSchedule(doctorId, appointmentDateId, fee, timeSlotId);
    }


    // http://localhost:5555/doctors/api/v1/doctors/getDoctorById?id=1
    @GetMapping("/getDoctorById")
    public Doctor getDoctorById(
            @RequestParam String id

    ) {
        return doctorRepository.findById(id).get();
    }


}

