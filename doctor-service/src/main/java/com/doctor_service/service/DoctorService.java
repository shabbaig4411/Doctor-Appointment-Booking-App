package com.doctor_service.service;

import com.doctor_service.dto.AppointmentDetailsDto;
import com.doctor_service.dto.DoctorDto;
import com.doctor_service.dto.DoctorResponseDTO;
import com.doctor_service.dto.DoctorScheduleDTO;
import com.doctor_service.entity.*;
import com.doctor_service.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final AppointmentDateRepository appointmentDateRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         StateRepository stateRepository,
                         CityRepository cityRepository,
                         AreaRepository areaRepository,
                         AppointmentDateRepository appointmentDateRepository) {
        this.doctorRepository = doctorRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
        this.appointmentDateRepository = appointmentDateRepository;
    }

    public Doctor saveDoctor(DoctorDto doctorDto, String doctorId) {
        // ---------- Handle State ----------
        State existingState = stateRepository
                .findByNameIgnoreCase(doctorDto.getState())
                .orElseGet(() -> {
                    State s = new State();
                    s.setName(doctorDto.getState());
                    return stateRepository.save(s);
                });
        // ---------- Handle City ----------
        City existingCity = cityRepository
                .findByNameIgnoreCase(doctorDto.getCity())
                .orElseGet(() -> {
                    City c = new City();
                    c.setName(doctorDto.getCity());
                    return cityRepository.save(c);
                });
        // ---------- Handle Area ----------
        Area existingArea = areaRepository
                .findByNameIgnoreCase(doctorDto.getArea())
                .orElseGet(() -> {
                    Area a = new Area();
                    a.setName(doctorDto.getArea());
                    return areaRepository.save(a);
                });

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId); // Extracted From Token
        doctor.setImageUrl(doctorDto.getImageUrl());
        doctor.setSpecialization(doctorDto.getSpecialization());
        doctor.setQualification(doctorDto.getQualification());
        doctor.setExperience(doctorDto.getExperience());
        doctor.setAddress(doctorDto.getAddress());
        doctor.setState(existingState);
        doctor.setCity(existingCity);
        doctor.setArea(existingArea);

        return doctorRepository.save(doctor);
    }


    public AppointmentDate saveAppointmentDetails(AppointmentDetailsDto details, String doctorId) {

        Optional<AppointmentDate> existing =
                appointmentDateRepository.findByDoctorIdAndDate(doctorId, details.getDate());

        AppointmentDate appointment;

        if (existing.isPresent()) {
            appointment = existing.get();

            // 🟢 SAFE CLEAR (Do NOT replace set!!)
            Iterator<TimeSlots> it = appointment.getTimeSlots().iterator();
            while (it.hasNext()) {
                TimeSlots slot = it.next();
                it.remove();
                slot.setAppointmentDate(null); // required for orphanRemoval
            }

        } else {
            appointment = new AppointmentDate();
            appointment.setDoctorId(doctorId);
            appointment.setDate(details.getDate());
        }

        // 🟢 ADD NEW SLOTS (the SAFE WAY)
        for (LocalTime time : details.getTimeSlots()) {
            TimeSlots ts = new TimeSlots();
            ts.setTime(time);
            ts.setAppointmentDate(appointment);
            appointment.getTimeSlots().add(ts);  // add to existing collection
        }

        appointment.setFee(details.getFee());

        return appointmentDateRepository.save(appointment);
    }


    public List<DoctorResponseDTO> searchDoctorWithAvailability(String specialization, String city) {
        List<Object[]> rows =
                doctorRepository.searchAvailableDoctors(specialization, city);
        Map<String, DoctorResponseDTO> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String doctorId = (String) row[0];
            String spec = (String) row[1];
            String cityName = (String) row[2];
            LocalDate date = (LocalDate) row[3];
            LocalTime time = (LocalTime) row[4];

            map.putIfAbsent(
                    doctorId,
                    new DoctorResponseDTO(doctorId, spec, cityName)
            );
            DoctorResponseDTO dto = map.get(doctorId);
            DoctorScheduleDTO schedule = dto.getSchedules().stream()
                    .filter(s -> s.getDate().equals(date))
                    .findFirst()
                    .orElse(null);
            if (schedule == null) {
                schedule = new DoctorScheduleDTO(date, new ArrayList<>());
                dto.addSchedule(schedule);
            }
            schedule.getTimeSlots().add(time);
        }
        return new ArrayList<>(map.values());
    }


}

