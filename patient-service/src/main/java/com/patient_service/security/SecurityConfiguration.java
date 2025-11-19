//package com.patient_service.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfiguration {
//
//    private final JwtUtil jwtUtil;
//
//    public SecurityConfiguration(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil);
//
//        http
//                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // PUBLIC APIs
//                        .requestMatchers("/api/v1/patients/register", "/api/v1/patients/login").permitAll()
//
//                        // EVERYTHING ELSE requires authentication
//                        .anyRequest().authenticated()
//                )
//                // Add JWT filter before UsernamePasswordAuthenticationFilter
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic(Customizer.withDefaults());
//        return http.build();
//    }
//}
