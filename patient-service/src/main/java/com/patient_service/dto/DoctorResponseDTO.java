package com.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DoctorResponseDTO {
    private String doctorId;
    private String doctorName;
    private String specialization;
    private String areaName;
    private List<DoctorScheduleDTO> schedules;

    public DoctorResponseDTO(String doctorId, String doctorName,
                             String specialization, String areaName) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.areaName = areaName;
        this.schedules = new ArrayList<>();
    }

    public void addSchedule(DoctorScheduleDTO schedule) {
        this.schedules.add(schedule);
    }
}
