package com.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgotPasswordDto {

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Mobile Number!!!")
    private String mobile;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmNewPassword;

    @Pattern(regexp = "\\d{6}$", message = "Invalid OTP!!!")
    private String otp;

}
