# Lộ trình dựng Ticket Booking Microservices Skeleton

## Mục tiêu và kiến trúc chốt

Xây dựng hệ thống bán vé sự kiện/concert, chưa có chọn ghế ở v1. Giữ nguyên kiến trúc nền tảng của microservices-full-
code, chỉ đổi ngôn ngữ nghiệp vụ:

```
Repository mẫu          Ticket Booking
━━━━━━━━━━━━━━━━━━━━━━  ━━━━━━━━━━━━━━━━━━━━━━━
Product Service         Event Catalog Service
──────────────────────  ───────────────────────
Customer Service        Customer Service
──────────────────────  ───────────────────────
Order Service           Booking Service
──────────────────────  ───────────────────────
Payment Service         Payment Service
──────────────────────  ───────────────────────
Notification Service    Notification Service

```

Các platform service giữ nguyên:
```
Client
↓
API Gateway (Gateway-service)
↓
Business services ──→ Eureka Discovery Server (Discovery-service)
↓
Config Server
```

Sau này, Booking và Payment sẽ phát event đến Notification qua Kafka. PostgreSQL/MongoDB/Docker chỉ được thêm khi service
thật sự cần chúng.

Không có root pom.xml, không có shared database, và không tạo shared module ở giai đoạn đầu.

## Version và workspace baseline

- Java: 21.
- Spring Boot: 4.1.1
- Spring Cloud: 2025.1.3
- Build tool: Maven, mỗi service có Maven Wrapper riêng.
- Group ID: com.thucbn.ticketbooking.
- Mỗi service dùng package riêng dưới group ID trên.

Giữ cấu trúc root tối thiểu:

````
ticket-booking-system/
├── docs/
│   ├── .gitkeep
│   └── architecture.md
├── services/
│   └── .gitkeep
├── .gitignore
└── README.md
````

docs/architecture.md là tài liệu đầu tiên cần tạo trước khi code. Nó ghi sơ đồ service, port và quyết định domain; không
cần class diagram toàn hệ thống. Class diagram phải thuộc từng bounded context/service, không phải một sơ đồ entity dùng
chung toàn hệ thống.

## Thứ tự khởi tạo và kết nối service

### 1. Config Server — service đầu tiên

Tạo tại:

services/config-server/

Khởi tạo bằng Spring Initializr trong IntelliJ với:

- Maven, Java 21, JAR.
- Group: com.thucbn.ticketbooking.
- Artifact/Name: config-server.
- Dependencies: Spring Cloud Config Server, Spring Boot Actuator.
- Giữ Spring Boot 4.1.1 và Spring Cloud BOM 2025.1.3, rồi Maven reload.
- Tạo Maven Wrapper cùng service; không dùng Maven global.

Trách nhiệm: giữ cấu hình tập trung cho mọi service, chạy port 8888, dùng native profile và đọc các file cấu hình nằm
trong chính Config Server.

Cấu hình tập trung sẽ có dạng:
````
config-server/
└── src/main/resources/configurations/
├── application.yml
├── discovery-service.yml
├── gateway-service.yml
├── event-catalog-service.yml
├── customer-service.yml
├── booking-service.yml
├── payment-service.yml
└── notification-service.yml
````

Mỗi service chỉ giữ local bootstrap configuration tối thiểu: application name và địa chỉ Config Server. Port, database,
Kafka, Eureka và route nằm ở Config Server.

Kiểm chứng: Config Server khởi động độc lập tại http://localhost:8888 và trả được cấu hình cho một application name.

Git checkpoint:

feat(platform): add config server

### 2. Discovery Server

Tạo tại:

services/discovery-service/

Dependencies:

- Spring Cloud Config Client.
- Spring Cloud Netflix Eureka Server.
- Spring Boot Actuator.

Nó lấy cấu hình từ Config Server, chạy port 8761, không tự đăng ký vào Eureka. Đây là registry để các service tìm nhau
bằng service name thay vì hard-code host/port.

Kiểm chứng:

- Config Server chạy trước.
- Discovery Server lấy được config từ Config Server.
- Eureka dashboard mở tại http://localhost:8761.

Git checkpoint:

feat(platform): add discovery server

### 3. API Gateway

Tạo tại:

services/gateway-service/

Dependencies:

- Spring Cloud Config Client.
- Spring Cloud Gateway.
- Eureka Client.
- Spring Boot Actuator.

Gateway lấy config từ Config Server, đăng ký vào Eureka, chạy port 8222. Ban đầu chỉ cần cấu hình discovery locator; chỉ
thêm route cụ thể khi Event Catalog hoặc Customer Service đã tồn tại.

Gateway là entry point của client. Client không gọi trực tiếp Booking, Payment hay Event Catalog Service.

Kiểm chứng:

- Gateway xuất hiện trong Eureka.
- Gateway khởi động khi Config Server và Discovery Server đang chạy.
- Chưa cần Docker hoặc database.

Git checkpoint:

feat(platform): add api gateway

### 4. Event Catalog Service — business service đầu tiên

Tạo tại:

services/event-catalog-service/

Đây là business service đầu tiên, tương đương Product Service trong source mẫu.

Nó sở hữu dữ liệu:

- Event.
- Ticket type/category.
- Giá vé.
- Số lượng vé còn lại ở mức đơn giản.

Không có Seat Service và không có logic chọn/giữ ghế ở v1. Điều này giữ bài học tập trung vào microservices thay vì làm
phức tạp domain quá sớm.

Dependencies dự kiến:

- Spring Web.
- Validation.
- Spring Data JPA.
- PostgreSQL driver.
- Spring Cloud Config Client.
- Eureka Client.
- Actuator.
- Flyway.
- Lombok, chỉ khi bắt đầu viết entity/DTO.

Infrastructure Docker Compose hiện đã tồn tại ở root và gồm:

- PostgreSQL.
- MongoDB.
- Kafka ở KRaft mode và Kafbat UI.
- Mailpit.

Chưa thêm Zipkin. Mỗi dependency tiếp theo chỉ được thêm khi một service thật sự cần nó.

Kiểm chứng:

- PostgreSQL chạy qua Docker.
- Event Catalog Service lấy config từ Config Server.
- Service đăng ký vào Eureka.
- Gateway route chuyển request tới service bằng service ID.
- Service có database riêng; không dùng database chung với các service khác.

Git checkpoint:

feat(event-catalog): initialize service skeleton

### 5. Các business service tiếp theo

đã tạo skeleton, sẽ triển khai theo thứ tự:
1. customer-service: sở hữu customer profile; có thể dùng MongoDB để bám source mẫu.
2. booking-service: tương đương Order Service; sở hữu booking và booking line; gọi Event Catalog/Customer trực tiếp
   qua service discovery bằng HTTP Service Clients. Gateway chỉ là public entry point.

3. payment-service: sở hữu payment; không truy cập database của Booking Service.
4. notification-service: nhận Kafka event và gửi email. Kafka, MongoDB và Mailpit đã có trong Docker Compose.

Quy tắc cho mọi service mới:

- Có pom.xml, mvnw, mvnw.cmd, .mvn/ riêng.
- Có application name riêng.
- Import config từ Config Server.
- Đăng ký vào Eureka nếu là client-facing/business service.
- Có database ownership riêng nếu lưu dữ liệu.
- Gateway là public entry point.
- Không copy entity hoặc repository từ service này sang service khác.
- DTO gửi qua HTTP/Kafka không phải shared entity.

## IntelliJ và cách làm thủ công

- Mở ticket-booking-system làm một IntelliJ workspace duy nhất.
- Khi tạo một service mới, chọn thư mục con trong services/; không tạo ở root.
- Để IntelliJ import từng pom.xml độc lập vào Maven tool window.
- Project SDK và Maven importer JDK: Java 21.
- Maven home: Bundled Maven.
- Encoding: UTF-8.
- Git integration: bật.
- Terminal: PowerShell.
- Chưa bật annotation processing hoặc cài Lombok plugin cho đến khi Event Catalog Service dùng Lombok.
- Chưa cấu hình Docker integration; chỉ cấu hình sau khi Docker Compose đầu tiên xuất hiện.

Mỗi lần tạo xong một service:

1. Maven reload.
2. Chạy test skeleton.
3. Chạy application.
4. Kiểm tra port/config/Eureka theo đúng vai trò.
5. Commit một checkpoint nhỏ.
6. Ghi lại điều đã học và lỗi gặp phải vào docs/architecture.md hoặc một learning note.

## Tiêu chí hoàn thành platform skeleton

Trước khi bắt đầu logic nghiệp vụ Event Catalog, hệ thống phải đạt trạng thái:

Config Server      : localhost:8888
Discovery Server   : localhost:8761
API Gateway        : localhost:8222

Và luồng khởi động đúng:

````
Config Server
↓
Discovery Server
↓
API Gateway
↓
Business Service

Sau khi Event Catalog Service được thêm:

Client
↓
API Gateway :8222
↓
Event Catalog Service
↓
PostgreSQL
````

Các failure scenario cần hiểu trước khi sang Booking Service:

- Config Server không chạy: service có thể không lấy được cấu hình.
- Discovery Server không chạy: service không đăng ký/tìm được service khác.
- Gateway route sai service ID: request không đến được target service.
- Database không chạy: Event Catalog Service không thể khởi động hoặc lưu dữ liệu.
- Một service đang up không đồng nghĩa toàn hệ thống hoạt động.

## Assumptions

- Domain là vé sự kiện/concert, không có seat reservation ở v1.
- Java 21 được giữ theo môi trường hiện tại.
- Spring Boot 4.1.1 và Spring Cloud 2025.1.3 được dùng cho skeleton hiện tại.
- Docker Compose đã có PostgreSQL, MongoDB, Kafka KRaft, Kafbat UI và Mailpit.
- Config Server là Spring Boot application đầu tiên được khởi tạo.
