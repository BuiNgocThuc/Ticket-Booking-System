package com.thucbn.ticketbooking.eventcatalogservice.dto.response;

import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventSummaryResponse(
        UUID id,
        String name,
        String venue,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        EventStatus status
) {
}
