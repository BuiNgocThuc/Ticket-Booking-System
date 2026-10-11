package com.thucbn.ticketbooking.eventcatalogservice.dto.response;

import com.thucbn.ticketbooking.eventcatalogservice.constant.ReservationStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TicketReservationResponse(
        UUID reservationId,
        UUID bookingId,
        UUID eventId,
        ReservationStatus status,
        OffsetDateTime expiresAt,
        List<TicketReservationItemResponse> items
) {
}
