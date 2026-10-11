package com.thucbn.ticketbooking.eventcatalogservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketReservationItemResponse(
        UUID ticketTypeId,
        String ticketTypeName,
        BigDecimal unitPrice,
        int quantity
) {
}
