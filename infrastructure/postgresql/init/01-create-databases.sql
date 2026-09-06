CREATE
USER event_catalog_user WITH PASSWORD 'ticket_dev';
  CREATE
DATABASE event_catalog_db OWNER event_catalog_user;

  CREATE
USER booking_user WITH PASSWORD 'ticket_dev';
  CREATE
DATABASE booking_db OWNER booking_user;

  CREATE
USER payment_user WITH PASSWORD 'ticket_dev';
  CREATE
DATABASE payment_db OWNER payment_user;