package com.thucbn.ticketbooking.eventcatalogservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketTypeResponse(
        UUID id,
        String code,
        String name,
        BigDecimal price,
        int totalQuantity,
        int availableQuantity
) {
}
