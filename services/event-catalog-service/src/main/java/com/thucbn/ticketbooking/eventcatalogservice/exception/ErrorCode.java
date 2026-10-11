package com.thucbn.ticketbooking.eventcatalogservice.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(
            9999,
            "Unexpected error",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    INVALID_EVENT_TIME_RANGE(
            1001,
            "Event end time must be after start time",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_TICKET_PRICE(
            1002,
            "Ticket price must not be negative",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_TICKET_QUANTITY(
            1003,
            "Total ticket quantity must not be negative",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_KEY(
            1004,
            "Invalid validation error key",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_EVENT_NAME(
            1005,
            "Event name must not be blank and must be at most 200 characters",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_EVENT_VENUE(
            1006,
            "Event venue must not be blank and must be at most 200 characters",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_EVENT_START_TIME(
            1007,
            "Event start time is required",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_EVENT_END_TIME(
            1008,
            "Event end time is required",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_TICKET_TYPES(
            1009,
            "At least one ticket type is required",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_TICKET_TYPE_CODE(
            1010,
            "Ticket type code must not be blank and must be at most 50 characters",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_TICKET_TYPE_NAME(
            1011,
            "Ticket type name must not be blank and must be at most 100 characters",
            HttpStatus.BAD_REQUEST
    ),

    EVENT_NOT_FOUND(
            1012,
            "Event not found",
            HttpStatus.NOT_FOUND
    ),

    INVALID_PAGE_NUMBER(
            1013,
            "Page number must not be negative",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_PAGE_SIZE(
            1014,
            "Page size must be between 1 and 100",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_EVENT_STATUS(
            1015,
            "Only draft events can be published",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_EVENT_STATUS_FILTER(
            1016,
            "Event status filter is invalid",
            HttpStatus.BAD_REQUEST
    ),

    EVENT_ALREADY_CANCELLED(
            1017,
            "Event is already cancelled",
            HttpStatus.BAD_REQUEST
    );

    int code;
    String message;
    HttpStatus httpStatusCode;
}
