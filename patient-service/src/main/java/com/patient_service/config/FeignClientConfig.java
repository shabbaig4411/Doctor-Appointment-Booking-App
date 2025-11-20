package com.patient_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader != null) {
                    template.header("Authorization", authorizationHeader);
                }

                String userId = request.getHeader("X-User-Id");
                String role = request.getHeader("X-User-Role");

                if (userId != null) template.header("X-User-Id", userId);
                if (role != null) template.header("X-User-Role", role);
            }
        };
    }
}
