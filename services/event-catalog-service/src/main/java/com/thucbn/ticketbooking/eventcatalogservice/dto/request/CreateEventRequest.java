package com.thucbn.ticketbooking.eventcatalogservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateEventRequest(

        @NotBlank(message = "INVALID_EVENT_NAME")
        @Size(max = 200, message = "INVALID_EVENT_NAME")
        String name,

        String description,

        @NotBlank(message = "INVALID_EVENT_VENUE")
        @Size(max = 200, message = "INVALID_EVENT_VENUE")
        String venue,

        @NotNull(message = "INVALID_EVENT_START_TIME")
        OffsetDateTime startsAt,

        @NotNull(message = "INVALID_EVENT_END_TIME")
        OffsetDateTime endsAt,

        @NotEmpty(message = "INVALID_TICKET_TYPES")
        List<@Valid CreateTicketTypeRequest> ticketTypes
) {
}
