# Verification Service (Spring Boot, Java 21, H2, Docker)

This project implements a backend service that exposes three REST endpoints:

1) **FREE third-party API simulation**
2) **PREMIUM third-party API simulation**
3) **Backend API**

Additionally, it provides an endpoint for retrieving stored verifications.

---

## Prerequisites

To run this project locally you need:

- Java 21
- Maven 3.9+
- Docker Desktop (if running via Docker) / Docker Compose

Make sure Docker is running before executing any Docker commands.

---

## Running the Application Locally

From the project root

```
mvn spring-boot:run
```

## Running the Application via Docker

From the project root:

````
docker compose up --build
````

## H2 Database Console for stored data overview

Access via: http://localhost:4444/h2-console

````
JDBC URL: jdbc:h2:mem:verifications
User: sa
Password: 
````

## Quick Smoke Test Script

````
curl "http://localhost:4444/free-third-party?query=abc"
curl "http://localhost:4444/premium-third-party?query=abc"
curl "http://localhost:4444/backend-service?verificationId=11111111-1111-1111-1111-111111111111&query=abc"
curl "http://localhost:4444/verification/11111111-1111-1111-1111-111111111111"
````