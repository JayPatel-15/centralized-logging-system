# Log Server (Spring Boot WebFlux)

This project is a reactive Spring Boot application that acts as a simple in-memory log ingestion and query server. Logs are accepted via HTTP, stored in memory, and queried using optional filters.

## Features

- Ingest logs via REST API
- Query logs with optional filters
    - `username`
    - `is_blacklisted`
- Expose basic metrics
- Fully reactive using Spring WebFlux
- Unit tests using `WebTestClient`

## Project Structure

- **LogServerApplication**
    - Spring Boot application entry point

- **LogController**
    - `POST /ingest` — ingest and store logs
    - `GET /logs` — query stored logs with filters
    - `GET /metrics` — basic metrics (total logs)

- **LogControllerTest**
    - Tests ingestion, filtering, and metrics endpoints

## Technologies Used

- Java
- Spring Boot 4.x
- Spring WebFlux
- Reactor
- Maven
- JUnit 5
- WebTestClient

## Prerequisites

- Java 17 or higher
- Maven 3.8+

## Build the Project

```bash
mvn clean package
```

## Run the Application

```bash
mvn spring-boot:run
```

The server will start on:

```
http://localhost:8080
```

## API Endpoints

### Ingest Log

```http
POST /ingest
Content-Type: application/json
```

**Body example:**
```json
{
  "username": "john",
  "is.blacklisted": false
}
```

### Query Logs

```http
GET /logs?username=root
GET /logs?is_blacklisted=true
GET /logs?username=root&is_blacklisted=false
```

Returns a stream of matching log entries.

### Metrics

```http
GET /metrics
```

**Response:**
```json
{
  "total_logs": 10
}
```

## Run Tests

```bash
mvn test
```

## Notes

- Logs are stored in memory using `CopyOnWriteArrayList`.
- This implementation is intended for demos and testing.
- Data is lost when the application restarts.
- Filtering is string-based and assumes JSON-like log format.

## License

Educational and testing purposes only.