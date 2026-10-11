package com.thucbn.ticketbooking.eventcatalogservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;
import com.thucbn.ticketbooking.eventcatalogservice.entity.Event;
import com.thucbn.ticketbooking.eventcatalogservice.repository.EventRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class EventControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Test
    void shouldCreateEventWithTicketTypes() throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateEventRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Event created successfully"))
                .andExpect(jsonPath("$.result.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(responseBody);
        UUID eventId = UUID.fromString(response.at("/result/id").asText());

        Event savedEvent = eventRepository.findById(eventId).orElseThrow();

        assertThat(savedEvent.getName()).isEqualTo("Spring Boot Microservices Workshop");
        assertThat(savedEvent.getTicketTypes()).hasSize(2);
        assertThat(savedEvent.getTicketTypes())
                .allSatisfy(ticketType -> assertThat(ticketType.getEvent()).isSameAs(savedEvent));
    }

    @Test
    void shouldReturnBadRequestWhenEndTimeIsNotAfterStartTime() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Invalid Event",
                                  "venue": "Ho Chi Minh City",
                                  "startsAt": "2027-01-20T19:00:00+07:00",
                                  "endsAt": "2027-01-20T18:00:00+07:00",
                                  "ticketTypes": [
                                    {
                                      "code": "STANDARD",
                                      "name": "Standard Ticket",
                                      "price": 199000,
                                      "totalQuantity": 100
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message")
                        .value("Event end time must be after start time"));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "venue": "Ho Chi Minh City",
                                  "startsAt": "2027-01-20T19:00:00+07:00",
                                  "endsAt": "2027-01-20T22:00:00+07:00",
                                  "ticketTypes": [
                                    {
                                      "code": "STANDARD",
                                      "name": "Standard Ticket",
                                      "price": 199000,
                                      "totalQuantity": 100
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1005))
                .andExpect(jsonPath("$.message")
                        .value("Event name must not be blank and must be at most 200 characters"));
    }

    @Test
    void shouldReturnPagedEventSummaries() throws Exception {
        createAndSaveEvent("First Event");
        createAndSaveEvent("Second Event");

        mockMvc.perform(get("/api/v1/events")
                        .queryParam("page", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.page").value(0))
                .andExpect(jsonPath("$.result.size").value(10))
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void shouldFilterEventsByStatus() throws Exception {
        Event publishedEvent = createAndSaveEvent("Published Event For Filter");
        publishedEvent.publish();
        eventRepository.flush();

        String responseBody = mockMvc.perform(get("/api/v1/events")
                        .queryParam("status", "PUBLISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode content = objectMapper.readTree(responseBody).at("/result/content");

        assertThat(responseBody).contains(publishedEvent.getId().toString());
        for (JsonNode event : content) {
            assertThat(event.path("status").asText()).isEqualTo("PUBLISHED");
        }
    }

    @Test
    void shouldReturnBadRequestWhenStatusFilterIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/events")
                        .queryParam("status", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1016))
                .andExpect(jsonPath("$.message").value("Event status filter is invalid"));
    }

    @Test
    void shouldReturnEventDetailWithTicketTypes() throws Exception {
        Event event = createAndSaveEvent("Event Detail");

        mockMvc.perform(get("/api/v1/events/{eventId}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.id").value(event.getId().toString()))
                .andExpect(jsonPath("$.result.name").value("Event Detail"))
                .andExpect(jsonPath("$.result.ticketTypes").isArray())
                .andExpect(jsonPath("$.result.ticketTypes.length()").value(1))
                .andExpect(jsonPath("$.result.ticketTypes[0].code").value("STANDARD"));
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/events/{eventId}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1012))
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void shouldPublishDraftEvent() throws Exception {
        Event event = createAndSaveEvent("Draft Event");

        mockMvc.perform(patch("/api/v1/events/{eventId}/publish", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.id").value(event.getId().toString()))
                .andExpect(jsonPath("$.result.status").value("PUBLISHED"));

        assertThat(eventRepository.findById(event.getId()).orElseThrow().getStatus())
                .isEqualTo(EventStatus.PUBLISHED);
    }

    @Test
    void shouldReturnNotFoundWhenPublishingNonExistentEvent() throws Exception {
        mockMvc.perform(patch("/api/v1/events/{eventId}/publish", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1012))
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void shouldRejectPublishingNonDraftEvent() throws Exception {
        Event event = createAndSaveEvent("Published Event");
        event.publish();
        eventRepository.flush();

        mockMvc.perform(patch("/api/v1/events/{eventId}/publish", event.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1015))
                .andExpect(jsonPath("$.message").value("Only draft events can be published"));
    }

    @Test
    void shouldCancelPublishedEvent() throws Exception {
        Event event = createAndSaveEvent("Published Event To Cancel");
        event.publish();
        eventRepository.flush();

        mockMvc.perform(patch("/api/v1/events/{eventId}/cancel", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.id").value(event.getId().toString()))
                .andExpect(jsonPath("$.result.status").value("CANCELLED"));

        assertThat(eventRepository.findById(event.getId()).orElseThrow().getStatus())
                .isEqualTo(EventStatus.CANCELLED);
    }

    @Test
    void shouldReturnNotFoundWhenCancellingNonExistentEvent() throws Exception {
        mockMvc.perform(patch("/api/v1/events/{eventId}/cancel", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1012))
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void shouldRejectCancellingAlreadyCancelledEvent() throws Exception {
        Event event = createAndSaveEvent("Cancelled Event");
        event.cancel();
        eventRepository.flush();

        mockMvc.perform(patch("/api/v1/events/{eventId}/cancel", event.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1017))
                .andExpect(jsonPath("$.message").value("Event is already cancelled"));
    }

    private Event createAndSaveEvent(String name) {
        Event event = new Event(
                name,
                "Event description",
                "Ho Chi Minh City",
                OffsetDateTime.parse("2027-01-20T19:00:00+07:00"),
                OffsetDateTime.parse("2027-01-20T22:00:00+07:00")
        );
        event.addTicketType(
                "STANDARD",
                "Standard Ticket",
                new BigDecimal("199000.00"),
                100
        );

        return eventRepository.saveAndFlush(event);
    }

    private String validCreateEventRequest() {
        return """
                {
                  "name": "Spring Boot Microservices Workshop",
                  "description": "Workshop thực hành",
                  "venue": "Ho Chi Minh City",
                  "startsAt": "2027-01-20T19:00:00+07:00",
                  "endsAt": "2027-01-20T22:00:00+07:00",
                  "ticketTypes": [
                    {
                      "code": "STANDARD",
                      "name": "Standard Ticket",
                      "price": 199000,
                      "totalQuantity": 100
                    },
                    {
                      "code": "VIP",
                      "name": "VIP Ticket",
                      "price": 499000,
                      "totalQuantity": 20
                    }
                  ]
                }
                """;
    }
}
