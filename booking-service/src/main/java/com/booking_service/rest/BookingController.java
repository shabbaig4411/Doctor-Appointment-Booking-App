package com.booking_service.rest;

import com.booking_service.client.DoctorClient;
import com.booking_service.client.PatientClient;
import com.booking_service.dto.Doctor;
import com.booking_service.dto.Patient;
import com.booking_service.entity.BookingConfirmation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private DoctorClient doctorClient;

    @Autowired
    private PatientClient patientClient;

    // http://localhost:8084/api/v1/bookings/getDoctorAndPatient?doctorId=1&patientId=1
    @GetMapping("/getDoctorAndPatient")
    public String getDoctorById(
            @RequestParam long doctorId,
            @RequestParam long patientId
    ) {
        Patient p = patientClient.getPatientById(patientId);
        Doctor d = doctorClient.getDoctorById(doctorId);
        System.out.println(p.getName());
        System.out.println(d.getName());

        BookingConfirmation confirmation = new BookingConfirmation();
        confirmation.setDoctorName(d.getName());
        confirmation.setPatientName(p.getName());

        return p.getName()+" -Booked for Doctor "+d.getName();
    }

}
