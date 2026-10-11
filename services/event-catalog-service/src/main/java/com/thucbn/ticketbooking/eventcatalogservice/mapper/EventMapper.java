package com.thucbn.ticketbooking.eventcatalogservice.mapper;

import com.thucbn.ticketbooking.eventcatalogservice.dto.request.CreateEventRequest;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.CreateEventResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventDetailResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventSummaryResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.EventStatusResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.TicketTypeResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.TicketReservationItemResponse;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.TicketReservationResponse;
import com.thucbn.ticketbooking.eventcatalogservice.entity.Event;
import com.thucbn.ticketbooking.eventcatalogservice.entity.TicketReservation;
import com.thucbn.ticketbooking.eventcatalogservice.entity.TicketReservationItem;
import com.thucbn.ticketbooking.eventcatalogservice.entity.TicketType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EventMapper {

    @Mapping(target = "ticketTypes", ignore = true)
    Event toEntity(CreateEventRequest request);

    CreateEventResponse toCreateEventResponse(Event event);

    EventSummaryResponse toEventSummaryResponse(Event event);

    EventDetailResponse toEventDetailResponse(Event event);

    TicketTypeResponse toTicketTypeResponse(TicketType ticketType);

    EventStatusResponse toEventStatusResponse(Event event);

    @Mapping(target = "reservationId", source = "id")
    TicketReservationResponse toTicketReservationResponse(TicketReservation reservation);

    TicketReservationItemResponse toTicketReservationItemResponse(TicketReservationItem item);

    @AfterMapping
    default void addTicketTypes(
            CreateEventRequest request,
            @MappingTarget Event event
    ) {
        request.ticketTypes().forEach(ticketType -> event.addTicketType(
                ticketType.code(),
                ticketType.name(),
                ticketType.price(),
                ticketType.totalQuantity()
        ));
    }
}
