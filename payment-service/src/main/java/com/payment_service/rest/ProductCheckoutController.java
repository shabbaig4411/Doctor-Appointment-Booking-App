package com.payment_service.rest;

import com.payment_service.clients.BookingClient;
import com.payment_service.dto.PaymentRequestDTO;
import com.payment_service.dto.PaymentResponseDTO;
import com.payment_service.entity.PaymentStatus;
import com.payment_service.service.StripePaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class ProductCheckoutController {

    @Value("${stripe.secretKey}")
    private String secretKey;

    private final StripePaymentService stripePaymentService;
    private final BookingClient bookingClient;

    public ProductCheckoutController(
            StripePaymentService stripePaymentService,
            BookingClient bookingClient) {
        this.stripePaymentService = stripePaymentService;
        this.bookingClient = bookingClient;
    }

    // http://localhost:5555/payments/api/v1/payments/generatePaymentUrl
    @PostMapping("/generatePaymentUrl")
    public PaymentResponseDTO getCheckOutUrlToPay(
            @RequestBody PaymentRequestDTO paymentRequestDTO
    ) {
        System.out.println("🎉🎉🎉🎉🎉🎉 Payment Service Controller ");
        PaymentResponseDTO paymentResponseDto = stripePaymentService.checkOutUrlForPayment(paymentRequestDTO);
        return paymentResponseDto;
    }

    // http://localhost:5555/payments/api/v1/payments/success
    @GetMapping("/success")
    public String handleSuccess(@RequestParam("session_id") String sessionId) {
        Stripe.apiKey = secretKey; // Replace with your actual secret key

        try {
            Session session = Session.retrieve(sessionId);
            String status = session.getPaymentStatus();
            String bookingId = session.getMetadata().get("bookingId");

            if (status != null && status.equalsIgnoreCase("paid")) {
                System.out.println(status); // paid
                System.out.println("✅ Payment successful: true");

                // Update Payment table
                stripePaymentService.markPaymentSuccessful(sessionId);

                System.out.println("🎉🎉🎉🎉🎉🎉🎉 Booking Id :  " + bookingId);
                // Inform Booking-Service
              return bookingClient.updatePaymentStatus(bookingId, PaymentStatus.CONFIRMED);

            } else {
                System.out.println("❌ Payment not completed: false");
            }

        } catch (StripeException e) {
            e.printStackTrace();
        }
        return "Payment Failed!!!";
    }

    // http://localhost:5555/payments/api/v1/payments/cancel
    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {

        System.out.println("❌ Payment cancelled: false");
        return ResponseEntity.ok("Payment cancelled");
    }


}






