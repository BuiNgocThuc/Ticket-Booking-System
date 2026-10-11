package com.thucbn.ticketbooking.bookingservice.entity;

import com.thucbn.ticketbooking.bookingservice.constant.BookingStatus;
import com.thucbn.ticketbooking.bookingservice.exception.AppException;
import com.thucbn.ticketbooking.bookingservice.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "customer_id", nullable = false)
    UUID customerId;

    @Column(name = "event_id", nullable = false)
    UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    BookingStatus status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    BigDecimal totalAmount;

    @Column(name = "expires_at")
    OffsetDateTime expiresAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BookingItem> items = new ArrayList<>();

    public Booking(UUID customerId, UUID eventId, OffsetDateTime expiresAt) {
        if (customerId == null) {
            throw new AppException(ErrorCode.INVALID_BOOKING_CUSTOMER_ID);
        }

        if (eventId == null) {
            throw new AppException(ErrorCode.INVALID_BOOKING_EVENT_ID);
        }

        this.customerId = customerId;
        this.eventId = eventId;
        this.status = BookingStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.expiresAt = expiresAt;
    }

    public void addItem(
            UUID ticketTypeId,
            String ticketTypeName,
            BigDecimal unitPrice,
            int quantity
    ) {
        BookingItem item = new BookingItem(
                this,
                ticketTypeId,
                ticketTypeName,
                unitPrice,
                quantity
        );

        items.add(item);
        totalAmount = totalAmount.add(item.getSubtotal());
    }

    public List<BookingItem> getItems() {
        return List.copyOf(items);
    }
}
