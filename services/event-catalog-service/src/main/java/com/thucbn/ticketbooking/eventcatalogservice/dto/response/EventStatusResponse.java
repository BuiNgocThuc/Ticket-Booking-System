package com.thucbn.ticketbooking.eventcatalogservice.dto.response;

import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;

import java.util.UUID;

public record EventStatusResponse(
        UUID id,
        EventStatus status
) {
}
