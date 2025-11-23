package com.doctor_service.service;

import com.doctor_service.clients.AuthClient;
import com.doctor_service.dto.*;
import com.doctor_service.entity.*;
import com.doctor_service.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorService {

    private final AuthClient authClient;
    private final DoctorRepository doctorRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;
    private final AppointmentDateRepository appointmentDateRepository;

    public DoctorService(AuthClient authClient,
                         DoctorRepository doctorRepository,
                         StateRepository stateRepository,
                         CityRepository cityRepository,
                         AreaRepository areaRepository,
                         AppointmentDateRepository appointmentDateRepository) {
        this.authClient = authClient;
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
            appointment.setDateId(ULIDGenerator.generate());
            appointment.setDoctorId(doctorId);
            appointment.setDate(details.getDate());
            appointment.setFee(details.getFee());
        }

        // 🟢 ADD NEW SLOTS (the SAFE WAY)
        for (LocalTime time : details.getTimeSlots()) {
            TimeSlots ts = new TimeSlots();
            ts.setTimeslotId(ULIDGenerator.generate());
            ts.setTime(time);
            ts.setAppointmentDate(appointment);
            appointment.getTimeSlots().add(ts);  // add to existing collection
        }


        return appointmentDateRepository.save(appointment);
    }


    public List<DoctorResponseDTO> searchDoctorWithAvailability(String specialization, String cityName) {
        List<Object[]> rows =
                doctorRepository.searchAvailableDoctors(specialization, cityName);
        Map<String, DoctorResponseDTO> doctorMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String doctorId = (String) row[0];
            String spec = (String) row[1];
            String address = (String) row[2];
            String area = (String) row[3];
            String city = (String) row[4];
            String state = (String) row[5];
            String appointmentDateId = String.valueOf(row[6]);
            LocalDate date = (LocalDate) row[7];
            long fee = (long) row[8];
            String timeSlotId = String.valueOf(row[9]);
            String time = String.valueOf(row[10]);
            if (!doctorMap.containsKey(doctorId)) {
                String name = authClient.getDoctorNameById(doctorId);
//            String name ="doctorName"; //  getDoctorNameByIdFromAUTH-SERVICE  using feign client.
                doctorMap.putIfAbsent(
                        doctorId,
                        new DoctorResponseDTO(doctorId, name, spec, address, area, city, state)
                );
            }
            //  Schedule Date assigning
            DoctorResponseDTO doctorDTO = doctorMap.get(doctorId);
            DoctorScheduleDTO scheduleDTO = doctorDTO.getSchedules().stream()
                    .filter(s -> s.getAppointmentDateId().equals(appointmentDateId))
                    .findFirst()
                    .orElse(null);
            if (scheduleDTO == null) {
                scheduleDTO = new DoctorScheduleDTO(appointmentDateId, date, fee, new ArrayList<>());
                doctorDTO.addSchedule(scheduleDTO);
            }
            scheduleDTO.getTimeSlots().add(new TimeSlotDTO(timeSlotId, time));
        }
        return new ArrayList<>(doctorMap.values());
    }


    public DoctorBookingResponseDto verifyDoctorSchedule(String doctorId, String appointmentDateId, long fee, String timeSlotId) {
        long count = doctorRepository.isDoctorExistsBy(doctorId, appointmentDateId, fee, timeSlotId);
        boolean available = (count > 0);
        return DoctorBookingResponseDto.builder()
                .doctorId(doctorId)
                .fee(fee)
                .available(available)
                .build();
    }


}

