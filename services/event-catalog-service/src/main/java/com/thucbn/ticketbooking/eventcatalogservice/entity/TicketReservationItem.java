package com.thucbn.ticketbooking.eventcatalogservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ticket_reservation_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketReservationItem extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    TicketReservation reservation;
    @Column(name = "ticket_type_id", nullable = false)
    UUID ticketTypeId;
    @Column(name = "ticket_type_name", nullable = false, length = 100)
    String ticketTypeName;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    BigDecimal unitPrice;
    @Column(nullable = false)
    int quantity;

    TicketReservationItem(TicketReservation reservation, TicketType ticketType, int quantity) {
        this.reservation = reservation;
        this.ticketTypeId = ticketType.getId();
        this.ticketTypeName = ticketType.getName();
        this.unitPrice = ticketType.getPrice();
        this.quantity = quantity;
    }
}
