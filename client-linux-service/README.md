# Client Linux Log Simulator

This project is a simple Java Maven application that simulates Linux sudo session logs and sends them as HTTP POST requests to a local server. It also includes a JUnit test that verifies the HTTP client behavior using an embedded HTTP server.

## Project Structure

- **ClientLinuxService**
    - Continuously generates fake Linux `sudo` session log messages.
    - Randomly selects a user and UID.
    - Sends logs as JSON to `http://localhost:8081/log` every second.

- **ClientLinuxServiceTest**
    - Starts a lightweight local HTTP server on port `8081`.
    - Verifies that the client sends the correct request body via HTTP POST.

## Technologies Used

- Java (Standard Library)
- Maven
- JUnit 5 (Jupiter)
- Built-in `HttpURLConnection`
- Built-in `HttpServer`

## Prerequisites

- Java 8 or higher
- Maven 3.x

## Build the Project

```bash
mvn clean package
```

## Run the Application

```bash
mvn exec:java -Dexec.mainClass="ClientLinuxService"
```

> Ensure a server is listening on `http://localhost:8081/log` before running the application.

## Run Tests

```bash
mvn test
```

## Sample Log Payload

```json
{
  "message": "<86> aiops9242 sudo: pam_unix(sudo:session): session opened for user root(uid=1)"
}
```

## Notes

- The application runs in an infinite loop and sends logs every second.
- Designed for local testing, demos, or log ingestion simulations.
- Ports and endpoint URLs are hardcoded for simplicity.