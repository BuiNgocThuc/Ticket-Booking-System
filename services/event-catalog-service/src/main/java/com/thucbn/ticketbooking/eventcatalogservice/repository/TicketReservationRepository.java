package com.thucbn.ticketbooking.eventcatalogservice.repository;

import com.thucbn.ticketbooking.eventcatalogservice.entity.TicketReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketReservationRepository extends JpaRepository<TicketReservation, UUID> {
    Optional<TicketReservation> findByBookingId(UUID bookingId);
}
