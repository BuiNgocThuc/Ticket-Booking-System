package com.thucbn.ticketbooking.eventcatalogservice.controller;

import com.thucbn.ticketbooking.eventcatalogservice.dto.request.ReserveTicketsRequest;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.ApiResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.TicketReservationResponse;
import com.thucbn.ticketbooking.eventcatalogservice.service.TicketReservationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/ticket-reservations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalTicketReservationController {

    TicketReservationService ticketReservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TicketReservationResponse> reserve(
            @Valid @RequestBody ReserveTicketsRequest request
    ) {
        return ApiResponse.<TicketReservationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tickets reserved successfully")
                .result(ticketReservationService.reserve(request))
                .build();
    }
}
