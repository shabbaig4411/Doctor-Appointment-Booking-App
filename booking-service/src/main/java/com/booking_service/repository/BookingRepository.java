package com.booking_service.repository;

import com.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface BookingRepository extends JpaRepository<Booking, String> {
}