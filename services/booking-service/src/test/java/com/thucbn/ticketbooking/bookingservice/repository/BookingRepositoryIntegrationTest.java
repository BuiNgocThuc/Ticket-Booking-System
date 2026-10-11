package com.thucbn.ticketbooking.bookingservice.repository;

import com.thucbn.ticketbooking.bookingservice.constant.BookingStatus;
import com.thucbn.ticketbooking.bookingservice.entity.Booking;
import com.thucbn.ticketbooking.bookingservice.exception.AppException;
import com.thucbn.ticketbooking.bookingservice.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingRepositoryIntegrationTest {

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void shouldSaveBookingWithItemsTotalAndAuditFields() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.parse("2027-01-20T19:15:00+07:00")
        );

        booking.addItem(
                UUID.randomUUID(),
                "VIP Ticket",
                new BigDecimal("2500000.00"),
                2
        );
        booking.addItem(
                UUID.randomUUID(),
                "Standard Ticket",
                new BigDecimal("1000000.00"),
                1
        );

        Booking savedBooking = bookingRepository.saveAndFlush(booking);

        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(savedBooking.getTotalAmount()).isEqualByComparingTo("6000000.00");
        assertThat(savedBooking.getCreatedAt()).isNotNull();
        assertThat(savedBooking.getUpdatedAt()).isNotNull();
        assertThat(savedBooking.getItems()).hasSize(2);
        assertThat(savedBooking.getItems()).allSatisfy(item ->
                assertThat(item.getId()).isNotNull()
        );
    }

    @Test
    void shouldRejectNonPositiveTicketQuantity() {
        Booking booking = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.parse("2027-01-20T19:15:00+07:00")
        );

        assertThatThrownBy(() -> booking.addItem(
                UUID.randomUUID(),
                "VIP Ticket",
                new BigDecimal("2500000.00"),
                0
        ))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_TICKET_QUANTITY);
    }
}
