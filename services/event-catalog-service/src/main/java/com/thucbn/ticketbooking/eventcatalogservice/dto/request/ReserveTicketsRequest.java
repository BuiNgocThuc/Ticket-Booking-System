package com.thucbn.ticketbooking.eventcatalogservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReserveTicketsRequest(
        @NotNull(message = "INVALID_KEY") UUID bookingId,
        @NotNull(message = "INVALID_KEY") UUID eventId,
        @NotEmpty(message = "INVALID_RESERVATION_ITEMS") List<@Valid ReserveTicketItemRequest> items
) {
}
