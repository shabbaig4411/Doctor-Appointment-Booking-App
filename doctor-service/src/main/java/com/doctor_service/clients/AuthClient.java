package com.doctor_service.clients;

import com.doctor_service.clients.fallback.AuthClientFallback;
import com.doctor_service.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AUTH-SERVICE", configuration = FeignClientConfig.class, fallback = AuthClientFallback.class)
public interface AuthClient {

    @GetMapping("/api/v1/auth/getDoctorNameById")
    String getDoctorNameById(@RequestParam String doctorId);

}
