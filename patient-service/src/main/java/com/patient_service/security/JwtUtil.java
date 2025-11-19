//package com.patient_service.security;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import com.auth0.jwt.interfaces.JWTVerifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    private final Algorithm algorithm;
//    private final JWTVerifier verifier;
//    private final long expirationMs;
//
//    public JwtUtil(@Value("${jwt.secret}") String secret,
//                   @Value("${jwt.expiration-ms}") long expirationMs) {
//        // secret must be sufficiently long (recommended from env)
//        this.algorithm = Algorithm.HMAC256(secret);
//        this.verifier = JWT.require(algorithm).build();
//        this.expirationMs = expirationMs;
//    }
//
//    public String generateToken(String subject) {
//        Date now = new Date();
//        Date exp = new Date(now.getTime() + expirationMs);
//
//        return JWT.create()
//                .withSubject(subject)
//                .withIssuedAt(now)
//                .withExpiresAt(exp)
//                .sign(algorithm);
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            verifier.verify(token);
//            return true;
//        } catch (Exception ex) {
//            return false;
//        }
//    }
//
//    public String extractSubject(String token) {
//        DecodedJWT decoded = verifier.verify(token);
//        return decoded.getSubject();
//    }
//}
