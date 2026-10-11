CREATE TABLE ticket_reservations
(
    id         UUID PRIMARY KEY,
    booking_id UUID                     NOT NULL UNIQUE,
    event_id   UUID                     NOT NULL,
    status     VARCHAR(30)              NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ticket_reservations_event_id ON ticket_reservations (event_id);
CREATE INDEX idx_ticket_reservations_status ON ticket_reservations (status);

CREATE TABLE ticket_reservation_items
(
    id               UUID PRIMARY KEY,
    reservation_id   UUID                     NOT NULL,
    ticket_type_id   UUID                     NOT NULL,
    ticket_type_name VARCHAR(100)             NOT NULL,
    unit_price       NUMERIC(12, 2)           NOT NULL,
    quantity         INTEGER                  NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ticket_reservation_items_reservation
        FOREIGN KEY (reservation_id) REFERENCES ticket_reservations (id),
    CONSTRAINT chk_ticket_reservation_items_quantity
        CHECK (quantity > 0),
    CONSTRAINT chk_ticket_reservation_items_unit_price
        CHECK (unit_price >= 0)
);
