package com.api_gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "INVISIBLE";

    // Public endpoints (NO TOKEN REQUIRED)
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/api/v1/register",
            "/auth/api/v1/login",
            "/auth/api/v1/forgotpassword",
            "/auth/api/v1/otp"
    );

    // Protected endpoints WITH REQUIRED ROLES
    private static final Map<String, List<String>> PROTECTED_ENDPOINTS = Map.of(
            "/doctors/api/v1/doctors/saveDoctorProfile", List.of("DOCTOR"),
            "/doctors/api/v1/doctors/saveAppointmentDetails", List.of("DOCTOR"),
            "/doctors/api/v1/doctors/searchDoctors?specialization=cardiologist&city=bengaluru", List.of("patient"),
            "/doctors/api/v1/doctors/getDoctorById?id=1", List.of("DOCTOR")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);

            String role = jwt.getClaim("role").asString();
            String userId = jwt.getSubject();  // USER ID from token

            if (!isAllowed(path, role)) {
                return forbidden(exchange);
            }

            // send role + userId to downstream microservices
            exchange = exchange.mutate()
                    .request(req -> req
                            .header("X-User-Role", role)
                            .header("X-User-Id", userId)
                    )
                    .build();

        } catch (JWTVerificationException e) {
            return unauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    // PUBLIC ENDPOINT CHECK
    private boolean isPublic(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    // ROLE BASED AUTHORIZATION CHECK
    private boolean isAllowed(String path, String role) {
        for (var entry : PROTECTED_ENDPOINTS.entrySet()) {
            String protectedPath = entry.getKey();
            List<String> allowedRoles = entry.getValue();

            if (path.startsWith(protectedPath)) {
                return allowedRoles.contains(role);
            }
        }
        return true;  // all unprotected endpoints → allowed
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
