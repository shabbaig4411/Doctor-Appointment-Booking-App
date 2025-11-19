package com.payment_service.rest;

import com.payment_service.dto.ProductRequest;
import com.payment_service.dto.StripeResponse;
import com.payment_service.service.StripeService;
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

    // http://localhost:8085/api/v1/payments
    private final StripeService stripeService;

    @Value("${stripe.secretKey}")
    private String secretKey;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    // http://localhost:8085/api/v1/payments/checkout
    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
//        System.out.println(stripeResponse.getSessionId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    // http://localhost:8085/api/v1/payments/success
    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("session_id") String sessionId) {
        Stripe.apiKey = secretKey; // Replace with your actual secret key

        try {
//            System.out.println(sessionId);
            Session session = Session.retrieve(sessionId);
            String paymentStatus = session.getPaymentStatus();

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                System.out.println(paymentStatus); // paid
                System.out.println("✅ Payment successful: true");
                // update payment status as true fron here using feign
                return ResponseEntity.ok("Payment successful");
            } else {
                System.out.println("❌ Payment not completed: false");
                return ResponseEntity.status(400).body("Payment not completed");
            }

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Stripe error occurred");
        }
    }

    // http://localhost:8085/api/v1/payments/cancel
    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {

        System.out.println("❌ Payment cancelled: false");
        return ResponseEntity.ok("Payment cancelled");
    }


}






