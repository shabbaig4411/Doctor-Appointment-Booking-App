package com.patient_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private final static String SECRET_KEY = "INVISIBLE";  // SAME as auth-service
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET_KEY);
    }
    // Extract entire JWT
    public DecodedJWT decode(String token) {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token);
    }
    // Extract userId from subject
    public String extractUserId(String token) {
        return decode(token).getSubject();
    }

    // Extract role from claim
    public String extractRole(String token) {
        return decode(token).getClaim("role").asString();
    }
}


