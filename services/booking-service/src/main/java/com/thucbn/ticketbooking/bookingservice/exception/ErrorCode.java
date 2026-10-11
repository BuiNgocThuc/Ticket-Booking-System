package com.thucbn.ticketbooking.bookingservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    INVALID_BOOKING_CUSTOMER_ID("Booking customer ID must not be null"),
    INVALID_BOOKING_EVENT_ID("Booking event ID must not be null"),
    INVALID_TICKET_TYPE_ID("Ticket type ID must not be null"),
    INVALID_TICKET_TYPE_NAME("Ticket type name must not be blank"),
    INVALID_TICKET_PRICE("Ticket price must not be negative"),
    INVALID_TICKET_QUANTITY("Ticket quantity must be greater than zero");

    String message;
}
