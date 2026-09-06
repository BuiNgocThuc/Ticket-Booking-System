# Architecture — Ticket Booking System

## Mục tiêu checkpoint

Checkpoint này chỉ cung cấp một blank microservice skeleton có thể chạy local: platform services, infrastructure, centralized configuration và database ownership. Chưa có API, entity, repository hay business logic.

## Service và port

| Service | Port | Trách nhiệm | Storage |
| --- | ---: | --- | --- |
| `config-server` | 8888 | Cấp central configuration | Native files trong classpath |
| `discovery-service` | 8761 | Eureka service registry | Không có database |
| `gateway-service` | 8222 | External entry point và discovery routing | Không có database |
| `event-catalog-service` | 8050 | Event concert, ticket type và inventory | PostgreSQL `event_catalog_db` |
| `booking-service` | 8070 | Booking và booking line | PostgreSQL `booking_db` |
| `payment-service` | 8060 | Payment | PostgreSQL `payment_db` |
| `customer-service` | 8090 | Customer profile | MongoDB `customer_db` |
| `notification-service` | 8040 | Notification và email delivery | MongoDB `notification_db` |

## Startup và communication flow

```text
Docker Compose
    |
    +--> PostgreSQL :5432
    +--> MongoDB :27017
    +--> Kafka :29092
    +--> Mailpit SMTP :1025
    |
    v
Config Server :8888
    |
    v
Discovery Service :8761
    |
    v
Gateway Service :8222
    |
    v
Business services
```

Config Client lấy properties từ Config Server khi khởi động và đăng ký vào Eureka. Gateway sử dụng Discovery Locator để định tuyến tới service đã đăng ký.

Client bên ngoài gọi Gateway. Sau này business services sẽ dùng Spring HTTP Service Clients cho synchronous request và Kafka cho asynchronous event. Không gọi qua Gateway cho internal service-to-service request.

## Local infrastructure

| Component | Host address | Consumer |
| --- | --- | --- |
| PostgreSQL | `localhost:5432` | Event Catalog, Booking, Payment |
| MongoDB | `localhost:27017` | Customer, Notification |
| Kafka | `localhost:29092` | Booking, Payment, Notification chạy local |
| Kafka internal listener | `kafka:9092` | Kafbat UI container |
| Mailpit SMTP | `localhost:1025` | Notification |
| Mailpit UI | http://localhost:8025 | Developer |
| Kafbat UI | http://localhost:8085 | Developer |

Database init scripts tạo user/database riêng cho từng business service. Việc cùng chạy trong một PostgreSQL hoặc MongoDB container chỉ là tối ưu local development; ownership dữ liệu vẫn tách biệt.

## Chạy và kiểm tra

```powershell
docker compose config
docker compose up -d
docker compose ps
Invoke-WebRequest http://localhost:8888/event-catalog-service/default
```

Khởi động application theo thứ tự: Config Server, Discovery Service, Gateway, sau đó từng business service. Mỗi service business phải xuất hiện `UP` tại Eureka dashboard.

Nếu sửa SQL/JavaScript dưới `infrastructure/*/init`, Docker chỉ chạy lại init script khi volume còn mới. Trong môi trường học, có thể reset infrastructure khi không cần giữ dữ liệu:

```powershell
docker compose down -v
docker compose up -d
```

Không chạy lệnh reset này nếu volume có dữ liệu cần giữ.
