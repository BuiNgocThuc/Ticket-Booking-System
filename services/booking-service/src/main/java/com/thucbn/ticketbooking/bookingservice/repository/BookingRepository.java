package com.thucbn.ticketbooking.bookingservice.repository;

import com.thucbn.ticketbooking.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
