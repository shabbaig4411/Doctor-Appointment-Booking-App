package com.auth_service.config;

import com.auth_service.service.JwtFilter;
import com.auth_service.service.LoginUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppConfig {

    private LoginUserDetailsService loginUserDetailsService;
    private JwtFilter jwtFilter;

    public AppConfig(LoginUserDetailsService loginUserDetailsService, JwtFilter jwtFilter) {
        this.loginUserDetailsService = loginUserDetailsService;
        this.jwtFilter = jwtFilter;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(loginUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    String[] publicEndpoints = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/forgotpassword",
            "/api/v1/auth/otp/**"

    };

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(publicEndpoints).permitAll()
                            .requestMatchers("/api/v1/auth/getDoctorNameById").hasAuthority("PATIENT")
                            .requestMatchers("/api/v1/auth/").hasAuthority("DOCTOR")
    //  if multiple user need to access any single api  use  .hasAnyRole("1","2")
    // we can use .hasRole("USER") also, but Spring Security will add Prefix "ROLE_" to it.
    // it will become ROLE_USER while matching from DB role we need to add explicitly "ROLE_" before matching it.
                            .anyRequest().authenticated();
                }).authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
