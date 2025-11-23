package com.booking_service.service;

import com.booking_service.clients.DoctorClient;
import com.booking_service.clients.PaymentClient;
import com.booking_service.dto.*;
import com.booking_service.entity.Booking;
import com.booking_service.entity.BookingStatus;
import com.booking_service.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DoctorClient doctorClient;
    private final PaymentClient paymentClient;

    public BookingService(BookingRepository bookingRepository,
                          DoctorClient doctorClient,
                          PaymentClient paymentClient) {
        this.bookingRepository = bookingRepository;
        this.doctorClient = doctorClient;
        this.paymentClient = paymentClient;
    }

    public BookingResponseDTO createBooking(BookingRequestDTO request, String patientId) {

        // 1️⃣ Validate schedule using Doctor Service
        DoctorBookingResponseDto validation = doctorClient.validateSchedule(
                request.getDoctorId(),
                request.getAppointmentDateId(),
                request.getFee(),
                request.getTimeSlotId()
        );
        System.out.println(validation.getMessage());
        System.out.println("🎉🎉🎉🎉🎉🎉 " + validation.isAvailable());
        if (validation == null || !validation.isAvailable()) {
            throw new RuntimeException("Selected date/slot is not available!");
        }

        // 2️⃣ Create Booking
        try {
            Booking booking = new Booking();
            booking.setPatientId(patientId);
            booking.setDoctorId(request.getDoctorId());
            booking.setAppointmentDateId(request.getAppointmentDateId());
            booking.setTimeSlotId(request.getTimeSlotId());
            booking.setFee(validation.getFee());
            booking.setStatus(BookingStatus.PENDING);


            // Generate-PaymentUrl Request
            PaymentRequestDTO paymentReq = new PaymentRequestDTO();
            paymentReq.setBookingId(ULIDGenerator.generate());
            paymentReq.setAmount(request.getFee());
            paymentReq.setPatientId(patientId);
            paymentReq.setCurrency(request.getCurrency());
            PaymentResponseDTO paymentUrlRequest = paymentClient.getCheckOutUrlToPay(paymentReq);

            booking.setBookingId(paymentUrlRequest.getBookingId());
            booking.setPaymentId(paymentUrlRequest.getPaymentId());
            booking.setPaymentUrl(paymentUrlRequest.getPaymentLink());
            booking.setCreatedDate(LocalDate.now());
            Booking saved = bookingRepository.save(booking);

            BookingResponseDTO response = new BookingResponseDTO();
            response.setBookingId(saved.getBookingId());
            response.setPatientId(saved.getPatientId());
            response.setDoctorId(saved.getDoctorId());
            response.setAppointmentDateId(saved.getAppointmentDateId());
            response.setTimeSlotId(saved.getTimeSlotId());
            response.setFee(saved.getFee());
            response.setStatus(saved.getStatus());
            response.setPaymentUrl(paymentUrlRequest
                    .getPaymentLink() != null ? paymentUrlRequest
                    .getPaymentLink() : "Payment URL Generation is Failed!!!");
            System.out.println("🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉🎉 Payment and Booking response Successfull");
            return response;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());

        }
        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId("Failed To Create Booking");
        response.setPatientId(patientId);
        response.setDoctorId(request.getDoctorId());
        response.setAppointmentDateId(request.getAppointmentDateId());
        response.setTimeSlotId(request.getTimeSlotId());
        response.setFee(request.getFee());
        response.setStatus(BookingStatus.FAILED);
        response.setPaymentUrl("Payment URL Generation Failed!!!");
        return response;

    }


    public String updatePaymentStatus(String bookingId, BookingStatus paymentStatus) {

        Optional<Booking> booking = bookingRepository.findById(bookingId);

        booking.get().setStatus(paymentStatus);
        bookingRepository.save(booking.get());
        return "Appointment Booked";
    }
}
