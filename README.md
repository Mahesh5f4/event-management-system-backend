I fixed the README you shared  and folded in the missing pieces: a sharper top summary, an actual architecture flow, better scanability, and stronger recruiter-facing positioning.

````markdown
# High-Concurrency Event Ticket Booking System (Backend)

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker&logoColor=white)
![Load Testing](https://img.shields.io/badge/Load%20Tested-JMeter-D22128)

A production-oriented backend system for event publishing and ticket booking, designed for high concurrency, strong consistency, and performance under load.  
This project focuses on safe seat allocation, transactional integrity, caching, and scalable backend design rather than basic CRUD.

> **Engineering focus:** concurrency-safe booking with consistency-first design to prevent overbooking and race-condition failures.

---

## 🔥 Key Highlights

- Concurrency-safe booking system using **optimistic locking**
- Handles **200 concurrent users** in load testing
- Achieved **120–150 requests/sec** throughput
- Reduced latency from **150ms → 90ms** using Redis caching
- Prevents **double booking** with transactional consistency
- Built with **JWT security**, **MySQL**, **Redis**, and **Docker**

---

## Quick Navigation

- [Tech Stack](#tech-stack)
- [System Design Highlights](#system-design-highlights)
- [Architecture](#architecture)
- [Architecture / System Flow](#architecture--system-flow)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [Concurrency Handling](#concurrency-handling)
- [Caching Strategy](#caching-strategy)
- [Performance Metrics](#performance-metrics)
- [Load Testing](#load-testing)
- [Docker Setup](#docker-setup)
- [How to Run Locally](#how-to-run-locally)
- [Project Structure](#project-structure)
- [Future Improvements](#future-improvements)
- [ATS Keywords](#ats-keywords)

---

## Tech Stack

- **Language & Framework:** Java 17, Spring Boot 3
- **Database:** MySQL 8 (transactions, indexing, query optimization)
- **Caching:** Redis
- **Security:** Spring Security + JWT
- **Containerization:** Docker, Docker Compose
- **Load Testing:** Apache JMeter

---

## System Design Highlights

- Concurrency-safe booking model using **optimistic locking** (`version` field strategy)
- **Atomic transactions** for booking consistency
- Double-booking prevention under concurrent requests
- Redis-based caching for read-heavy APIs
- Clean layered architecture with clear separation of concerns
- Backend design suitable for horizontal scaling

---

## Architecture

The backend follows a layered architecture:

`controller -> service -> repository -> dto -> model -> config`

### What each layer does

- **Controller**
  - Handles HTTP requests and responses
  - Validates input and delegates work
- **Service**
  - Contains business logic
  - Handles booking rules, seat checks, and retry logic
- **Repository**
  - Talks to the database using Spring Data JPA
- **DTO**
  - Keeps API request/response models separate from entities
- **Model**
  - Contains JPA entities and database mappings
- **Config**
  - Handles security, caching, and app-level configuration

### Simple architecture flow

```text
Client
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
MySQL
  ↕
Redis (for cached read-heavy data)
````

---

## Architecture / System Flow

### Booking Request Flow

1. User sends a booking request with `eventId` and `ticketCount`.
2. Controller validates the request and checks JWT authentication.
3. Service layer starts a transaction and loads the event state.
4. Available seats are checked before booking.
5. Optimistic locking protects against concurrent seat updates.
6. If the update succeeds, booking is confirmed.
7. If a version conflict occurs, the transaction is safely retried.
8. If retries fail, the API returns a controlled conflict response.

### Why this works

* Prevents two users from booking the same seats at the same time
* Keeps seat counts accurate even under high contention
* Avoids silent corruption and negative inventory

---

## Database Design

### Core Tables

* `users`
* `events`
* `bookings`

### Relationships

* One `user` can have many `bookings`
* One `event` can have many `bookings`
* `bookings` connects users and events in a transactional flow

### Indexing Strategy

* `users.email` → unique index for login lookup
* `events.start_time` → event listing and sorting
* `bookings.event_id` → event booking queries
* `bookings.user_id` → user booking history
* Composite index recommendation: `(event_id, created_at)`

### Schema Notes

* `events.total_seats` and `events.available_seats` track seat inventory
* `events.version` supports optimistic locking
* `bookings.status` tracks booking lifecycle
* `created_at` and `updated_at` support auditing

---

## API Endpoints

> Base URL: `http://localhost:8080`

### Auth APIs

#### Register

* **Method:** `POST`
* **Endpoint:** `/auth/register`

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "StrongPass@123"
}
```

```json
{
  "message": "User registered successfully"
}
```

#### Login

* **Method:** `POST`
* **Endpoint:** `/auth/login`

```json
{
  "email": "john@example.com",
  "password": "StrongPass@123"
}
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Event APIs

#### Create Event

* **Method:** `POST`
* **Endpoint:** `/events`
* **Auth:** `Bearer <jwt>`

```json
{
  "title": "Spring Boot Summit",
  "description": "Backend engineering conference",
  "location": "Hyderabad",
  "startTime": "2026-05-10T10:00:00",
  "endTime": "2026-05-10T18:00:00",
  "price": 1499.0,
  "totalSeats": 500
}
```

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Spring Boot Summit",
    "location": "Hyderabad",
    "startTime": "2026-05-10T10:00:00",
    "endTime": "2026-05-10T18:00:00",
    "price": 1499.0,
    "availableSeats": 500,
    "totalSeats": 500
  },
  "message": "Event created successfully"
}
```

#### Publish Event

* **Method:** `PATCH`
* **Endpoint:** `/events/{id}`

```json
{
  "description": "Published event details"
}
```

```json
{
  "success": true,
  "data": {
    "id": 1
  },
  "message": "Event updated successfully"
}
```

#### Get Events

* **Method:** `GET`
* **Endpoint:** `/events?page=0&size=5`

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Spring Boot Summit"
      }
    ]
  },
  "message": "Events fetched successfully"
}
```

### Booking APIs

#### Book Tickets

* **Method:** `POST`
* **Endpoint:** `/bookings`
* **Auth:** `Bearer <jwt>`

```json
{
  "eventId": 1,
  "ticketCount": 2
}
```

```json
{
  "success": true,
  "data": {
    "bookingId": 101,
    "eventId": 1,
    "userId": 5,
    "ticketCount": 2,
    "status": "CONFIRMED"
  },
  "message": "Tickets booked successfully"
}
```

#### Get Bookings

* **Method:** `GET`
* **Endpoint:** `/bookings`
* **Auth:** `Bearer <jwt>`

```json
{
  "success": true,
  "data": [
    {
      "bookingId": 101,
      "eventId": 1,
      "ticketCount": 2,
      "status": "CONFIRMED"
    }
  ],
  "message": "Bookings fetched successfully"
}
```

---

## Concurrency Handling

This is the core reliability mechanism of the system.

### Optimistic Locking Strategy

* `events` table includes a `version` column
* Booking updates only succeed if the version matches the latest loaded value
* If another transaction updates the row first, the current transaction fails safely

### Why this prevents race conditions

* Concurrent requests cannot overwrite each other blindly
* Seat counts remain consistent under load
* No negative inventory or duplicate booking allocation

### Retry Logic

* On optimistic lock failure:

  1. Re-fetch latest event data
  2. Re-check available seats
  3. Retry transaction with a bounded retry count
* If retries fail, return `409 CONFLICT`

### Why optimistic locking was chosen

* Better throughput for burst traffic
* Avoids long-lived database locks
* Scales better than pessimistic locking for this use case

---

## Caching Strategy

### What is cached

* Event listing responses
* Event detail responses

### Cache layer

* Redis stores read-heavy data
* TTL-based expiration prevents stale long-lived cache entries

### Cache invalidation

* On create/update/delete:

  * Invalidate event detail cache
  * Invalidate relevant event list cache
* Database remains the source of truth for writes

### Performance benefit

* Reduced DB read pressure
* Lower latency on frequently accessed endpoints
* Better responsiveness under mixed traffic

---

## Performance Metrics

Load testing results from JMeter simulation:

* **Concurrent users:** 200
* **Throughput:** 120–150 requests/sec
* **Latency improvement:** 150ms → 90ms
* **Failure rate:** <2%

These numbers show stable performance under moderate concurrency with clear room for scaling.

---

## Load Testing

### JMeter setup

* Thread group: 200 users
* Ramp-up: 30 seconds
* Loop count: 10 or duration-based run
* Tested endpoints:

  * `POST /auth/login`
  * `GET /events`
  * `GET /events/{id}`
  * `POST /bookings`

### What to observe

* Cache hit rate for read APIs
* Booking contention under parallel requests
* Failure rate during bursts
* Throughput and response time stability

---

## Docker Setup

### Services

* MySQL
* Redis

### Start containers

```bash
docker compose up -d
```

### Stop containers

```bash
docker compose down
```

> Add a Spring Boot `Dockerfile` if you want the application itself containerized too.

---

## How to Run Locally

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd backend
```

### 2. Start dependencies

```bash
docker compose up -d
```

### 3. Configure application properties

Set database and Redis details in `application.properties` or environment variables.

### 4. Run the app

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 5. Test APIs

* Use Postman
* Send JWT in `Authorization: Bearer <token>`
* Test booking flow under concurrency

---

## Project Structure

```text
src/main/java/com/eventhub/backend/
├── auth/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   └── entity/
├── event/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   └── entity/
├── booking/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── dto/
│   └── entity/
├── common/
│   ├── config/
│   ├── exception/
│   └── response/
└── config/
```

---

## Screenshots / Demo

Add these to make the repo stronger:

* API screenshots
* JMeter report screenshots
* DB schema diagram
* Booking flow diagram

Example:

```md
![API Screenshot](docs/images/api.png)
![JMeter Report](docs/images/jmeter.png)
![DB Schema](docs/images/schema.png)
```

---

## Future Improvements

* Distributed locking using Redis/Redisson
* Kafka-based event-driven architecture
* Horizontal scaling with stateless app nodes
* Rate limiting and abuse protection
* Idempotency keys for safer booking retries

---

## ATS Keywords

`Java` `Spring Boot` `REST APIs` `MySQL` `Redis` `JWT Authentication` `Spring Security` `Optimistic Locking` `Transaction Management` `Concurrency Control` `Caching` `Docker` `JMeter` `Scalability` `System Design` `Backend Engineering`

---

## Conclusion

This project is built as a scalability-first ticket booking backend with strong consistency guarantees.
It reflects real backend engineering principles: concurrency control, transactional safety, caching, and performance optimization.

```


