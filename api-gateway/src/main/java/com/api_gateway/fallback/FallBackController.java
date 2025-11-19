package com.api_gateway.fallback;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {

    @GetMapping("/fallback/user")
    public String userServiceFallback() {
        return "User Service is currently unavailable. Please try again later.";
    }

    @GetMapping("/fallback/distributor")
    public String distributorServiceFallback() {
        return "Distributor Service is currently unavailable. Please try again later.";
    }
}