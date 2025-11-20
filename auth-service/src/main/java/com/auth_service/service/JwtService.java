package com.auth_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final static String SECRET_KEY = "INVISIBLE";
    private static final long EXPIRATION_TIME = 86400000;// 1 day - in seconds
    //private static final String TOKEN_PREFIX = "Bearer ";

    public String generateToken(String userId, String role) {

        return JWT.create()
                .withSubject(userId)
                .withClaim("role",role)  //  if we need roll based login
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String validateTokenAndRetrieveSubject(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token)
                .getSubject();
    }


}
