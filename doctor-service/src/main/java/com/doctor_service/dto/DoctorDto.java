package com.doctor_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDto {

    @NotBlank(message = "Specialization cannot be empty!")
    private String specialization;

    @NotBlank(message = "Qualification cannot be empty!")
    private String qualification;

    @Pattern(regexp = "\\d+", message = "Experience should contain digits only!")
    private String experience;

    // These are NOT JPA entities. Only names from frontend.
    @NotBlank(message = "State cannot be empty!")
    private String state;

    @NotBlank(message = "City cannot be empty!")
    private String city;

    @NotBlank(message = "Area cannot be empty!")
    private String area;

    @NotBlank(message = "Address cannot be empty!")
    private String address;
}
