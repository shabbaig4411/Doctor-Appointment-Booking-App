package com.doctor_service.clients.fallback;

import com.doctor_service.clients.AuthClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class AuthClientFallback implements AuthClient {

    @Override
    public String getDoctorNameById(String doctorId) {
        System.out.println("⚠ AUTH-SERVICE DOWN — Returning fallback response"+ LocalDateTime.now().toString());
        return Collections.emptyList().toString();
    }
}
