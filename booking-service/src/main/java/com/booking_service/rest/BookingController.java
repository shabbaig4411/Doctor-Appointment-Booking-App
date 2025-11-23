package com.booking_service.rest;

import com.booking_service.dto.BookingRequestDTO;
import com.booking_service.dto.BookingResponseDTO;
import com.booking_service.entity.BookingStatus;
import com.booking_service.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // http://localhost:5555/bookings/api/v1/bookings/bookAppointment
    @PostMapping("/bookAppointment")
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestBody BookingRequestDTO request,
            @RequestHeader("Authorization") String patientToken,
            @RequestHeader("X-User-Id") String patientId,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equalsIgnoreCase("PATIENT")) {
            return ResponseEntity.status(403).build();
        }
        BookingResponseDTO response = bookingService.createBooking(request, patientId);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/updatePaymentStatus")
    String updatePaymentStatus(
            @RequestParam String bookingId,
            @RequestParam BookingStatus paymentStatus
    ) {
        System.out.println("🎉🎉🎉🎉🎉 UPDATE PAYMENT STATUS ");
        System.out.println(paymentStatus);
       return bookingService.updatePaymentStatus(bookingId, paymentStatus);

    }


}
