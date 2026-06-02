# JP Morgan Chase - Advanced Software Engineering Virtual Experience

## Project Overview
This project simulates a real-world banking transaction processing system called **Midas Core** using Spring Boot, Apache Kafka, and H2 database.

## Technologies Used
- Java 17
- Spring Boot 3.2.5
- Apache Kafka
- Spring Data JPA
- H2 Database
- REST APIs

## What I Built
- ✅ Kafka listener for real-time transaction processing
- ✅ REST API for balance queries on port 33400
- ✅ External Incentive API integration
- ✅ Transaction validation and database storage

## How to Run

### Start Incentive API:
```bash
cd services && java -jar transaction-incentive-api.jar
```

### Run the application:
```bash
mvn spring-boot:run
```

### Test balance endpoint:
```bash
curl "http://localhost:33400/balance?userId=1"
```

## API Endpoint
- `GET /balance?userId={id}` - Returns user balance (0 if user not found)

## Connect with Me
- GitHub: [dipanshu-kashyap00](https://github.com/dipanshu-kashyap00)

**Completed:** June 2026 | **Platform:** Forage | **Company:** JP Morgan Chase & Co.
