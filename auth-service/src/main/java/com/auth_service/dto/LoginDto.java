package com.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {

    @Pattern(regexp = "^[6-9]\\d{9}$",message = "Invalid Mobile Number!!!")
    private String mobile;
    @NotBlank
    private String password;
}
