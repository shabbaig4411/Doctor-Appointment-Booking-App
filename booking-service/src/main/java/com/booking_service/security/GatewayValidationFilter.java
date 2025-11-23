package com.booking_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("path: " + path);
        if (
                path.startsWith("/api/v1/bookings/updatePaymentStatus") ||
                        path.startsWith("/api/v1/bookings/anyUpdateFromPaymentGatewayClientSide")
        ) {
            // Check if session_id exists
            String bookingId = request.getParameter("bookingId");
            System.out.println("🎉🎉🎉🎉🎉🎉🎉🎉 🎉" + bookingId);
            if (bookingId == null || bookingId.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Missing session_id");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        // If missing → request bypassed API-Gateway
        if (userId == null || role == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Request did not pass through API Gateway Or\n  Unauthorized: Missing identity headers");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

