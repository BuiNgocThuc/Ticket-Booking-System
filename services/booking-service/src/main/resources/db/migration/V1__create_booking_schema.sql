CREATE TABLE bookings
(
    id           UUID PRIMARY KEY,
    customer_id  UUID                     NOT NULL,
    event_id     UUID                     NOT NULL,
    status       VARCHAR(30)              NOT NULL,
    total_amount NUMERIC(12, 2)           NOT NULL,
    expires_at   TIMESTAMP WITH TIME ZONE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_bookings_total_amount
        CHECK (total_amount >= 0)
);

CREATE INDEX idx_bookings_customer_id ON bookings (customer_id);
CREATE INDEX idx_bookings_event_id ON bookings (event_id);
CREATE INDEX idx_bookings_status ON bookings (status);

CREATE TABLE booking_items
(
    id               UUID PRIMARY KEY,
    booking_id       UUID                     NOT NULL,
    ticket_type_id   UUID                     NOT NULL,
    ticket_type_name VARCHAR(100)             NOT NULL,
    unit_price       NUMERIC(12, 2)           NOT NULL,
    quantity         INTEGER                  NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_items_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id),

    CONSTRAINT chk_booking_items_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_booking_items_quantity
        CHECK (quantity > 0)
);
