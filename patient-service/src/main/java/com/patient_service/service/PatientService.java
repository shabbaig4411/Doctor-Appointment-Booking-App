package com.patient_service.service;

import com.patient_service.dto.PatientDto;
import com.patient_service.entity.*;
import com.patient_service.repository.*;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AreaRepository areaRepository;

    public PatientService(PatientRepository patientRepository,
                          StateRepository stateRepository,
                          CityRepository cityRepository,
                          AreaRepository areaRepository) {
        this.patientRepository = patientRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.areaRepository = areaRepository;
    }

    public Patient savePatient(PatientDto dto, String patientId) {

        // Save or fetch STATE
        State state = stateRepository.findByNameIgnoreCase(dto.getState())
                .orElseGet(() -> {
                    State s = new State();
                    s.setName(dto.getState());
                    return stateRepository.save(s);
                });

        // Save or fetch CITY
        City city = cityRepository.findByNameIgnoreCase(dto.getCity())
                .orElseGet(() -> {
                    City c = new City();
                    c.setName(dto.getCity());
                    return cityRepository.save(c);
                });

        // Save or fetch AREA
        Area area = areaRepository.findByNameIgnoreCase(dto.getArea())
                .orElseGet(() -> {
                    Area a = new Area();
                    a.setName(dto.getArea());
                    return areaRepository.save(a);
                });

        // Create and save Patient
        Patient p = new Patient();
        p.setPatientId(patientId);   // from JWT token (X-User-Id)
        p.setAddress(dto.getAddress());
        p.setState(state);
        p.setCity(city);
        p.setArea(area);

        return patientRepository.save(p);
    }




}
