# Ticket Booking System

Dự án học Spring Boot Microservices và distributed systems, xây dựng hệ thống bán vé concert theo từng bước nhỏ.

## Kiến trúc hiện tại

```text
Client
  |
  v
Gateway Service :8222
  |
  +--> Event Catalog Service :8050 --> PostgreSQL / event_catalog_db
  +--> Customer Service      :8090 --> MongoDB / customer_db
  +--> Booking Service       :8070 --> PostgreSQL / booking_db
  +--> Payment Service       :8060 --> PostgreSQL / payment_db
  +--> Notification Service  :8040 --> MongoDB / notification_db

Mọi service client --> Config Server :8888
Mọi service client --> Discovery Service :8761
Booking, Payment, Notification --> Kafka
Notification --> Mailpit
```

Mỗi service sở hữu database riêng. Service này không được truy vấn database của service khác.

## Chạy local

Yêu cầu: Java 21, Docker Desktop và IntelliJ IDEA.

Khởi động infrastructure tại thư mục root:

```powershell
docker compose config
docker compose up -d
docker compose ps
```

Khởi động các Spring Boot application từ IntelliJ theo thứ tự:

```text
Config Server -> Discovery Service -> Gateway Service -> business services
```

Các endpoint quan sát local:

| Thành phần | Địa chỉ |
| --- | --- |
| Config Server | http://localhost:8888 |
| Eureka | http://localhost:8761 |
| Gateway | http://localhost:8222 |
| Kafka UI | http://localhost:8085 |
| Mailpit | http://localhost:8025 |

Java services chạy trên host nên dùng `localhost:5432`, `localhost:27017` và `localhost:29092`. Kafka UI chạy trong Docker nên dùng `kafka:9092`.

## Kiểm tra configuration

Sau khi Config Server chạy, kiểm tra central configuration:

```powershell
Invoke-WebRequest http://localhost:8888/event-catalog-service/default
```

Một Config Client sẽ fail-fast nếu Config Server chưa chạy. Đây là chủ ý để không che giấu lỗi cấu hình tập trung.

Xem thêm sơ đồ port, database ownership và cách kiểm tra tại [docs/architecture.md](docs/architecture.md).
