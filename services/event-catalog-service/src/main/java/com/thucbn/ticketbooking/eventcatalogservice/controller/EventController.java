package com.thucbn.ticketbooking.eventcatalogservice.controller;

import com.thucbn.ticketbooking.eventcatalogservice.dto.request.CreateEventRequest;
import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.ApiResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.CreateEventResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventDetailResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventSummaryResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.PageResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventStatusResponse;
import com.thucbn.ticketbooking.eventcatalogservice.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventController {

    EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateEventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {
        CreateEventResponse response = eventService.createEvent(request);

        return ApiResponse.<CreateEventResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Event created successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<EventSummaryResponse>> getEvents(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "INVALID_PAGE_NUMBER") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "INVALID_PAGE_SIZE")
            @Max(value = 100, message = "INVALID_PAGE_SIZE") int size
    ) {
        PageResponse<EventSummaryResponse> response = eventService.getEvents(status, page, size);

        return ApiResponse.<PageResponse<EventSummaryResponse>>builder()
                .message("Events retrieved successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> getEventById(
            @PathVariable UUID eventId
    ) {
        EventDetailResponse response = eventService.getEventById(eventId);

        return ApiResponse.<EventDetailResponse>builder()
                .message("Event retrieved successfully")
                .result(response)
                .build();
    }

    @PatchMapping("/{eventId}/publish")
    public ApiResponse<EventStatusResponse> publishEvent(
            @PathVariable UUID eventId
    ) {
        return ApiResponse.<EventStatusResponse>builder()
                .message("Event published successfully")
                .result(eventService.publishEvent(eventId))
                .build();
    }

    @PatchMapping("/{eventId}/cancel")
    public ApiResponse<EventStatusResponse> cancelEvent(
            @PathVariable UUID eventId
    ) {
        return ApiResponse.<EventStatusResponse>builder()
                .message("Event cancelled successfully")
                .result(eventService.cancelEvent(eventId))
                .build();
    }
}
