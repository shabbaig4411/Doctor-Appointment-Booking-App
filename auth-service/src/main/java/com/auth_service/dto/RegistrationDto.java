package com.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationDto {

    @NotBlank(message = "Role Cannot be BLANK ❌ ")
    private String role;

    @Size(min = 3, max = 30)
    private String name;

    @Pattern(regexp = "^[6-9]\\d{9}$",message = " 📱-> Invalid Mobile Number!!!")
    private String mobile;

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = " 📧 Invalid email format"
    )
    private String email;

    @NotBlank(message = "❌ Password Cannot be Empty.\n Password Should be include Alpha-Numeric and Special Characters")
    private String password;

}
