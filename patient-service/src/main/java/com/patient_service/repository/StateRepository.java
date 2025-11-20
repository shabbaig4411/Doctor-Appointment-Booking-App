package com.patient_service.repository;

import com.patient_service.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State,Long> {
    Optional<State> findByNameIgnoreCase(String name);

}
