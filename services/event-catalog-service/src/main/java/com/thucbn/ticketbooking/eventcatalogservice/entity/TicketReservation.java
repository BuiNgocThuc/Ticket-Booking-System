package com.thucbn.ticketbooking.eventcatalogservice.entity;

import com.thucbn.ticketbooking.eventcatalogservice.constant.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ticket_reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketReservation extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(name = "booking_id", nullable = false, unique = true)
    UUID bookingId;
    @Column(name = "event_id", nullable = false)
    UUID eventId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    ReservationStatus status;
    @Column(name = "expires_at", nullable = false)
    OffsetDateTime expiresAt;
    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TicketReservationItem> items = new ArrayList<>();

    public TicketReservation(UUID bookingId, UUID eventId, OffsetDateTime expiresAt) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.status = ReservationStatus.ACTIVE;
        this.expiresAt = expiresAt;
    }
    public void addItem(TicketType ticketType, int quantity) {
        items.add(new TicketReservationItem(this, ticketType, quantity));
    }
    public List<TicketReservationItem> getItems() {
        return List.copyOf(items);
    }
}
