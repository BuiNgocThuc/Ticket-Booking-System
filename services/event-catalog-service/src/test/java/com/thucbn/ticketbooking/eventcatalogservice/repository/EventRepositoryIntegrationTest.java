package com.thucbn.ticketbooking.eventcatalogservice.repository;

import com.thucbn.ticketbooking.eventcatalogservice.entity.Event;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class EventRepositoryIntegrationTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    void shouldSaveEventWithTicketTypesAndAuditFields() {
        Event event = new Event(
                "Taylor Swift: The Eras Tour",
                "Concert in Ho Chi Minh City",
                "My Dinh National Stadium",
                OffsetDateTime.parse("2027-01-20T19:00:00+07:00"),
                OffsetDateTime.parse("2027-01-20T22:00:00+07:00")
        );

        event.addTicketType(
                "VIP",
                "VIP Ticket",
                new BigDecimal("2500000.00"),
                100
        );

        Event savedEvent = eventRepository.saveAndFlush(event);

        assertThat(savedEvent.getId()).isNotNull();
        assertThat(savedEvent.getCreatedAt()).isNotNull();
        assertThat(savedEvent.getUpdatedAt()).isNotNull();

        assertThat(savedEvent.getTicketTypes()).hasSize(1);
        assertThat(savedEvent.getTicketTypes().getFirst().getId()).isNotNull();
    }
}
