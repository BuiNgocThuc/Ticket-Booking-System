package com.thucbn.ticketbooking.eventcatalogservice.service;

import com.thucbn.ticketbooking.eventcatalogservice.dto.request.ReserveTicketItemRequest;
import com.thucbn.ticketbooking.eventcatalogservice.dto.request.ReserveTicketsRequest;
import com.thucbn.ticketbooking.eventcatalogservice.dto.response.TicketReservationResponse;
import com.thucbn.ticketbooking.eventcatalogservice.entity.Event;
import com.thucbn.ticketbooking.eventcatalogservice.entity.TicketReservation;
import com.thucbn.ticketbooking.eventcatalogservice.exception.AppException;
import com.thucbn.ticketbooking.eventcatalogservice.exception.ErrorCode;
import com.thucbn.ticketbooking.eventcatalogservice.repository.EventRepository;
import com.thucbn.ticketbooking.eventcatalogservice.repository.TicketReservationRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class TicketReservationIntegrationTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    TicketReservationService ticketReservationService;
    @Autowired
    TicketReservationRepository ticketReservationRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void shouldReserveTicketsAndReturnPriceSnapshot() {
        Event event = publishedEventWithVipTickets(5);
        UUID bookingId = UUID.randomUUID();
        UUID ticketTypeId = event.getTicketTypes().getFirst().getId();

        TicketReservationResponse response = ticketReservationService.reserve(
                new ReserveTicketsRequest(
                        bookingId,
                        event.getId(),
                        List.of(new ReserveTicketItemRequest(ticketTypeId, 2))
                )
        );

        assertThat(response.bookingId()).isEqualTo(bookingId);
        assertThat(response.items()).singleElement().satisfies(item -> {
            assertThat(item.ticketTypeName()).isEqualTo("VIP Ticket");
            assertThat(item.unitPrice()).isEqualByComparingTo("2500000.00");
            assertThat(item.quantity()).isEqualTo(2);
        });
        ticketReservationRepository.flush();
        entityManager.clear();

        TicketReservation savedReservation = ticketReservationRepository
                .findById(response.reservationId())
                .orElseThrow();
        assertThat(savedReservation.getItems()).hasSize(1);
        assertThat(eventRepository.findByIdWithTicketTypes(event.getId())
                .orElseThrow()
                .getTicketTypes()
                .getFirst()
                .getAvailableQuantity())
                .isEqualTo(3);
    }

    @Test
    void shouldRejectReservationWhenTicketsAreInsufficient() {
        Event event = publishedEventWithVipTickets(1);
        UUID ticketTypeId = event.getTicketTypes().getFirst().getId();

        assertThatThrownBy(() -> ticketReservationService.reserve(
                new ReserveTicketsRequest(
                        UUID.randomUUID(),
                        event.getId(),
                        List.of(new ReserveTicketItemRequest(ticketTypeId, 2))
                )
        ))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INSUFFICIENT_TICKET_AVAILABILITY);

        assertThat(event.getTicketTypes().getFirst().getAvailableQuantity()).isEqualTo(1);
    }

    @Test
    void shouldReturnExistingReservationWhenBookingIsRetried() {
        Event event = publishedEventWithVipTickets(5);
        UUID bookingId = UUID.randomUUID();
        UUID ticketTypeId = event.getTicketTypes().getFirst().getId();
        ReserveTicketsRequest request = new ReserveTicketsRequest(
                bookingId,
                event.getId(),
                List.of(new ReserveTicketItemRequest(ticketTypeId, 2))
        );

        TicketReservationResponse firstReservation = ticketReservationService.reserve(request);
        TicketReservationResponse retriedReservation = ticketReservationService.reserve(request);

        assertThat(retriedReservation.reservationId()).isEqualTo(firstReservation.reservationId());
        assertThat(event.getTicketTypes().getFirst().getAvailableQuantity()).isEqualTo(3);
    }

    @Test
    void shouldRejectReservationForDraftEvent() {
        Event event = new Event(
                "Taylor Swift: The Eras Tour",
                "Concert in Ho Chi Minh City",
                "My Dinh National Stadium",
                OffsetDateTime.parse("2027-01-20T19:00:00+07:00"),
                OffsetDateTime.parse("2027-01-20T22:00:00+07:00")
        );
        event.addTicketType("VIP", "VIP Ticket", new BigDecimal("2500000.00"), 5);
        Event savedEvent = eventRepository.saveAndFlush(event);

        assertThatThrownBy(() -> ticketReservationService.reserve(new ReserveTicketsRequest(
                UUID.randomUUID(),
                savedEvent.getId(),
                List.of(new ReserveTicketItemRequest(savedEvent.getTicketTypes().getFirst().getId(), 1))
        )))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.EVENT_NOT_PUBLISHED);
    }

    @Test
    void shouldRejectDuplicateTicketTypeInReservationRequest() {
        Event event = publishedEventWithVipTickets(5);
        UUID ticketTypeId = event.getTicketTypes().getFirst().getId();

        assertThatThrownBy(() -> ticketReservationService.reserve(new ReserveTicketsRequest(
                UUID.randomUUID(),
                event.getId(),
                List.of(
                        new ReserveTicketItemRequest(ticketTypeId, 1),
                        new ReserveTicketItemRequest(ticketTypeId, 1)
                )
        )))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_TICKET_TYPE_RESERVATION);
    }

    @Test
    void shouldNotPartiallyReserveWhenOneTicketTypeIsInsufficient() {
        Event event = new Event(
                "Taylor Swift: The Eras Tour",
                "Concert in Ho Chi Minh City",
                "My Dinh National Stadium",
                OffsetDateTime.parse("2027-01-20T19:00:00+07:00"),
                OffsetDateTime.parse("2027-01-20T22:00:00+07:00")
        );
        event.addTicketType("VIP", "VIP Ticket", new BigDecimal("2500000.00"), 5);
        event.addTicketType("STANDARD", "Standard Ticket", new BigDecimal("1000000.00"), 1);
        event.publish();
        Event savedEvent = eventRepository.saveAndFlush(event);

        UUID vipTicketTypeId = savedEvent.getTicketTypes().get(0).getId();
        UUID standardTicketTypeId = savedEvent.getTicketTypes().get(1).getId();

        assertThatThrownBy(() -> ticketReservationService.reserve(new ReserveTicketsRequest(
                UUID.randomUUID(),
                savedEvent.getId(),
                List.of(
                        new ReserveTicketItemRequest(vipTicketTypeId, 2),
                        new ReserveTicketItemRequest(standardTicketTypeId, 2)
                )
        )))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INSUFFICIENT_TICKET_AVAILABILITY);

        assertThat(event.getTicketTypes().get(0).getAvailableQuantity()).isEqualTo(5);
        assertThat(event.getTicketTypes().get(1).getAvailableQuantity()).isEqualTo(1);
    }

    private Event publishedEventWithVipTickets(int quantity) {
        Event event = new Event(
                "Taylor Swift: The Eras Tour",
                "Concert in Ho Chi Minh City",
                "My Dinh National Stadium",
                OffsetDateTime.parse("2027-01-20T19:00:00+07:00"),
                OffsetDateTime.parse("2027-01-20T22:00:00+07:00")
        );
        event.addTicketType("VIP", "VIP Ticket", new BigDecimal("2500000.00"), quantity);
        event.publish();
        return eventRepository.saveAndFlush(event);
    }
}
