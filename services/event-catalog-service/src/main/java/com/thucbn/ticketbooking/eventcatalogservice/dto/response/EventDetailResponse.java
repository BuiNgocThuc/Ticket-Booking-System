package com.thucbn.ticketbooking.eventcatalogservice.dto.response;

import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EventDetailResponse(
        UUID id,
        String name,
        String description,
        String venue,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        EventStatus status,
        List<TicketTypeResponse> ticketTypes
) {
}
