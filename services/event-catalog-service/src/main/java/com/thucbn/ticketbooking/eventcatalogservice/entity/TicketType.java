package com.thucbn.ticketbooking.eventcatalogservice.entity;

import com.thucbn.ticketbooking.eventcatalogservice.exception.AppException;
import com.thucbn.ticketbooking.eventcatalogservice.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ticket_types")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketType extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    @Column(nullable = false, length = 50)
    String code;

    @Column(nullable = false, length = 100)
    String name;

    @Column(nullable = false, precision = 12, scale = 2)
    BigDecimal price;

    @Column(name = "total_quantity", nullable = false)
    int totalQuantity;

    @Column(name = "available_quantity", nullable = false)
    int availableQuantity;

    TicketType(
            Event event,
            String code,
            String name,
            BigDecimal price,
            int totalQuantity
    ) {
        if (price == null || price.signum() < 0) {
            throw new AppException(
                    ErrorCode.INVALID_TICKET_PRICE
            );
        }

        if (totalQuantity < 0) {
            throw new AppException(
                    ErrorCode.INVALID_TICKET_QUANTITY
            );
        }

        this.event = event;
        this.code = code;
        this.name = name;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
    }
}
