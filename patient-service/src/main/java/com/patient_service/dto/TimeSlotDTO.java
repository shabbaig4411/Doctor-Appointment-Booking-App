package com.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeSlotDTO {
    private String timeSlotId;
    private String time;

    public TimeSlotDTO(String timeSlotId, String time) {
        this.timeSlotId = timeSlotId;
        this.time = time;
    }
}

