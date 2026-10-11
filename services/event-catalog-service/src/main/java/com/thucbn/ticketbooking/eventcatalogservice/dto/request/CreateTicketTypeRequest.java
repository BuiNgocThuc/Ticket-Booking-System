package com.thucbn.ticketbooking.eventcatalogservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateTicketTypeRequest(

        @NotBlank(message = "INVALID_TICKET_TYPE_CODE")
        @Size(max = 50, message = "INVALID_TICKET_TYPE_CODE")
        String code,

        @NotBlank(message = "INVALID_TICKET_TYPE_NAME")
        @Size(max = 100, message = "INVALID_TICKET_TYPE_NAME")
        String name,

        @NotNull(message = "INVALID_TICKET_PRICE")
        @DecimalMin(value = "0.00", message = "INVALID_TICKET_PRICE")
        BigDecimal price,

        @NotNull(message = "INVALID_TICKET_QUANTITY")
        @PositiveOrZero(message = "INVALID_TICKET_QUANTITY")
        Integer totalQuantity
) {
}
