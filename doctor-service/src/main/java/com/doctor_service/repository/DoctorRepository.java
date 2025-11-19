package com.doctor_service.repository;

import com.doctor_service.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("""
    SELECT d.id, d.specialization, d.city.name,
           a.date, t.time
    FROM Doctor d
    JOIN AppointmentDate a ON a.doctorId = d.id
    JOIN TimeSlots t ON t.appointmentDate = a
    WHERE LOWER(d.specialization) = LOWER(:specialization)
      AND LOWER(d.city.name) = LOWER(:city)
      AND (
            a.date > CURRENT_DATE
            OR (a.date = CURRENT_DATE AND t.time > CURRENT_TIME)
          )
    ORDER BY d.id, a.date, t.time
""")
    List<Object[]> searchAvailableDoctors(
            @Param("specialization") String specialization,
            @Param("city") String city
    );


//    @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialization) = LOWER(:specialization) AND LOWER(d.area.name) = LOWER(:areaName)")
//    List<Doctor> findBySpecializationAndArea(@Param("specialization") String specialization,
//                                               @Param("areaName") String areaName);


}
