//package com.patient_service.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//
//    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest req,
//                                    HttpServletResponse res,
//                                    FilterChain chain)
//            throws ServletException, IOException {
//
//        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
//
//        // No token → move forward without authentication
//        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
//            chain.doFilter(req, res);
//            return;
//        }
//
//        String token = header.substring(7);
//
//        // If token invalid → ignore and continue (Spring will block later)
//        if (!jwtUtil.validateToken(token)) {
//            chain.doFilter(req, res);
//            return;
//        }
//
//        // Extract user (email)
//        String subject = jwtUtil.extractSubject(token); // email
//
//        // Create authentication
//        var auth = new UsernamePasswordAuthenticationToken(
//                subject,
//                null,
//                List.of(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//
//        // Set authenticated user
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        chain.doFilter(req, res);
//    }
//}
