package com.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientDto {

    private String state;     // State name
    private String city;      // City name
    private String area;      // Area name
    private String address;   // Full address
}

