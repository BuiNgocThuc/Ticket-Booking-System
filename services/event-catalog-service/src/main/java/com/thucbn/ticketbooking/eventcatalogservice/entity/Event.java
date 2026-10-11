package com.thucbn.ticketbooking.eventcatalogservice.entity;

import com.thucbn.ticketbooking.eventcatalogservice.constant.EventStatus;
import com.thucbn.ticketbooking.eventcatalogservice.exception.AppException;
import com.thucbn.ticketbooking.eventcatalogservice.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;


    @Column(nullable = false, length = 200)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(nullable = false, length = 200)
    String venue;

    @Column(name = "starts_at", nullable = false)
    OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    OffsetDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    EventStatus status;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<TicketType> ticketTypes = new ArrayList<>();

    public Event(
            String name,
            String description,
            String venue,
            OffsetDateTime startsAt,
            OffsetDateTime endsAt
    ) {
        if (startsAt == null || endsAt == null || !endsAt.isAfter(startsAt)) {
            throw new AppException(
                    ErrorCode.INVALID_EVENT_TIME_RANGE
            );
        }

        this.name = name;
        this.description = description;
        this.venue = venue;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = EventStatus.DRAFT;
    }

    public void addTicketType(
            String code,
            String name,
            BigDecimal price,
            int totalQuantity
    ) {
        TicketType ticketType = new TicketType(
                this,
                code,
                name,
                price,
                totalQuantity
        );

        ticketTypes.add(ticketType);
    }

    public List<TicketType> getTicketTypes() {
        return List.copyOf(ticketTypes);
    }

    public void publish() {
        if (status != EventStatus.DRAFT) {
            throw new AppException(ErrorCode.INVALID_EVENT_STATUS);
        }

        status = EventStatus.PUBLISHED;
    }

    public void cancel() {
        if (status == EventStatus.CANCELLED) {
            throw new AppException(ErrorCode.EVENT_ALREADY_CANCELLED);
        }

        status = EventStatus.CANCELLED;
    }
}
