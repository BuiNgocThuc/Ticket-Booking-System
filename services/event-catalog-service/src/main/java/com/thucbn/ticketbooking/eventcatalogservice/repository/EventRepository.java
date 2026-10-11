package com.thucbn.ticketbooking.eventcatalogservice.repository;

import com.thucbn.ticketbooking.eventcatalogservice.entity.Event;
import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @EntityGraph(attributePaths = "ticketTypes")
    @Query("select event from Event event where event.id = :eventId")
    Optional<Event> findByIdWithTicketTypes(@Param("eventId") UUID eventId);

    Page<Event> findByStatus(EventStatus status, Pageable pageable);
}
