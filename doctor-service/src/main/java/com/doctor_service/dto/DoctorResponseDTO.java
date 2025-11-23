package com.doctor_service.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DoctorResponseDTO {

    private String doctorId;
    private String name;
    private String specialization;
    private String address;
    private String area;
    private String city;
    private String state;
    private List<DoctorScheduleDTO> schedules;

    public DoctorResponseDTO(String doctorId,
                             String name,
                             String specialization,
                             String address,
                             String area,
                             String city,
                             String state) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.address = address;
        this.area = area;
        this.city = city;
        this.state = state;
        this.schedules = new ArrayList<>();
    }

    public void addSchedule(DoctorScheduleDTO schedule) {
        this.schedules.add(schedule);
    }
}
