package com.auth_service.util;

import java.time.LocalDateTime;

public record OtpEntry(String mobile,String otp, LocalDateTime expiry) {
}
