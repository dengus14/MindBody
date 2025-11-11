# Mind & Body Achievement based Gym Platform - Backend

A Spring Boot REST API for tracking workouts, managing achievements, and gamifying fitness goals. Provides JWT authentication, workout logging (PUSH/PULL/LEGS), badge system, daily challenges, and leaderboard functionality for the Mind & Body fitness tracking application.

## Tech Stack

- Java 17+ | Spring Boot 3.x | Spring Security (JWT) | Spring Data JPA | PostgreSQL 14+ | Maven | Docker

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker Desktop

## Local Setup

1. **Clone and navigate to project**
```bash
git clone https://github.com/yourusername/backend-mind-body.git
cd backend-mind-body
```

2. **Start PostgreSQL with Docker**
```bash
docker-compose up -d
```

3. **application.properties** SETUP THIS FILE
spring.application.name=MindBody

spring.datasource.url=jdbc:postgresql://localhost:5433/mindbodydb
spring.datasource.username=minduser
spring.datasource.password=mindpass

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.jwt.secret=f7a4b3c8e2d9f0164ab72c9159d83ef5c1a0e43d9b87f5e2d6a1c4b7f0e8a9d3


4. **Run the application**
```bash
mvn spring-boot:run
```


API will be available at `http://localhost:8080`

## Frontend Integration

Works with the React frontend. Ensure frontend [`api.ts`](front-mind-body/src/services/api.ts) points to:
```typescript
baseURL: 'http://localhost:8080/api'
```

## Configuration

Database runs on `localhost:5433` with credentials in `compose.yaml`:
- Database: `mindbodydb`
- Username: `minduser`
- Password: `mindpass`

JWT secret configured in `application.properties`

## Quick Commands

```bash
# Start database
docker-compose up -d

# Stop database
docker-compose down

# Run tests
mvn test
```
