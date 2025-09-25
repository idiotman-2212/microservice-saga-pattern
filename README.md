# Spring Boot Microservices Project

## Overview
This project demonstrates a microservices architecture using Spring Boot. It includes several independent services communicating via REST and Apache Kafka, with service discovery, API gateway, and centralized data management using PostgreSQL and Redis.

### Services
- **API Gateway**: Routes requests to appropriate services.
- **Service Discovery**: Registers and discovers services (Eureka/Consul).
- **Customer Service**: Manages customer data.
- **Product Service**: Handles product information.
- **Order Service**: Processes orders.
- **Payment Service**: Manages payments.
- **Inventory Service**: Tracks inventory.

### Supporting Infrastructure
- **PostgreSQL**: Database for all services (separate DBs per service).
- **Redis**: Caching and message brokering.
- **Kafka & Zookeeper**: Event-driven communication between services.

## Architecture Diagram

```
+-------------+        +-------------------+        +-------------------+
|             |        |                   |        |                   |
|  API Gateway+------->+ Service Discovery +<------>+   Microservices   |
|             |        |                   |        | (Order, Product,  |
+-------------+        +-------------------+        |  Customer, etc.)  |
       |                        |                   +-------------------+
       |                        |                           |
       v                        v                           v
+-------------------+   +-------------------+       +-------------------+
|    PostgreSQL     |   |      Redis        |       |      Kafka        |
+-------------------+   +-------------------+       +-------------------+
```

## Installation & Setup

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Maven

### Quick Start (Recommended)
1. **Clone the repository:**
   ```sh
   git clone https://github.com/idiotman-2212/microservice-saga-pattern.git
   cd spring-boot-microservices
   ```
2. **Start all services with Docker Compose:**
   ```sh
   docker-compose up --build
   ```
   This will start Zookeeper, Kafka, Redis, and PostgreSQL with all required databases.

3. **Build and run microservices:**
   - You can use the provided `start-services.ps1` script (on Windows) or run each service manually:
     ```sh
     cd C:\Users\Admin\Desktop\microservice\spring-boot-microservice
     mvn clean package
     java -jar target/spring-boot-microservice-1.0-SNAPSHOT.jar
     ```

### Access Points
- API Gateway: `http://localhost:8080`
- Service Discovery: `http://localhost:8761` (if using Eureka)
- PostgreSQL: `localhost:5432` (user: postgres, password: postgres)
- Redis: `localhost:6379`
- Kafka: `localhost:9092`

## Notes
- Databases are initialized using `init-multiple-databases.sh`.
- Kafka topics are auto-created for inter-service communication.
- Update `application.yml` in each service for custom configuration.

## License
MIT

