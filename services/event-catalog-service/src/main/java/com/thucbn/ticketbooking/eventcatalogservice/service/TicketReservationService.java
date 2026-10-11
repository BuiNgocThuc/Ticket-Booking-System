package com.thucbn.ticketbooking.eventcatalogservice.service;

import com.thucbn.ticketbooking.eventcatalogservice.dto.request.ReserveTicketItemRequest;
import com.thucbn.ticketbooking.eventcatalogservice.dto.request.ReserveTicketsRequest;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.TicketReservationResponse;
import com.thucbn.ticketbooking.eventcatalogservice.entity.Event;
import com.thucbn.ticketbooking.eventcatalogservice.entity.TicketReservation;
import com.thucbn.ticketbooking.eventcatalogservice.entity.TicketType;
import com.thucbn.ticketbooking.eventcatalogservice.exception.AppException;
import com.thucbn.ticketbooking.eventcatalogservice.exception.ErrorCode;
import com.thucbn.ticketbooking.eventcatalogservice.mapper.EventMapper;
import com.thucbn.ticketbooking.eventcatalogservice.repository.EventRepository;
import com.thucbn.ticketbooking.eventcatalogservice.repository.TicketReservationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketReservationService {

    private static final int RESERVATION_DURATION_MINUTES = 15;

    EventRepository eventRepository;
    TicketReservationRepository ticketReservationRepository;
    EventMapper eventMapper;

    @Transactional
    public TicketReservationResponse reserve(ReserveTicketsRequest request) {
        TicketReservation existingReservation = ticketReservationRepository
                .findByBookingId(request.bookingId())
                .orElse(null);

        if (existingReservation != null) {
            return eventMapper.toTicketReservationResponse(existingReservation);
        }

        validateNoDuplicateTicketTypes(request.items());

        Event event = eventRepository.findByIdWithTicketTypesForUpdate(request.eventId())
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));

        existingReservation = ticketReservationRepository
                .findByBookingId(request.bookingId())
                .orElse(null);

        if (existingReservation != null) {
            return eventMapper.toTicketReservationResponse(existingReservation);
        }

        event.ensurePublishedForReservation();

        List<ReservationCommand> commands = request.items().stream()
                .map(item -> new ReservationCommand(
                        event.getTicketType(item.ticketTypeId()),
                        item.quantity()
                ))
                .toList();

        commands.forEach(command -> command.ticketType().validateReservation(command.quantity()));

        TicketReservation reservation = new TicketReservation(
                request.bookingId(),
                request.eventId(),
                OffsetDateTime.now().plusMinutes(RESERVATION_DURATION_MINUTES)
        );

        commands.forEach(command -> {
            command.ticketType().reserve(command.quantity());
            reservation.addItem(command.ticketType(), command.quantity());
        });

        return eventMapper.toTicketReservationResponse(
                ticketReservationRepository.save(reservation)
        );
    }

    private void validateNoDuplicateTicketTypes(List<ReserveTicketItemRequest> items) {
        Set<UUID> ticketTypeIds = new HashSet<>();
        boolean hasDuplicate = items.stream()
                .map(ReserveTicketItemRequest::ticketTypeId)
                .anyMatch(ticketTypeId -> !ticketTypeIds.add(ticketTypeId));

        if (hasDuplicate) {
            throw new AppException(ErrorCode.DUPLICATE_TICKET_TYPE_RESERVATION);
        }
    }

    private record ReservationCommand(TicketType ticketType, int quantity) {
    }
}
