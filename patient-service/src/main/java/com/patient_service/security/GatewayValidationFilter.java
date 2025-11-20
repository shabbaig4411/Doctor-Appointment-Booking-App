package com.patient_service.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.netflix.discovery.converters.Auto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayValidationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        /**       String token = request.getHeader("Authorization");
         String tokenExtracted = token.startsWith("Bearer ")
         ? token.substring(7)
         : token;

         DecodedJWT jwt = jwtService.decode(tokenExtracted);
         String userId = jwt.getSubject();
         String role = jwt.getClaim("role").asString();

         */

        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        // If missing → request bypassed API-Gateway
        if (userId == null || role == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Request did not pass through API Gateway");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

