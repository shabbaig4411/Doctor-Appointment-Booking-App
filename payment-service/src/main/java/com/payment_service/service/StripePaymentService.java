package com.payment_service.service;

import com.payment_service.dto.PaymentRequestDTO;
import com.payment_service.dto.PaymentResponseDTO;
import com.payment_service.entity.Payment;
import com.payment_service.entity.PaymentStatus;
import com.payment_service.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StripePaymentService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    private final PaymentRepository paymentRepository;

    StripePaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    //stripe -API
    //-> productName , amount , quantity , currency
    //-> return sessionId and url For payment

    public PaymentResponseDTO checkOutUrlForPayment(PaymentRequestDTO paymentRequestDTO) {
        // Set your secret key. Remember to switch to your live secret key in production!
        Stripe.apiKey = secretKey;

        Payment payment = new Payment();
        payment.setPaymentId(ULIDGenerator.generate());
        payment.setBookingId(paymentRequestDTO.getBookingId());
        payment.setPatientId(paymentRequestDTO.getPatientId());
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setCurrency(paymentRequestDTO.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);

        // Create a PaymentIntent with the order amount and currency
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(paymentRequestDTO.getBookingId())
                        .build();

        // Create new line item with the above product data and associated price
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(paymentRequestDTO.getCurrency() != null ? paymentRequestDTO.getCurrency() : "INR")
                        .setUnitAmount(paymentRequestDTO.getAmount() * 100)
                        .setProductData(productData)
                        .build();

        // Create new line item with the above price data
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        // Create new session with the line items
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:5555/payments/api/v1/payments/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:5555/payments/api/v1/payments/cancel?session_id={CHECKOUT_SESSION_ID}")
                        .addLineItem(lineItem)
                        .putMetadata("bookingId", paymentRequestDTO.getBookingId()) // put bookingId in url to updates after transaction
                        .build();

        // Create new session
        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            System.out.println("❌❌❌❌❌❌  PAYMENT SESSION CREATION EXCEPTION OCCURRED!!  ❌❌❌❌❌❌❌");
        }
        if (session != null) {
            payment.setMessage("session has been created");
            payment.setPaymentGatewayId(session.getId());
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            Payment saved = paymentRepository.save(payment);

            return PaymentResponseDTO
                    .builder()
                    .paymentId(saved.getPaymentId())
                    .bookingId(saved.getBookingId())
                    .amount(saved.getAmount())
                    .sessionId(session.getId())
                    .paymentLink(session.getUrl())
                    .build();
        } else {
            return PaymentResponseDTO
                    .builder()
                    .paymentId("Payment Id Not Generated!!!")
                    .bookingId(payment.getBookingId())
                    .amount(payment.getAmount())
                    .sessionId("Session Id Not Generated!!!")
                    .paymentLink("Failed To Generate Payment URL!!!")
                    .build();
        }
    }


    public void markPaymentSuccessful(String sessionId) {

        Payment payment = paymentRepository.findByPaymentGatewayId(sessionId);

        if (payment != null) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setMessage("Payment Completed Successfully");
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
        }
    }



}

