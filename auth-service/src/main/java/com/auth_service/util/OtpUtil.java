package com.auth_service.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtil {

    public String generateOtp(String mobile) {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // generates 6-digit number
        //  Sent OTP to Given Mobile Number
        return String.valueOf(otp);
    }

}
