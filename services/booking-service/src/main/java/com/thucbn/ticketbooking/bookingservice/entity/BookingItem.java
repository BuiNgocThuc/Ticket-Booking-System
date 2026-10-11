package com.thucbn.ticketbooking.bookingservice.entity;

import com.thucbn.ticketbooking.bookingservice.exception.AppException;
import com.thucbn.ticketbooking.bookingservice.exception.ErrorCode;
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
@Table(name = "booking_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingItem extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;

    @Column(name = "ticket_type_id", nullable = false)
    UUID ticketTypeId;

    @Column(name = "ticket_type_name", nullable = false, length = 100)
    String ticketTypeName;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    BigDecimal unitPrice;

    @Column(nullable = false)
    int quantity;

    BookingItem(
            Booking booking,
            UUID ticketTypeId,
            String ticketTypeName,
            BigDecimal unitPrice,
            int quantity
    ) {
        if (ticketTypeId == null) {
            throw new AppException(ErrorCode.INVALID_TICKET_TYPE_ID);
        }

        if (ticketTypeName == null || ticketTypeName.isBlank()) {
            throw new AppException(ErrorCode.INVALID_TICKET_TYPE_NAME);
        }

        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new AppException(ErrorCode.INVALID_TICKET_PRICE);
        }

        if (quantity <= 0) {
            throw new AppException(ErrorCode.INVALID_TICKET_QUANTITY);
        }

        this.booking = booking;
        this.ticketTypeId = ticketTypeId;
        this.ticketTypeName = ticketTypeName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
