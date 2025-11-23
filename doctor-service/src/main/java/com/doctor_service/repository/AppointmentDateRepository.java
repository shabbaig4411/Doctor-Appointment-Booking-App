package com.doctor_service.repository;

import com.doctor_service.entity.AppointmentDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.Optional;

public interface AppointmentDateRepository extends JpaRepository<AppointmentDate, String> {

    @Query("SELECT a FROM AppointmentDate a WHERE a.doctorId = :doctorId AND a.date = :date")
    Optional<AppointmentDate> findByDoctorIdAndDate(String doctorId, LocalDate date);

}