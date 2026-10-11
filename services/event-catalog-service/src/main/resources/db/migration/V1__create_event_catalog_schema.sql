CREATE TABLE events
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(200)             NOT NULL,
    description TEXT,
    venue       VARCHAR(200)             NOT NULL,
    starts_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    ends_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    status      VARCHAR(30)              NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_events_time_range
        CHECK (ends_at > starts_at)
);

CREATE TABLE ticket_types
(
    id                 UUID PRIMARY KEY,
    event_id           UUID                     NOT NULL,
    code               VARCHAR(50)              NOT NULL,
    name               VARCHAR(100)             NOT NULL,
    price              NUMERIC(12, 2)           NOT NULL,
    total_quantity     INTEGER                  NOT NULL,
    available_quantity INTEGER                  NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ticket_types_event
        FOREIGN KEY (event_id)
            REFERENCES events (id),

    CONSTRAINT uk_ticket_types_event_code
        UNIQUE (event_id, code),

    CONSTRAINT chk_ticket_types_price
        CHECK (price >= 0),

    CONSTRAINT chk_ticket_types_total_quantity
        CHECK (total_quantity >= 0),

    CONSTRAINT chk_ticket_types_available_quantity
        CHECK (
            available_quantity >= 0
                AND available_quantity <= total_quantity
            )
);