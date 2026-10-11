package com.thucbn.ticketbooking.eventcatalogservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReserveTicketItemRequest(
        @NotNull(message = "INVALID_KEY") UUID ticketTypeId,
        @Min(value = 1, message = "INVALID_RESERVATION_QUANTITY") int quantity
) {
}
