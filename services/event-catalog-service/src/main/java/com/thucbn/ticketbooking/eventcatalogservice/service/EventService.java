package com.thucbn.ticketbooking.eventcatalogservice.service;

import com.thucbn.ticketbooking.eventcatalogservice.dto.request.CreateEventRequest;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.CreateEventResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventDetailResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventSummaryResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.PageResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventStatusResponse;
import com.thucbn.ticketbooking.eventcatalogservice.entity.Event;
import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;
import com.thucbn.ticketbooking.eventcatalogservice.exception.AppException;
import com.thucbn.ticketbooking.eventcatalogservice.exception.ErrorCode;
import com.thucbn.ticketbooking.eventcatalogservice.mapper.EventMapper;
import com.thucbn.ticketbooking.eventcatalogservice.repository.EventRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {

    EventRepository eventRepository;
    EventMapper eventMapper;

    @Transactional
    public CreateEventResponse createEvent(CreateEventRequest request) {
        Event event = eventMapper.toEntity(request);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toCreateEventResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public PageResponse<EventSummaryResponse> getEvents(
            EventStatus status,
            int page,
            int size
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "startsAt")
        );
        Page<Event> events = status == null
                ? eventRepository.findAll(pageRequest)
                : eventRepository.findByStatus(status, pageRequest);

        Page<EventSummaryResponse> eventPage = events
                .map(eventMapper::toEventSummaryResponse);

        return PageResponse.from(eventPage);
    }

    @Transactional(readOnly = true)
    public EventDetailResponse getEventById(UUID eventId) {
        Event event = eventRepository.findByIdWithTicketTypes(eventId)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));

        return eventMapper.toEventDetailResponse(event);
    }

    @Transactional
    public EventStatusResponse publishEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));

        event.publish();

        return eventMapper.toEventStatusResponse(event);
    }

    @Transactional
    public EventStatusResponse cancelEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));

        event.cancel();

        return eventMapper.toEventStatusResponse(event);
    }
}
